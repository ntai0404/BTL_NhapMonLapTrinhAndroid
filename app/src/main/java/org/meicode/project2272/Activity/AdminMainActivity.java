package org.meicode.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.meicode.project2272.databinding.ActivityAdminMainBinding;
import android.content.Intent;

public class AdminMainActivity extends AppCompatActivity {
    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tạm thời chỉ triển khai Quản lý sản phẩm
        binding.btnManageProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khởi chạy Activity quản lý sản phẩm
                startActivity(new Intent(AdminMainActivity.this, ManageProductsActivity.class));
            }
        });

        binding.btnManageCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminMainActivity.this, "Chức năng sẽ được triển khai!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminMainActivity.this, "Chức năng sẽ được triển khai!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}