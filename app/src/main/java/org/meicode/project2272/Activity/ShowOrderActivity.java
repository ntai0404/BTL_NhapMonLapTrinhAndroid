package org.meicode.project2272.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.meicode.project2272.Adapter.ShowOrderAdapter;
import org.meicode.project2272.R;
import org.meicode.project2272.ViewModel.MainViewModel;

public class ShowOrderActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private RecyclerView ordersRecyclerView;
    private ShowOrderAdapter adapter;
    private ProgressBar progressBar;
    private boolean isAdmin = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        progressBar = findViewById(R.id.progressBar);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setVisibility(View.VISIBLE);

        if (isAdmin) {
            loadAllOrders();
        } else {
            userId = getIntent().getStringExtra("userId");
            if (userId != null && !userId.isEmpty()) {
                loadUserOrders(userId);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void loadUserOrders(String userIdToLoad) {
        viewModel.getUserOrders(userIdToLoad).observe(this, bills -> {
            if (bills != null) {
                adapter = new ShowOrderAdapter(bills, false);
                ordersRecyclerView.setAdapter(adapter);
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void loadAllOrders() {
        viewModel.getAllOrders().observe(this, bills -> {
            if (bills != null) {
                adapter = new ShowOrderAdapter(bills, true);
                ordersRecyclerView.setAdapter(adapter);
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}
