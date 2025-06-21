package org.meicode.project2272.Fragment;

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
import org.meicode.project2272.Activity.AddEditProductActivity;
import org.meicode.project2272.Adapter.AdminProductAdapter;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.FragmentAdminProductsBinding;
import java.util.ArrayList;

public class AdminProductsFragment extends Fragment implements AdminProductAdapter.ProductClickListener {

    private FragmentAdminProductsBinding binding;
    private MainViewModel viewModel;
    private AdminProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        loadProducts();

        binding.fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditProductActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        binding.productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminProductAdapter(new ArrayList<>(), this);
        binding.productsRecyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        viewModel.loadPopular().observe(getViewLifecycleOwner(), itemsModels -> {
            if (itemsModels != null) {
                adapter.updateData(itemsModels);
            }
        });
    }

    @Override
    public void onEditClick(ItemsModel item) {
        Intent intent = new Intent(getActivity(), AddEditProductActivity.class);
        intent.putExtra("product_to_edit", item);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ItemsModel item, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '" + item.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // TODO: Gọi phương thức trong ViewModel/Repository để xóa item khỏi Firebase.
                    // Bạn có thể dùng item.getId() để xác định sản phẩm cần xóa.
                    Toast.makeText(getContext(), "Chức năng xóa sẽ được triển khai!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}