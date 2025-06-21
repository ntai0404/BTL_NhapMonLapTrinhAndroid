package org.meicode.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.meicode.project2272.Adapter.AdminProductAdapter;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.FragmentAdminProductsBinding;

import java.util.ArrayList;

public class AdminProductsFragment extends Fragment implements AdminProductAdapter.ProductClickListener {

    private FragmentAdminProductsBinding binding;
    private MainViewModel viewModel;
    private AdminProductAdapter adapter;
    private ArrayList<ItemsModel> productList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng View Binding cho Fragment
        binding = FragmentAdminProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        loadProducts();

        // Xử lý sự kiện cho nút thêm sản phẩm
        binding.fabAddProduct.setOnClickListener(v -> {
            // Trong Fragment, dùng requireContext() để lấy Context
            startActivity(new Intent(requireContext(), AddEditProductActivity.class));
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(productList, this);
        binding.productsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.productsRecyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        // Trong Fragment, dùng getViewLifecycleOwner() để theo dõi LiveData
        viewModel.loadPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (itemsModels != null) {
                productList.clear();
                productList.addAll(itemsModels);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onEditClick(ItemsModel item) {
        Intent intent = new Intent(requireContext(), AddEditProductActivity.class);
        intent.putExtra("product_to_edit", item);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ItemsModel item, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '" + item.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteProduct(item.getId()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Tránh rò rỉ bộ nhớ với View Binding trong Fragment
        binding = null;
    }
}