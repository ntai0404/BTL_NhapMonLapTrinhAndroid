// File: org/meicode/project2272/Adapter/ColorAdapter.java
package org.meicode.project2272.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.meicode.project2272.R;
import org.meicode.project2272.databinding.ViewholderColorBinding;
import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private final ArrayList<String> items;
    private final OnColorSelectedListener listener;
    private int selectedPosition = -1;
    private Context context;

    /**
     * Interface để gửi "tín hiệu" chứa mã màu đã chọn ra bên ngoài.
     */
    public interface OnColorSelectedListener {
        void onColorSelected(String color);
    }

    /**
     * Constructor nhận vào danh sách mã màu và một đối tượng listener.
     */
    public ColorAdapter(ArrayList<String> items, OnColorSelectedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderColorBinding binding = ViewholderColorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Tô màu cho view
        try {
            holder.binding.colorView.setBackgroundColor(Color.parseColor(items.get(position)));
        } catch (Exception e) {
            // Xử lý nếu mã màu không hợp lệ
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Gọi listener để thông báo cho DetailActivity
            if (listener != null) {
                listener.onColorSelected(items.get(selectedPosition));
            }

            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
        });

        // Hiển thị viền hoặc dấu check nếu được chọn
        if (selectedPosition == holder.getAdapterPosition()) {
            holder.binding.selector.setVisibility(View.VISIBLE);
        } else {
            holder.binding.selector.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderColorBinding binding;
        public ViewHolder(ViewholderColorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}