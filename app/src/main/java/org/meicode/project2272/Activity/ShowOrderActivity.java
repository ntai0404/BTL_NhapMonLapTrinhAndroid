package org.meicode.project2272.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.meicode.project2272.Adapter.ShowOrderAdapter;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.R;
import org.meicode.project2272.ViewModel.MainViewModel;

// SỬA ĐỔI 1: Implement interface mới
public class ShowOrderActivity extends AppCompatActivity implements ShowOrderAdapter.OnOrderActionsListener {

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
        userId = getIntent().getStringExtra("userId");

        progressBar = findViewById(R.id.progressBar);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        if (isAdmin) {
            viewModel.getAllOrders().observe(this, bills -> {
                adapter = new ShowOrderAdapter(bills, true, this);
                ordersRecyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            });
        } else {
            if (userId != null && !userId.isEmpty()) {
                viewModel.getUserOrders(userId).observe(this, bills -> {
                    adapter = new ShowOrderAdapter(bills, false, this);
                    ordersRecyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                });
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCancelClick(BillModel bill) {
        String cancelMessage = isAdmin ? "Cancelled by Admin" : "Cancelled by User";
        new AlertDialog.Builder(this)
                .setTitle("Hủy Đơn Hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    viewModel.updateOrderStatus(bill.getBillId(), cancelMessage);
                    Toast.makeText(this, "Đã hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    // SỬA ĐỔI 2: Implement phương thức mới để cập nhật trạng thái
    @Override
    public void onUpdateStatusClick(BillModel bill, String newStatus) {
        new AlertDialog.Builder(this)
                .setTitle("Cập nhật Trạng thái")
                .setMessage("Chuyển trạng thái đơn hàng thành '" + newStatus + "'?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    viewModel.updateOrderStatus(bill.getBillId(), newStatus);
                    Toast.makeText(this, "Đã cập nhật trạng thái.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}