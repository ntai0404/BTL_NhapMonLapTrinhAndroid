package org.meicode.project2272.Activity;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.meicode.project2272.Helper.TinyDB;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivitySplashBinding;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new MainViewModel();

        binding.startBtn.setOnClickListener(v -> {
            String nameOrEmail = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            if (nameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(SplashActivity.this, "Username/Email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            String temp;
            if (nameOrEmail.contains(".admin") || nameOrEmail.contains(".com") || nameOrEmail.contains("@")) {
                temp = "email";
            } else {
                temp = "username";
            }

            viewModel.loadUser(nameOrEmail, password, temp).observe(SplashActivity.this, userModels -> {
                if (userModels == null || userModels.isEmpty()) {
                    Toast.makeText(SplashActivity.this, "Please retry with true information!!!", Toast.LENGTH_SHORT).show();
                } else {
                    UserModel user = userModels.get(0);
                    //thêm 2 dòng này
                    TinyDB tinyDB = new TinyDB(SplashActivity.this);
                    tinyDB.putString("userId", user.getUid());
                    if (user.getRole().equals("user")) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        });
        binding.textView3.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        binding.textView4.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, ForgotActivity.class);
            startActivity(intent);
        });
    }
}
