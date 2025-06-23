package org.meicode.project2272.Adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.databinding.ViewholderCartBinding;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public ArrayList<ItemsModel> listItemSelected;
    private final CartItemListener listener;

    public interface CartItemListener {
        void onPlusClick(String cartId);
        void onMinusClick(String cartId);
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
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(currencyFormatter.format(item.getPrice()));
        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberinCart()));

        double totalEach = (double) item.getPrice() * item.getNumberinCart();
        holder.binding.totalEachItem.setText(currencyFormatter.format(totalEach));

        holder.binding.sizeValueTxt.setText(item.getSelectedSize());
        // --- LOGIC TÔ MÀU CHO VIEW ---
        // 1. Lấy mã màu hex từ sản phẩm
        String hexColor = item.getSelectedColor();

        // 2. Kiểm tra xem mã màu có hợp lệ không
        if (hexColor != null && !hexColor.isEmpty()) {
            holder.binding.colorSwatchView.setVisibility(View.VISIBLE);
            try {
                // 3. Lấy background drawable của View
                Drawable background = holder.binding.colorSwatchView.getBackground();

                // 4. Wrap drawable để tương thích với các phiên bản Android cũ
                Drawable wrappedDrawable = DrawableCompat.wrap(background);

                // 5. Tô màu mới từ mã hex lên drawable
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(hexColor));

            } catch (IllegalArgumentException e) {
                // Nếu mã màu không hợp lệ (ví dụ: sai định dạng), ẩn ô màu đi
                holder.binding.colorSwatchView.setVisibility(View.GONE);
            }
        } else {
            // Nếu sản phẩm không có thông tin màu, ẩn ô màu đi
            holder.binding.colorSwatchView.setVisibility(View.GONE);
        }
        // --- KẾT THÚC LOGIC TÔ MÀU ---

        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPicUrl().get(0))
                    .into(holder.binding.pic);
        }

        holder.binding.plsuCartBtn.setOnClickListener(v -> listener.onPlusClick(item.getCartId()));
        holder.binding.minusCartBtn.setOnClickListener(v -> listener.onMinusClick(item.getCartId()));
    }

    @Override
    public int getItemCount() {
        return (listItemSelected != null) ? listItemSelected.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;
        public CartViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
