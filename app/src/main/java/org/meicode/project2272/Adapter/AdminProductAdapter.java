package org.meicode.project2272.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.databinding.ViewholderAdminProductBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private ArrayList<ItemsModel> items;
    private final ProductClickListener listener;

    // SỬA LỖI: Cập nhật interface để nhất quán
    public interface ProductClickListener {
        void onEditClick(ItemsModel item);
        void onDeleteClick(ItemsModel item, int position); // Thêm tham số position
    }

    public AdminProductAdapter(ArrayList<ItemsModel> items, ProductClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderAdminProductBinding binding = ViewholderAdminProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel currentItem = items.get(position);
        holder.binding.titleTxt.setText(currentItem.getTitle());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.binding.priceTxt.setText(currencyFormatter.format(currentItem.getPrice()));

        if (currentItem.getPicUrl() != null && !currentItem.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentItem.getPicUrl().get(0))
                    .into(holder.binding.pic);
        }

        holder.binding.editBtn.setOnClickListener(v -> listener.onEditClick(currentItem));
        // SỬA LỖI: Truyền cả vị trí của item vào listener
        holder.binding.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(currentItem, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // SỬA LỖI: Bổ sung phương thức updateData
    public void updateData(ArrayList<ItemsModel> newItems) {
        if (newItems != null) {
            this.items = newItems;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderAdminProductBinding binding;
        public ViewHolder(ViewholderAdminProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}