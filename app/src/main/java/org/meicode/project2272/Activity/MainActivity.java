package org.meicode.project2272.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // SỬA: Import đúng
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.meicode.project2272.Adapter.PopularAdapter;
import org.meicode.project2272.Adapter.SliderAdapter;
import org.meicode.project2272.Model.BannerModel;
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
    private PopularAdapter popularAdapter; // SỬA 1: Khai báo Adapter ở cấp lớp để có thể tái sử dụng

    // Khai báo một ActivityResultLauncher để nhận kết quả trả về từ ProfileActivity
    private final ActivityResultLauncher<Intent> profileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    UserModel updatedUser = (UserModel) result.getData().getSerializableExtra("updated_user");
                    if (updatedUser != null) {
                        this.currentUser = updatedUser;
                        initUserProfile();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // SỬA 2: Khởi tạo ViewModel đúng cách, theo vòng đời của Activity
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        currentUser = (UserModel) getIntent().getSerializableExtra("user");

        initUserProfile();
        initBanner();
        initPopular();
        initActionListeners();
        setupSearchFunctionality();
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        refreshCurrentUser();
    }

    private void refreshCurrentUser() {
        if (currentUser == null || currentUser.getUid() == null) {
            return;
        }
        viewModel.fetchUserOnce(currentUser.getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        UserModel updatedUser = childSnapshot.getValue(UserModel.class);
                        if (updatedUser != null) {
                            MainActivity.this.currentUser = updatedUser;
                            initUserProfile();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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

        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            if (itemId == R.id.cart) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            } else if (itemId == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user", currentUser);
                profileLauncher.launch(intent);
            }
        });
    }

    //  Tối ưu hóa initPopular
    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        // Khởi tạo Adapter một lần duy nhất
        popularAdapter = new PopularAdapter(new ArrayList<>(), currentUser);
        binding.popularView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        binding.popularView.setAdapter(popularAdapter);
        binding.popularView.setNestedScrollingEnabled(true);

        // Quan sát và tải danh sách sản phẩm ban đầu
        viewModel.loadPopular().observe(this, itemsModels -> {
            if (itemsModels != null) {
                // Chỉ cập nhật dữ liệu cho adapter đã có
                popularAdapter.setItems(itemsModels);
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

    private void initUserProfile() {
        if (currentUser != null) {
            binding.textView5.setText(currentUser.getUsername());
            Glide.with(MainActivity.this)
                    .load(currentUser.getImageUrl())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(binding.imageView2);
        }
    }

    // SỬA 4: Sửa toàn bộ logic tìm kiếm
    private void setupSearchFunctionality() {
        // Lắng nghe trạng thái tìm kiếm từ ViewModel để ẩn/hiện banner
        viewModel.isSearching().observe(this, searching -> {
            int visibility = searching ? View.GONE : View.VISIBLE;
            binding.viewPagerSlider.setVisibility(visibility);
            binding.progressBarSlider.setVisibility(visibility);
        });

        // Cài đặt sự kiện cho nút "Tìm"
        binding.searchBtn.setOnClickListener(v -> {
            // SỬA 1: Lấy từ khóa từ ô nhập liệu "searchEdt", không phải "searchBtn"
            String keyword = binding.searchEdt.getText().toString();

            // Gọi ViewModel để thực hiện tìm kiếm và lắng nghe kết quả trả về
            viewModel.searchProducts(keyword).observe(MainActivity.this, items -> {
                if (items != null) {
                    // SỬA 2 & 3: Sử dụng đúng tên biến "popularAdapter" và gọi phương thức trên đối tượng, không phải trên lớp
                    if (popularAdapter != null) {
                        popularAdapter.setItems(items);
                    }
                }
            });
        });
    }
}
