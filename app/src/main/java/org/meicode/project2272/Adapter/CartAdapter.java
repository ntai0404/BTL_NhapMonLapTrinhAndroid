package org.meicode.project2272.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.databinding.ViewholderCartBinding;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    public ArrayList<ItemsModel> listItemSelected;
    private CartItemListener listener;

    public interface CartItemListener {
        void onPlusClick(String itemId);
        void onMinusClick(String itemId);
    }

    public CartAdapter(ArrayList<ItemsModel> listItemSelected, CartItemListener listener) {
        this.listItemSelected = listItemSelected;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ItemsModel item = listItemSelected.get(position);
        // Tạo đối tượng định dạng tiền tệ
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        holder.binding.titleTxt.setText(item.getTitle());

        // Định dạng giá của mỗi sản phẩm
        holder.binding.feeEachItem.setText(currencyFormatter.format(item.getPrice()));

        // Tính toán và định dạng tổng giá của mỗi loại sản phẩm
        double totalEachItem = item.getNumberinCart() * item.getPrice();
        holder.binding.totalEachItem.setText(currencyFormatter.format(totalEachItem));

        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberinCart()));

        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPicUrl().get(0))
                    .into(holder.binding.pic);
        }

        holder.binding.plsuCartBtn.setOnClickListener(v -> listener.onPlusClick(item.getId()));
        holder.binding.minusCartBtn.setOnClickListener(v -> listener.onMinusClick(item.getId()));
    }

    @Override
    public int getItemCount() {
        return (listItemSelected != null) ? listItemSelected.size() : 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;
        public CartViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
