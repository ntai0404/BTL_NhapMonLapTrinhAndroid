package org.meicode.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.meicode.project2272.Adapter.CategoryAdapter;
import org.meicode.project2272.Adapter.PopularAdapter;
import org.meicode.project2272.Adapter.SliderAdapter;
import org.meicode.project2272.Domain.BannerModel;
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
        binding.bottomNavigation.setItemSelected(R.id.home, true); // reset selection về Home
    }


    private void bottomNavigation() {
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CartActivity.class))
        );

        // Dùng if-else thay vì switch (vì lỗi Constant expression)
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            if (itemId == R.id.home || itemId == R.id.favorites) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            } else if (itemId == R.id.cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            } else if (itemId == R.id.profile) {
                // Chưa có xử lý
            }

        });
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observeForever(itemsModels -> {
            if (itemsModels != null) {
                binding.popularView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                binding.popularView.setAdapter(new PopularAdapter(itemsModels));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular();
    }

    private void initBanner() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observeForever(bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                banners(bannerModels);
            }
            binding.progressBarSlider.setVisibility(View.GONE);
        });
        viewModel.loadBanner();
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
        viewModel.loadCategory().observeForever(categoryModels -> {
            binding.categoryView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }
}
