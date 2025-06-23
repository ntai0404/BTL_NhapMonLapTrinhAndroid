// File: org/meicode/project2272/Adapter/SizeAdapter.java
package org.meicode.project2272.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.meicode.project2272.R;
import org.meicode.project2272.databinding.ViewholderSizeBinding;
import java.util.ArrayList;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {

    private final ArrayList<String> items;
    private final OnSizeSelectedListener listener;
    private int selectedPosition = -1; // -1 có nghĩa là chưa có mục nào được chọn
    private Context context;

    /**
     * Interface để gửi "tín hiệu" chứa size đã chọn ra bên ngoài (cho DetailActivity).
     */
    public interface OnSizeSelectedListener {
        void onSizeSelected(String size);
    }

    /**
     * Constructor nhận vào danh sách các size và một đối tượng listener.
     */
    public SizeAdapter(ArrayList<String> items, OnSizeSelectedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderSizeBinding binding = ViewholderSizeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.sizeTxt.setText(items.get(position));

        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Gọi listener để thông báo cho DetailActivity về lựa chọn mới
            if (listener != null) {
                listener.onSizeSelected(items.get(selectedPosition));
            }

            // Yêu cầu RecyclerView vẽ lại item cũ và item mới để cập nhật giao diện
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
        });

        // Thay đổi giao diện dựa trên việc item có được chọn hay không
        if (selectedPosition == position) {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.size_selected);
            holder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.purple));
        } else {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.size_unselected);
            holder.binding.sizeTxt.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderSizeBinding binding;
        public ViewHolder(ViewholderSizeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
