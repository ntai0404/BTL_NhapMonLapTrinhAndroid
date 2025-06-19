package org.meicode.project2272.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.R;
import org.meicode.project2272.Respository.MainRespository;
import org.meicode.project2272.databinding.ActivityAddEditProductBinding;

import java.util.ArrayList;
import java.util.Arrays;
import android.util.Log;

public class AddEditProductActivity extends AppCompatActivity {

    private ActivityAddEditProductBinding binding;
    private MainRespository respository;
    private ItemsModel existingProduct = null;
    private String productKey = null;
    private static final String TAG = "AddEditProductActivity_Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        respository = new MainRespository();

        // 1. Khởi tạo Spinner
        setupSizeSpinner();

        // 2. Kiểm tra nếu là chế độ "Sửa" thì điền dữ liệu cũ
        if (getIntent().hasExtra("product")) {
            existingProduct = (ItemsModel) getIntent().getSerializableExtra("product");
            productKey = existingProduct.getKey();
            populateData();
        }

        // 3. Xử lý sự kiện khi nhấn nút "Lưu"
        binding.btnSave.setOnClickListener(v -> saveProduct());

        // 4. Xử lý sự kiện khi nhấn nút "Thêm" size
        binding.btnAddSize.setOnClickListener(v -> {
            String selectedSize = binding.spinnerSize.getSelectedItem().toString();
            if (!isSizeAlreadyAdded(selectedSize)) {
                addChipToGroup(selectedSize);
            } else {
                Toast.makeText(this, "Size đã được thêm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSizeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.common_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSize.setAdapter(adapter);
    }

    private boolean isSizeAlreadyAdded(String size) {
        for (int i = 0; i < binding.sizeChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.sizeChipGroup.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(size)) {
                return true;
            }
        }
        return false;
    }

    private void addChipToGroup(String size) {
        Chip chip = new Chip(this);
        chip.setText(size);
        chip.setCloseIconVisible(true);
        chip.setClickable(false);
        chip.setOnCloseIconClickListener(v -> binding.sizeChipGroup.removeView(chip));
        binding.sizeChipGroup.addView(chip);
    }

    private void populateData() {
        binding.edtTitle.setText(existingProduct.getTitle());
        binding.edtDescription.setText(existingProduct.getDescription());
        binding.edtPrice.setText(String.valueOf(existingProduct.getPrice()));
        binding.edtOldPrice.setText(String.valueOf(existingProduct.getOldPrice()));
        binding.edtOffPercent.setText(existingProduct.getOffPercent());

        if (existingProduct.getPicUrl() != null) {
            binding.edtPicUrl.setText(String.join(",", existingProduct.getPicUrl()));
        }

        // Sửa lỗi: Lấy danh sách size và thêm vào ChipGroup
        if (existingProduct.getSize() != null && !existingProduct.getSize().isEmpty()) {
            for (String size : existingProduct.getSize()) {
                addChipToGroup(size);
            }
        }
    }

    private void saveProduct() {
        // Lấy dữ liệu từ các trường nhập liệu
        String title = binding.edtTitle.getText().toString().trim();
        String description = binding.edtDescription.getText().toString().trim();
        String priceStr = binding.edtPrice.getText().toString().trim();
        String oldPriceStr = binding.edtOldPrice.getText().toString().trim();
        String offPercent = binding.edtOffPercent.getText().toString().trim();
        String picUrlStr = binding.edtPicUrl.getText().toString().trim();

        // Kiểm tra các trường bắt buộc
        if (title.isEmpty() || priceStr.isEmpty() || picUrlStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền các trường bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        // *** PHẦN QUAN TRỌNG NHẤT: LẤY SIZE TỪ CHIPGROUP ***
        // Vòng lặp này sẽ lấy tất cả các size bạn đã thêm vào giao diện
        ArrayList<String> sizes = new ArrayList<>();
        for (int i = 0; i < binding.sizeChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.sizeChipGroup.getChildAt(i);
            sizes.add(chip.getText().toString());
        }
        Log.d(TAG, "Danh sách size lấy từ ChipGroup: " + sizes.toString());

        // Tạo đối tượng sản phẩm mới
        ItemsModel product = new ItemsModel();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(Integer.parseInt(priceStr));
        product.setOldPrice(oldPriceStr.isEmpty() ? 0 : Integer.parseInt(oldPriceStr));
        product.setOffPercent(offPercent);
        product.setPicUrl(new ArrayList<>(Arrays.asList(picUrlStr.split(","))));

        // *** GÁN DANH SÁCH SIZE ĐÚNG VÀO SẢN PHẨM ***
        product.setSize(sizes);

        // Gán các giá trị mặc định khác nếu cần
        product.setColor(new ArrayList<>(Arrays.asList("#FF5733", "#33FF57")));
        Log.d(TAG, "Size trong đối tượng Product trước khi lưu: " + product.getSize().toString());

        // Lưu sản phẩm lên Firebase
        if (productKey != null) { // Chế độ sửa
            respository.updateProduct(productKey, product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        } else { // Chế độ thêm mới
            respository.addProduct(product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}