package org.meicode.project2272.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.meicode.project2272.Adapter.NotificationAdapter;
import org.meicode.project2272.Model.NotificationModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityNotificationBinding;

// 1. Implement interface của Adapter
public class NotificationActivity extends AppCompatActivity implements NotificationAdapter.NotificationClickListener {

    private ActivityNotificationBinding binding;
    private MainViewModel viewModel;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = (UserModel) getIntent().getSerializableExtra("user");
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        if (currentUser == null) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            return;
        }

        initRecyclerView();
        setListeners();
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initRecyclerView() {
        binding.notificationView.setLayoutManager(new LinearLayoutManager(this));

        viewModel.loadNotifications(currentUser.getUid()).observe(this, notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                binding.emptyTxt.setVisibility(View.VISIBLE);
                binding.notificationView.setVisibility(View.GONE);
            } else {
                binding.emptyTxt.setVisibility(View.GONE);
                binding.notificationView.setVisibility(View.VISIBLE);
                // 2. Truyền Activity (this) vào làm listener khi tạo Adapter
                NotificationAdapter adapter = new NotificationAdapter(notifications, this);
                binding.notificationView.setAdapter(adapter);
            }
        });
    }

    // 3. Ghi đè và cài đặt logic cho phương thức từ interface
    @Override
    public void onNotificationClicked(NotificationModel notification) {
        // Chỉ thực hiện khi thông báo chưa được đọc
        if (!notification.isRead()) {
            if (currentUser != null) {
                viewModel.markNotificationAsRead(currentUser.getUid(), notification.getNotificationId());
                Toast.makeText(this, "Đã đánh dấu là đã đọc", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
