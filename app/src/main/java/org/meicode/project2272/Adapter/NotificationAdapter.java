package org.meicode.project2272.Adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.meicode.project2272.Model.NotificationModel;
import org.meicode.project2272.databinding.ViewholderNotificationBinding;
import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<NotificationModel> notifications;
    private NotificationClickListener clickListener; // Thêm Listener

    // 1. Tạo một interface để xử lý sự kiện click
    public interface NotificationClickListener {
        void onNotificationClicked(NotificationModel notification);
    }

    // 2. Sửa constructor để nhận vào listener
    public NotificationAdapter(ArrayList<NotificationModel> notifications, NotificationClickListener clickListener) {
        this.notifications = notifications;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderNotificationBinding binding = ViewholderNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = notifications.get(position);
        holder.binding.contentTxt.setText(item.getContent());

        if (item.isRead()) {
            holder.binding.statusTxt.setText("Đã đọc");
            holder.binding.contentTxt.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.binding.statusTxt.setText("Mới");
            holder.binding.contentTxt.setTypeface(null, Typeface.BOLD);
        }

        // 3. Bắt sự kiện click trên toàn bộ item view
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderNotificationBinding binding;
        public ViewHolder(ViewholderNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
