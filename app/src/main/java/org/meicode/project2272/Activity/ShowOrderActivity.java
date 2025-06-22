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

public class ShowOrderActivity extends AppCompatActivity implements ShowOrderAdapter.OnCancelClickListener {

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
                // 2. Khi tạo Adapter, truyền 'this' vào làm listener
                adapter = new ShowOrderAdapter(bills, false, this);
                ordersRecyclerView.setAdapter(adapter);
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void loadAllOrders() {
        viewModel.getAllOrders().observe(this, bills -> {
            if (bills != null) {
                // 2. Khi tạo Adapter, truyền 'this' vào làm listener
                adapter = new ShowOrderAdapter(bills, true, this);
                ordersRecyclerView.setAdapter(adapter);
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    // 3. Override phương thức onCancelClick để xử lý sự kiện
    @Override
    public void onCancelClick(BillModel bill) {
        // Hiển thị hộp thoại xác nhận trước khi hủy
        new AlertDialog.Builder(this)
                .setTitle("Hủy Đơn Hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + (bill.getBillId() != null ? bill.getBillId().substring(0, 8) : "") + "?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Nếu người dùng xác nhận, gọi ViewModel để thực hiện việc hủy
                    viewModel.cancelOrder(bill);
                    Toast.makeText(this, "Đã gửi yêu cầu hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Không", null) // Nút "Không" không làm gì cả
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
