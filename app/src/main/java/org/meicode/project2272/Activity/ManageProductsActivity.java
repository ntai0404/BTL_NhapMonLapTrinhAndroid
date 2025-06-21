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

public class ManageProductsActivity extends AppCompatActivity {

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



        binding.fabAddProduct.setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditProductActivity.class));
        });
    }





}