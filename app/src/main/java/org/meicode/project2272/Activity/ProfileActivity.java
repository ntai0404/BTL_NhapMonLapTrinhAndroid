package org.meicode.project2272.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.meicode.project2272.R;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityProfileBinding;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private MainViewModel viewModel;
    private UserModel currentUser;
    private Uri newImageUri;
    private boolean isDataUpdated = false; // Cờ để kiểm tra xem dữ liệu đã được cập nhật chưa

    // Trình khởi chạy để nhận kết quả từ việc chọn ảnh
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    newImageUri = uri;
                    binding.profileImage.setImageURI(newImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        currentUser = (UserModel) getIntent().getSerializableExtra("user");

        if (currentUser != null) {
            loadUserInfo();
        }

        setListeners();
    }

    /**
     * Tải và hiển thị thông tin của người dùng lên các thành phần giao diện.
     */
    private void loadUserInfo() {
        if (currentUser == null) return;

        binding.usernameEdt.setText(currentUser.getUsername());
        binding.emailEdt.setText(currentUser.getEmail());
        binding.phoneEdt.setText(currentUser.getPhone());
        binding.addressEdt.setText(currentUser.getAddress());
        binding.passwordEdt.setText(""); // Luôn để trống ô mật khẩu sau khi load

        // Chỉ load ảnh từ URL khi không có ảnh mới đang chờ được chọn
        if (newImageUri == null) {
            Glide.with(this)
                    .load(currentUser.getImageUrl())
                    .error(R.drawable.profile)
                    .into(binding.profileImage);
        }
    }

    /**
     * Cài đặt các sự kiện onClick cho các nút trong màn hình.
     */
    private void setListeners() {
        UserModel user= this.currentUser;
        // SỬA LẠI NÚT BACK
        binding.backBtn.setOnClickListener(v -> {
            // Tạo một Intent để chứa kết quả trả về
            Intent resultIntent = new Intent();

            // isDataUpdated là cờ chúng ta đã nói ở câu trả lời trước.
            // Nó đảm bảo chúng ta chỉ trả về user mới nếu có sự thay đổi.
            if (isDataUpdated) {
                // Đặt user đã được cập nhật vào intent
                resultIntent.putExtra("updated_user", this.currentUser);
                setResult(Activity.RESULT_OK, resultIntent);
            } else {
                // Nếu không có gì thay đổi, chỉ cần báo là đã hủy
                setResult(Activity.RESULT_CANCELED, resultIntent);
            }
            // Quan trọng: Gọi finish() để đóng Activity hiện tại và quay về Activity trước đó.
            // Không dùng startActivity() ở đây nữa.
            finish();
        });

        binding.editImageBtn.setOnClickListener(v -> mGetContent.launch("image/*"));

        binding.saveBtn.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            updateProfile();
        });

        binding.logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    // Thêm hàm này vào ProfileActivity.java
    @Override
    public void onBackPressed() {
        // Giả lập việc bấm nút back trên giao diện
        binding.backBtn.performClick();
        super.onBackPressed();
    }


    /**
     * Thu thập dữ liệu từ các EditText và bắt đầu quá trình cập nhật.
     */
    private void updateProfile() {
        // Tạo một đối tượng user mới để chứa thông tin sẽ được cập nhật
        UserModel userToUpdate = new UserModel();
        userToUpdate.setUid(currentUser.getUid());
        userToUpdate.setRole(currentUser.getRole());
        userToUpdate.setPassword(currentUser.getPassword());

        userToUpdate.setUsername(binding.usernameEdt.getText().toString().trim());
        userToUpdate.setEmail(binding.emailEdt.getText().toString().trim());
        userToUpdate.setPhone(binding.phoneEdt.getText().toString().trim());
        userToUpdate.setAddress(binding.addressEdt.getText().toString().trim());

        String newPassword = binding.passwordEdt.getText().toString();
        if (!newPassword.isEmpty()) {
            userToUpdate.setPassword(newPassword);
        }

        if (newImageUri != null) {
            uploadImageAndUpdateUser(userToUpdate);
        } else {
            userToUpdate.setImageUrl(currentUser.getImageUrl());
            saveUserToFirebase(userToUpdate);
        }
    }

    private void uploadImageAndUpdateUser(UserModel userToUpdate) {
        MediaManager.get().upload(newImageUri).callback(new UploadCallback() {
            @Override
            public void onSuccess(String requestId, Map resultData) {
                userToUpdate.setImageUrl(resultData.get("secure_url").toString());
                newImageUri = null;
                saveUserToFirebase(userToUpdate);
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Lỗi tải ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onStart(String requestId) {}
            @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    /**
     * Lưu thông tin người dùng lên Firebase.
     * Sau khi gửi lệnh cập nhật, chúng ta tin tưởng rằng dữ liệu đã gửi là đúng
     * và cập nhật giao diện ngay lập tức mà không cần đọc lại từ server.
     */
    private void saveUserToFirebase(UserModel userToUpdate) {
        // 1. Gửi lệnh cập nhật lên server (và tin rằng nó sẽ thành công)
        viewModel.updateUser(userToUpdate);

        // 2. KHÔNG CẦN GỌI LẠI FIREBASE ĐỂ LẤY DỮ LIỆU.
        //    Điều này giúp loại bỏ hoàn toàn "race condition".

        // 3. Cập nhật ngay lập tức biến 'currentUser' của Activity
        //    bằng chính đối tượng mà chúng ta vừa gửi đi.
        this.currentUser = userToUpdate;
        this.isDataUpdated = true; // <<< QUAN TRỌNG: Đặt cờ để báo cho MainActivity biết có thay đổi.

        // 4. Tải lại thông tin lên giao diện từ 'currentUser' đã được làm mới.
        loadUserInfo();

        // 5. Ẩn progress bar và hiển thị thông báo thành công.
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
    }
}