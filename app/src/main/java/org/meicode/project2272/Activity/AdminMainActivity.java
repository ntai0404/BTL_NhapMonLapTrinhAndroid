package org.meicode.project2272.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Import các Fragment bạn đã tạo
import org.meicode.project2272.Fragment.AdminDashboardFragment;
import org.meicode.project2272.Fragment.AdminProductsFragment;
import org.meicode.project2272.Fragment.AdminProfileFragment;
import org.meicode.project2272.Fragment.AdminStatsFragment;
import org.meicode.project2272.R;
import org.meicode.project2272.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {
    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load fragment mặc định là Dashboard
        if (savedInstanceState == null) {
            replaceFragment(new AdminDashboardFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_admin_dashboard);
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_admin_dashboard) {
                selectedFragment = new AdminDashboardFragment();
            } else if (itemId == R.id.nav_admin_products) {
                selectedFragment = new AdminProductsFragment();
            } else if (itemId == R.id.nav_admin_orders) {
                // selectedFragment = new AdminOrdersFragment(); // Fragment quản lý đơn hàng
            } else if (itemId == R.id.nav_admin_profile) {
                selectedFragment = new AdminProfileFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}