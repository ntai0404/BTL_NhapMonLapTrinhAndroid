package org.meicode.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Để kiểm tra chuỗi rỗng
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Nếu tvLoginLink là TextView
import android.widget.Toast;
import android.util.Log; // Để ghi log debug

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query; // Cần cho truy vấn
import com.google.firebase.database.DataSnapshot; // Cần cho kết quả truy vấn
import com.google.firebase.database.DatabaseError; // Cần cho lỗi truy vấn
import com.google.firebase.database.ValueEventListener; // Cần cho lắng nghe sự kiện

import org.meicode.project2272.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etPhoneNumber, etAddress;
    private Button btnRegister;
    private TextView tvLoginLink;
    private DatabaseReference databaseRef;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Khởi tạo Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference();
        // Ánh xạ các View từ layout
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        // Thiết lập OnApplyWindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnRegister.setOnClickListener(v -> registerUser());
        if (tvLoginLink != null) {
            tvLoginLink.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Tên đăng nhập không được để trống!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Mật khẩu không được để trống!");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email không được để trống!");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ!");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Số điện thoại không được để trống!");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Địa chỉ không được để trống!");
            return;
        }
        checkUserExists(username, email, password, phoneNumber, address);
    }
    private void checkUserExists(String username, String email, String password, String phoneNumber, String address) {
        // Kiểm tra username
        Query usernameQuery = databaseRef.child("User").orderByChild("username").equalTo(username);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.", Toast.LENGTH_LONG).show();
                    return; // Dừng nếu username đã tồn tại
                }
                // Nếu username không tồn tại, kiểm tra email
                Query emailQuery = databaseRef.child("User").orderByChild("email").equalTo(email);
                emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(RegisterActivity.this, "Email đã được sử dụng! Vui lòng sử dụng email khác.", Toast.LENGTH_LONG).show();
                            return; // Dừng nếu email đã tồn tại
                        }
                        saveNewUser(username, email, password, phoneNumber, address);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error during email check: " + error.getMessage());
                        Toast.makeText(RegisterActivity.this, "Lỗi kiểm tra email. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error during username check: " + error.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi kiểm tra tên đăng nhập. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveNewUser(String username, String email, String password, String phoneNumber, String address) {
        String uid = UUID.randomUUID().toString(); // Sử dụng UUID để tạo ID duy nhất
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid); // Lưu UID vào trong tài liệu người dùng (theo cấu trúc bạn muốn)
        user.put("username", username);
        user.put("password", password); // CẢNH BÁO: MẬT KHẨU LƯU DƯỚI DẠNG PLAIN TEXT LÀ KHÔNG AN TOÀN!
        user.put("email", email);
        user.put("phone", phoneNumber);
        user.put("address", address);
        user.put("role", "user");
        databaseRef.child("User").child(uid).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile added to Realtime Database with UID: " + uid);
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "Error adding user profile to Realtime Database for UID: " + uid, task.getException());
                            Toast.makeText(RegisterActivity.this, "Lỗi khi lưu thông tin người dùng: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
