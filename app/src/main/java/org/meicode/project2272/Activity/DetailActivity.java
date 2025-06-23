// File: org/meicode/project2272/Activity/DetailActivity.java
package org.meicode.project2272.Activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import org.meicode.project2272.Adapter.ColorAdapter;
import org.meicode.project2272.Adapter.PicListAdapter;
import org.meicode.project2272.Adapter.SizeAdapter;
import org.meicode.project2272.Model.CartModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityDetailBinding;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private ItemsModel object;
    private MainViewModel viewModel;
    private UserModel currentUser;

    // Biến để lưu trữ lựa chọn của người dùng
    private int quantity = 1;
    private String selectedSize = null;
    private String selectedColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        currentUser = (UserModel) getIntent().getSerializableExtra("user");

        getBundleAndUI();
        initPicList();
        initSize();
        initColor();
//        setupQuantityButtons();
    }

    private void getBundleAndUI() {
        object = (ItemsModel) getIntent().getSerializableExtra("object");
        if (object == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText(currencyFormatter.format(object.getPrice()));
        binding.oldPriceTxt.setText(currencyFormatter.format(object.getOldPrice()));
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.decriptionTxt.setText(object.getDescription());

        binding.backBtn.setOnClickListener(v -> finish());

        // Logic cho nút "Thêm vào giỏ hàng"
        binding.addToCartBtn.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSize == null) {
                Toast.makeText(this, "Vui lòng chọn size", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedColor == null) {
                Toast.makeText(this, "Vui lòng chọn màu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng CartModel mới từ các lựa chọn
            CartModel newItem = new CartModel();
            newItem.setItemId(object.getId());
            newItem.setQuantity(quantity);
            newItem.setSize(selectedSize);
            newItem.setColor(selectedColor);

            // Gọi hàm ViewModel (giả sử đã có) để thêm vào giỏ hàng
            // viewModel.addItemToCart(currentUser.getUid(), newItem);
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    private void initColor() {
        if (object.getColor() != null && !object.getColor().isEmpty()) {
            // Tạo listener và truyền vào Adapter
            // Khi người dùng chọn màu, biến `selectedColor` sẽ được cập nhật
            ColorAdapter.OnColorSelectedListener listener = color -> this.selectedColor = color;

            binding.recyclerColor.setAdapter(new ColorAdapter(object.getColor(), listener));
            binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void initSize() {
        if (object.getSize() != null && !object.getSize().isEmpty()) {
            // Tạo listener và truyền vào Adapter
            // Khi người dùng chọn size, biến `selectedSize` sẽ được cập nhật
            SizeAdapter.OnSizeSelectedListener listener = size -> this.selectedSize = size;

            binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize(), listener));
            binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }

//    private void setupQuantityButtons() {
//        // Giả sử layout của bạn có các view với id: quantityTxt, plusBtn, minusBtn
//        binding.quantityTxt.setText(String.valueOf(quantity));
//
//        binding.plusBtn.setOnClickListener(v -> {
//            quantity++;
//            binding.quantityTxt.setText(String.valueOf(quantity));
//        });
//
//        binding.minusBtn.setOnClickListener(v -> {
//            if (quantity > 1) {
//                quantity--;
//                binding.quantityTxt.setText(String.valueOf(quantity));
//            }
//        });
//    }

    private void initPicList() {
        if (object.getPicUrl() != null && !object.getPicUrl().isEmpty()) {
            ArrayList<String> picList = new ArrayList<>(object.getPicUrl());
            Glide.with(this)
                    .load(picList.get(0))
                    .into(binding.pic);
            binding.picList.setAdapter(new PicListAdapter(picList, binding.pic));
            binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }
}