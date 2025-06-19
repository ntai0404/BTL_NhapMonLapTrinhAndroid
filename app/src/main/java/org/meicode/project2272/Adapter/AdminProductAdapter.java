package org.meicode.project2272.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.databinding.ViewholderAdminProductBinding;

import java.util.ArrayList;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private ArrayList<ItemsModel> items;
    private Context context;
    private ProductClickListener clickListener;

    public AdminProductAdapter(ArrayList<ItemsModel> items, ProductClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderAdminProductBinding binding = ViewholderAdminProductBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel currentItem = items.get(position);
        holder.binding.productTitleTxt.setText(currentItem.getTitle());
        holder.binding.productPriceTxt.setText("$" + currentItem.getPrice());

        if (currentItem.getPicUrl() != null && !currentItem.getPicUrl().isEmpty()) {
            Glide.with(context)
                    .load(currentItem.getPicUrl().get(0))
                    .into(holder.binding.productImageView);
        }

        holder.binding.btnEdit.setOnClickListener(v -> clickListener.onEditClick(currentItem));
        holder.binding.btnDelete.setOnClickListener(v -> clickListener.onDeleteClick(currentItem, position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderAdminProductBinding binding;
        public ViewHolder(ViewholderAdminProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Interface để gửi sự kiện click về Activity
    public interface ProductClickListener {
        void onEditClick(ItemsModel item);
        void onDeleteClick(ItemsModel item, int position);
    }
}