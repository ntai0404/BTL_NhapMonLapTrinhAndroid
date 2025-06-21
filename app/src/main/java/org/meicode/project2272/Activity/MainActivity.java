package org.meicode.project2272.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.meicode.project2272.Adapter.CategoryAdapter;
import org.meicode.project2272.Adapter.PopularAdapter;
import org.meicode.project2272.Adapter.SliderAdapter;
import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.R;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private UserModel currentUser;

    // <<< THÊM VÀO >>>
    // Khai báo một ActivityResultLauncher để nhận kết quả trả về từ ProfileActivity
    private final ActivityResultLauncher<Intent> profileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra xem kết quả có thành công không và có dữ liệu trả về không
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Lấy UserModel đã được cập nhật từ Intent trả về
                    UserModel updatedUser = (UserModel) result.getData().getSerializableExtra("updated_user");
                    if (updatedUser != null) {
                        // Cập nhật lại biến currentUser của MainActivity.
                        // Đây là bước quan trọng nhất để đảm bảo dữ liệu luôn mới.
                        this.currentUser = updatedUser;
                    }
                }
            }
    );
    // <<< KẾT THÚC PHẦN THÊM VÀO >>>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận UserModel từ SplashActivity
        currentUser = (UserModel) getIntent().getSerializableExtra("user");
        viewModel = new MainViewModel();

        initCategory();
        initBanner();
        initPopular();
        initActionListeners();
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        // <<< THÊM VÀO >>>
        // Gọi hàm làm mới dữ liệu người dùng mỗi khi Activity quay trở lại foreground
        refreshCurrentUser();
    }

    // <<< THÊM HÀM MỚI >>>
    /**
     * Hàm này gọi xuống ViewModel để lấy thông tin người dùng mới nhất từ Firebase.
     * Đây là cách đảm bảo dữ liệu luôn đúng, bất kể Activity có bị tạo lại hay không.
     */
    private void refreshCurrentUser() {
        if (currentUser == null || currentUser.getUid() == null) {
            return; // Không có user để làm mới
        }

        viewModel.fetchUserOnce(currentUser.getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        UserModel updatedUser = childSnapshot.getValue(UserModel.class);
                        if (updatedUser != null) {
                            // Cập nhật biến currentUser của MainActivity
                            MainActivity.this.currentUser = updatedUser;
                            // Log.d("REFRESH_DEBUG", "User data refreshed from Firebase: " + updatedUser.getUsername());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
                // Log.e("REFRESH_DEBUG", "Failed to refresh user data", error.toException());
            }
        });
    }

    private void initActionListeners() {
        binding.cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        binding.imageView3.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        });

        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            if (itemId == R.id.home || itemId == R.id.favorites) {
                // do nothing
            } else if (itemId == R.id.cart) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else if (itemId == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user", currentUser);

                // <<< THAY ĐỔI >>>
                // Khởi chạy ProfileActivity bằng launcher để có thể nhận kết quả trả về.
                // Không dùng startActivity(intent) nữa.
                profileLauncher.launch(intent);
            }
        });
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observe(this, itemsModels -> {
            if (itemsModels != null) {
                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false)
                );
                binding.popularView.setAdapter(new PopularAdapter(itemsModels, currentUser));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
    }

    private void initBanner() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);

        viewModel.loadBanner().observe(this, bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                banners(bannerModels);
            }
            binding.progressBarSlider.setVisibility(View.GONE);
        });
    }

    private void banners(ArrayList<BannerModel> bannerModels) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModels, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(transformer);
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        viewModel.loadCategory().observe(this, categoryModels -> {
            binding.categoryView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }
}
