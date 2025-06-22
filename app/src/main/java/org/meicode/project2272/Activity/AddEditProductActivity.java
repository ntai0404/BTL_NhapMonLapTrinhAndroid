package org.meicode.project2272.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.meicode.project2272.R;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityAddEditProductBinding;
import java.util.ArrayList;
import java.util.Map;

public class AddEditProductActivity extends AppCompatActivity {

    private ActivityAddEditProductBinding binding;
    private MainViewModel viewModel;
    private ItemsModel existingProduct;
    private boolean isEditMode = false;

    // Danh sách để lưu các lựa chọn
    private ArrayList<String> selectedSizes = new ArrayList<>();
    private ArrayList<String> selectedColors = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        initImagePicker();
        setupSpinners();

        if (getIntent().hasExtra("product_to_edit")) {
            existingProduct = (ItemsModel) getIntent().getSerializableExtra("product_to_edit");
            isEditMode = true;
            setupEditMode();
        } else {
            isEditMode = false;
            binding.toolbarTitle.setText("Thêm Sản phẩm Mới");
        }

        setListeners();
    }

    private void setupSpinners() {
        // Setup Size Spinner
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.common_sizes, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sizeSpinner.setAdapter(sizeAdapter);

        // Setup Color Spinner
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(
                this, R.array.common_colors, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.colorSpinner.setAdapter(colorAdapter);
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.saveBtn.setOnClickListener(v -> saveProduct());
        binding.chooseImageBtn.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Listener cho nút thêm Size và Color
        binding.addSizeBtn.setOnClickListener(v -> {
            String size = binding.sizeSpinner.getSelectedItem().toString();
            if (!size.isEmpty() && !selectedSizes.contains(size)) {
                selectedSizes.add(size);
                addChipToGroup(size, binding.sizeChipGroup, selectedSizes);
            }
        });

        binding.addColorBtn.setOnClickListener(v -> {
            String color = binding.colorSpinner.getSelectedItem().toString();
            if (!color.isEmpty() && !selectedColors.contains(color)) {
                selectedColors.add(color);
                addChipToGroup(color, binding.colorChipGroup, selectedColors);
            }
        });
    }

    private void addChipToGroup(String text, ChipGroup chipGroup, final ArrayList<String> list) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.lightGrey);
        chip.setTextColor(getResources().getColor(R.color.black));
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            list.remove(text);
        });
        chipGroup.addView(chip);
    }

    private void setupEditMode() {
        binding.toolbarTitle.setText("Chỉnh sửa Sản phẩm");

        // Điền dữ liệu
        binding.titleEdt.setText(existingProduct.getTitle());
        binding.descriptionEdt.setText(existingProduct.getDescription());
        binding.priceEdt.setText(String.valueOf(existingProduct.getPrice()));
        binding.oldPriceEdt.setText(String.valueOf(existingProduct.getOldPrice()));
        binding.offPercentEdt.setText(existingProduct.getOffPercent());

        // Hiển thị các size và color đã có
        if (existingProduct.getSize() != null) {
            selectedSizes.addAll(existingProduct.getSize());
            for (String size : selectedSizes) {
                addChipToGroup(size, binding.sizeChipGroup, selectedSizes);
            }
        }
        if (existingProduct.getColor() != null) {
            selectedColors.addAll(existingProduct.getColor());
            for (String color : selectedColors) {
                addChipToGroup(color, binding.colorChipGroup, selectedColors);
            }
        }
        if (existingProduct.getPicUrl() != null) {
            imageUrls.addAll(existingProduct.getPicUrl());
            updateImageUrlsTextView();
        }
    }

    private void saveProduct() {
        // Lấy dữ liệu
        String title = binding.titleEdt.getText().toString().trim();
        String description = binding.descriptionEdt.getText().toString().trim();
        String priceStr = binding.priceEdt.getText().toString().trim();
        String oldPriceStr = binding.oldPriceEdt.getText().toString().trim();
        String offPercentStr = binding.offPercentEdt.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || oldPriceStr.isEmpty() || offPercentStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        ItemsModel productToSave = isEditMode ? existingProduct : new ItemsModel();

        // Gán dữ liệu
        productToSave.setTitle(title);
        productToSave.setDescription(description);
        productToSave.setPrice(Integer.parseInt(priceStr));
        productToSave.setOldPrice(Integer.parseInt(oldPriceStr));
        productToSave.setOffPercent(offPercentStr);
        productToSave.setPicUrl(imageUrls);
        productToSave.setSize(selectedSizes);
        productToSave.setColor(selectedColors);

        if (isEditMode) {
            viewModel.updateProduct(productToSave);
            Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
        } else {
            productToSave.setRating(0.0);
            productToSave.setReview(0);
            viewModel.addProduct(productToSave);
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // Các phương thức hỗ trợ upload ảnh (giữ nguyên)
    private void initImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        binding.imagePreview.setImageURI(uri);
                        uploadImageToCloudinary(uri);
                    }
                }
        );
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        binding.uploadProgressBar.setVisibility(View.VISIBLE);
        binding.uploadProgressBar.setIndeterminate(true);

        MediaManager.get().upload(imageUri).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {}
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {}
            @Override
            public void onSuccess(String requestId, Map resultData) {
                binding.uploadProgressBar.setVisibility(View.GONE);
                String url = resultData.get("secure_url").toString();
                imageUrls.add(url);
                updateImageUrlsTextView();
                binding.imagePreview.setImageResource(R.drawable.grey_bg);
                Toast.makeText(AddEditProductActivity.this, "Tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String requestId, ErrorInfo error) {
                binding.uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(AddEditProductActivity.this, "Lỗi tải ảnh: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReschedule(String requestId, ErrorInfo error) {}
        }).dispatch();
    }

    private void updateImageUrlsTextView() {
        binding.imageUrlsTxt.setText(TextUtils.join("\n", imageUrls));
    }
}
