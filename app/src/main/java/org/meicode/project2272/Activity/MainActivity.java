package org.meicode.project2272.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import org.meicode.project2272.Adapter.CategoryAdapter;
import org.meicode.project2272.Adapter.PopularAdapter;
import org.meicode.project2272.Adapter.ShowOrderAdapter;
import org.meicode.project2272.Adapter.SliderAdapter;
import org.meicode.project2272.Helper.TinyDB;
import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.R;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new MainViewModel();

        initCategory();
        initBanner();
        initPopular();
        bottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setItemSelected(R.id.home, true);
    }

    private void bottomNavigation() {
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CartActivity.class))
        );

        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            if (itemId == R.id.home) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
           //Code của Nhung
            else if (itemId == R.id.favorites){
                SharedPreferences sharedPreferences = getSharedPreferences("AppSession", Context.MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");
                if (userId.isEmpty()) {
                    Toast.makeText(this, "Không tìm thấy thông tin đăng nhập!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ShowOrderActivity.class);
                    intent.putExtra("isAdmin", false);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
                // --- KẾT THÚC PHẦN THAY ĐỔI ---
            }
            else if (itemId == R.id.cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            } else if (itemId == R.id.profile) {
                // Chưa xử lý
            }
        });
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        viewModel.loadPopular().observe(this, new Observer<ArrayList<ItemsModel>>() {
            @Override
            public void onChanged(ArrayList<ItemsModel> itemsModels) {
                if (itemsModels != null) {
                    binding.popularView.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false)
                    );
                    binding.popularView.setAdapter(new PopularAdapter(itemsModels));
                    binding.popularView.setNestedScrollingEnabled(true);
                }
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }

    private void initBanner() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);

        viewModel.loadBanner().observe(this, new Observer<ArrayList<BannerModel>>() {
            @Override
            public void onChanged(ArrayList<BannerModel> bannerModels) {
                if (bannerModels != null && !bannerModels.isEmpty()) {
                    banners(bannerModels);
                }
                binding.progressBarSlider.setVisibility(View.GONE);
            }
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

        viewModel.loadCategory().observe(this, new Observer<ArrayList<CategoryModel>>() {
            @Override
            public void onChanged(ArrayList<CategoryModel> categoryModels) {
                binding.categoryView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
                binding.categoryView.setNestedScrollingEnabled(true);
                binding.progressBarCategory.setVisibility(View.GONE);
            }
        });
    }
}

