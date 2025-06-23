// File: org/meicode/project2272/Adapter/OrderSummaryAdapter.java
package org.meicode.project2272.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.meicode.project2272.Model.ItemsModel;
// Giả sử bạn có một layout tên là viewholder_order_summary.xml
import org.meicode.project2272.databinding.ViewholderOrderSummaryBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {

    private final ArrayList<ItemsModel> items;

    public OrderSummaryAdapter(ArrayList<ItemsModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderOrderSummaryBinding binding = ViewholderOrderSummaryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel item = items.get(position);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.quantityTxt.setText("Số lượng: " + item.getNumberinCart());
        holder.binding.priceTxt.setText(currencyFormatter.format(item.getPrice()));

        // Hiển thị size và màu đã chọn
        holder.binding.sizeTxt.setText("Size: " + item.getSelectedSize());
        holder.binding.colorTxt.setText("Màu: " + item.getSelectedColor());

        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPicUrl().get(0))
                    .into(holder.binding.pic);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderSummaryBinding binding;

        public ViewHolder(ViewholderOrderSummaryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
