package org.meicode.project2272.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.meicode.project2272.Adapter.AdminProductAdapter;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Respository.MainRespository;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityManageProductsBinding;

import java.util.ArrayList;

public class ManageProductsActivity extends AppCompatActivity implements AdminProductAdapter.ProductClickListener {

    private ActivityManageProductsBinding binding;
    private MainViewModel viewModel;
    private AdminProductAdapter adapter;
    private ArrayList<ItemsModel> productList = new ArrayList<>();
    private MainRespository respository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        respository = new MainRespository();

        setupRecyclerView();
        loadProducts();

        binding.fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditProductActivity.class));
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(productList, this);
        binding.productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productsRecyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        viewModel.loadPopular().observe(this, itemsModels -> {
            if (itemsModels != null) {
                productList.clear();
                productList.addAll(itemsModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onEditClick(ItemsModel item) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product", item);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ItemsModel item, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '" + item.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    respository.deleteProduct(item.getKey()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ManageProductsActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            // Không cần xóa thủ công khỏi list vì LiveData sẽ tự cập nhật
                        } else {
                            Toast.makeText(ManageProductsActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}