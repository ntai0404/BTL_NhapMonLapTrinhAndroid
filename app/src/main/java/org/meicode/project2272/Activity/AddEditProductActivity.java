package org.meicode.project2272.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.chip.Chip;

import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.R;
import org.meicode.project2272.Respository.MainRespository;
import org.meicode.project2272.databinding.ActivityAddEditProductBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AddEditProductActivity extends AppCompatActivity {

    private ActivityAddEditProductBinding binding;
    private MainRespository respository;
    private ItemsModel existingProduct = null;
    private String productKey = null;
    private Uri imageUri;
    private String uploadedImageUrl = null; // Biến lưu URL ảnh đã tải lên
    private ProgressDialog progressDialog;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    // Hiển thị ảnh đã chọn và bắt đầu tải lên
                    Glide.with(this).load(imageUri).into(binding.productImageView);
                    uploadToCloudinary();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        respository = new MainRespository();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải ảnh lên...");

        setupSizeSpinner();

        if (getIntent().hasExtra("product")) {
            existingProduct = (ItemsModel) getIntent().getSerializableExtra("product");
            productKey = existingProduct.getKey();
            populateData();
        }

        // Mở thư viện ảnh khi nhấn nút
        binding.btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        binding.btnSave.setOnClickListener(v -> saveProduct());
        binding.btnAddSize.setOnClickListener(v -> {
            String selectedSize = binding.spinnerSize.getSelectedItem().toString();
            if (!isSizeAlreadyAdded(selectedSize)) {
                addChipToGroup(selectedSize);
            } else {
                Toast.makeText(this, "Size đã được thêm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToCloudinary() {
        if (imageUri == null) return;
        progressDialog.show();

        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d("CLOUDINARY_UPLOAD", "Bắt đầu tải lên...");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {}

            @Override
            public void onSuccess(String requestId, Map resultData) {
                progressDialog.dismiss();
                uploadedImageUrl = (String) resultData.get("secure_url");
                Toast.makeText(AddEditProductActivity.this, "Tải ảnh thành công!", Toast.LENGTH_SHORT).show();
                Log.d("CLOUDINARY_UPLOAD", "URL: " + uploadedImageUrl);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                progressDialog.dismiss();
                Toast.makeText(AddEditProductActivity.this, "Tải ảnh thất bại: " + error.getDescription(), Toast.LENGTH_LONG).show();
                Log.e("CLOUDINARY_UPLOAD", "Lỗi: " + error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
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

        if (existingProduct.getPicUrl() != null && !existingProduct.getPicUrl().isEmpty()) {
            Glide.with(this).load(existingProduct.getPicUrl().get(0)).into(binding.productImageView);
        }

        // Sửa lỗi: Lấy danh sách size và thêm vào ChipGroup
        if (existingProduct.getSize() != null && !existingProduct.getSize().isEmpty()) {
            for (String size : existingProduct.getSize()) {
                addChipToGroup(size);
            }
        }
    }

    private void saveProduct() {
        if (uploadedImageUrl == null && existingProduct == null) {
            Toast.makeText(this, "Vui lòng chọn và đợi tải ảnh lên hoàn tất", Toast.LENGTH_SHORT).show();
            return;
        }
        // Lấy dữ liệu từ các trường nhập liệu
        String title = binding.edtTitle.getText().toString().trim();
        String description = binding.edtDescription.getText().toString().trim();
        String priceStr = binding.edtPrice.getText().toString().trim();
        String oldPriceStr = binding.edtOldPrice.getText().toString().trim();
        String offPercent = binding.edtOffPercent.getText().toString().trim();

        ArrayList<String> sizes = new ArrayList<>();
        for (int i = 0; i < binding.sizeChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.sizeChipGroup.getChildAt(i);
            sizes.add(chip.getText().toString());
        }


        // Tạo đối tượng sản phẩm mới
        ItemsModel product = new ItemsModel();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(Integer.parseInt(priceStr));
        product.setOldPrice(oldPriceStr.isEmpty() ? 0 : Integer.parseInt(oldPriceStr));
        product.setOffPercent(offPercent);
        product.setSize(sizes);
        product.setColor(new ArrayList<>(Arrays.asList("#FF5733", "#33FF57")));


        ArrayList<String> finalPicUrls = new ArrayList<>();
        if (uploadedImageUrl != null) {
            finalPicUrls.add(uploadedImageUrl);
        } else if (existingProduct != null && existingProduct.getPicUrl() != null) {
            finalPicUrls.addAll(existingProduct.getPicUrl());
        }
        product.setPicUrl(finalPicUrls);


        // Lưu sản phẩm lên Firebase (logic không đổi)
        if (productKey != null) {
            // TRƯỜNG HỢP 1: SỬA SẢN PHẨM ĐÃ CÓ
            respository.updateProduct(productKey, product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Nếu thành công, thông báo và đóng màn hình hiện tại
                    Toast.makeText(AddEditProductActivity.this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Nếu thất bại, chỉ thông báo lỗi
                    Toast.makeText(AddEditProductActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // TRƯỜNG HỢP 2: THÊM SẢN PHẨM MỚI
            respository.addProduct(product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Nếu thành công, thông báo và đóng màn hình hiện tại
                    Toast.makeText(AddEditProductActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Nếu thất bại, chỉ thông báo lỗi
                    Toast.makeText(AddEditProductActivity.this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}