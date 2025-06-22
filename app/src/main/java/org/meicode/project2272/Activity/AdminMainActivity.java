package org.meicode.project2272.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Import các Fragment bạn đã tạo
import org.meicode.project2272.Fragment.AdminDashboardFragment;
import org.meicode.project2272.Fragment.AdminProductsFragment;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.R;
import org.meicode.project2272.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {
    private ActivityAdminMainBinding binding;
    private UserModel currentUser;

    // --- BẮT ĐẦU PHẦN SỬA LỖI ---
    // 1. Khai báo ActivityResultLauncher để nhận kết quả trả về từ ProfileActivity
    private final ActivityResultLauncher<Intent> profileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    UserModel updatedUser = (UserModel) result.getData().getSerializableExtra("updated_user");
                    if (updatedUser != null) {
                        // Cập nhật lại thông tin currentUser trong AdminMainActivity
                        this.currentUser = updatedUser;
                        Toast.makeText(this, "Thông tin cá nhân đã được cập nhật.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    // --- KẾT THÚC PHẦN SỬA LỖI ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy UserModel để gọi chức năng Profile
        currentUser = (UserModel) getIntent().getSerializableExtra("user");

        // Load fragment mặc định là Dashboard
        if (savedInstanceState == null) {
            replaceFragment(new AdminDashboardFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_admin_dashboard);
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_admin_dashboard) {
                replaceFragment(new AdminDashboardFragment());
                return true;
            } else if (itemId == R.id.nav_admin_products) {
                replaceFragment(new AdminProductsFragment());
                return true;
            } else if (itemId == R.id.nav_admin_orders) {
                // Mở màn hình quản lý đơn hàng
                Intent intent = new Intent(AdminMainActivity.this, ShowOrderActivity.class);
                intent.putExtra("isAdmin", true);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                // --- BẮT ĐẦU PHẦN SỬA LỖI ---
                // 2. Sử dụng launcher đã khai báo để mở ProfileActivity
                Intent intent = new Intent(AdminMainActivity.this, ProfileActivity.class);
                intent.putExtra("user", currentUser);
                profileLauncher.launch(intent);
                // --- KẾT THÚC PHẦN SỬA LỖI ---
                return true;
            }

            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}