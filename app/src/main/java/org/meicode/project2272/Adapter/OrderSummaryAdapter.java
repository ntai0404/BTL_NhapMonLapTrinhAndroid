package org.meicode.project2272.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.databinding.ViewholderOrderSummaryItemBinding;

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
        ViewholderOrderSummaryItemBinding binding = ViewholderOrderSummaryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel item = items.get(position);
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.quantityTxt.setText("SL: " + item.getNumberinCart());
        holder.binding.priceTxt.setText(currencyFormatter.format(item.getPrice()));

        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPicUrl().get(0))
                    .into(holder.binding.pic);
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderSummaryItemBinding binding;
        public ViewHolder(ViewholderOrderSummaryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
