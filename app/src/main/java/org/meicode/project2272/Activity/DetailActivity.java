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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Nhận UserModel từ màn hình trước
        currentUser = (UserModel) getIntent().getSerializableExtra("user");
        getBundle();
        initPicList();
        initSize();
        initColor();
    }

    private void initColor() {
        if (object.getColor() != null) {
            binding.recyclerColor.setAdapter(new ColorAdapter(object.getColor()));
            binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void initSize() {
        if (object.getSize() != null) {
            binding.recyclerSize.setAdapter(new SizeAdapter(object.getSize()));
            binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }

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

    // Tệp: DetailActivity.java

    private void getBundle() {
        object = (ItemsModel) getIntent().getSerializableExtra("object");
        if (object == null ) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tạo đối tượng định dạng tiền tệ
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        binding.titleTxt.setText(object.getTitle());

        // Định dạng giá mới và giá cũ
        binding.priceTxt.setText(currencyFormatter.format(object.getPrice()));
        binding.oldPriceTxt.setText(currencyFormatter.format(object.getOldPrice()));

        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.decriptionTxt.setText(object.getDescription());

        binding.addToCartBtn.setOnClickListener(v -> {
            if (currentUser != null) {
                // Sử dụng uid của người dùng thật
                viewModel.manageCartItem(currentUser.getUid(), object.getId(), 1);
                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please log in to use this feature", Toast.LENGTH_SHORT).show();
            }
        });

        binding.backBtn.setOnClickListener(v -> finish());
    }
}