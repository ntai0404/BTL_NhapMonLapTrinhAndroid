package org.meicode.project2272.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.meicode.project2272.R;
import org.meicode.project2272.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {
    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        // Sửa id của view trong binding cho khớp với layout mới
        setContentView(binding.getRoot());

        // Đặt Fragment mặc định
        if (savedInstanceState == null) {
            replaceFragment(new AdminProductsFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_admin_products);
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_admin_products) {
                selectedFragment = new AdminProductsFragment();
            } else if (itemId == R.id.nav_admin_orders) {
                // selectedFragment = new AdminOrdersFragment();
            } else if (itemId == R.id.nav_admin_stats) {
                // selectedFragment = new AdminStatsFragment();
            } else if (itemId == R.id.nav_admin_profile) {
                // selectedFragment = new AdminProfileFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }

            return true; // Quan trọng: trả về true để mục được chọn
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}