package org.meicode.project2272.Adapter;

// Import các thư viện cần thiết

import android.graphics.Paint;
import android.view.ViewGroup;
import android.content.Context;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;

import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.databinding.ViewholderPopularBinding;
import org.meicode.project2272.Activity.DetailActivity;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private ArrayList<ItemsModel> items;

    // Ngữ cảnh (context) để khởi tạo view hoặc gọi activity
    private Context context;
    private UserModel currentUser;

    // Constructor nhận dữ liệu truyền vào từ bên ngoài
    public PopularAdapter(ArrayList<ItemsModel> items, UserModel user) {
        this.items = items;
        this.currentUser = user;
    }

    // Hàm tạo ViewHolder từ layout viewholder_popular.xml
    @NonNull
    @Override
    public PopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // Lưu context để sử dụng trong Glide và Intent
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding); // Trả về ViewHolder mới
    }

    // Gắn dữ liệu từng item vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.ViewHolder holder, int position) {
        // Ngữ cảnh (context) để khởi tạo view hoặc gọi activity
        context = holder.itemView.getContext();
        // Tạo đối tượng định dạng tiền tệ
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        // Gắn tiêu đề
        holder.binding.titleTxt.setText(items.get(position).getTitle());

        // Gắn giá hiện tại đã được định dạng
        holder.binding.priceTxt.setText(currencyFormatter.format(items.get(position).getPrice()));

        // Gắn điểm đánh giá
        holder.binding.ratingTxt.setText("(" + items.get(position).getRating() + ")");

        // Gắn phần trăm giảm giá
        holder.binding.offPercentTxt.setText(items.get(position).getOffPercent() + " Off");

        // Gắn giá cũ đã được định dạng và gạch ngang
        holder.binding.oldPriceTxt.setText(currencyFormatter.format(items.get(position).getOldPrice()));
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Hiển thị ảnh sản phẩm bằng Glide
        RequestOptions options = new RequestOptions().transform(new CenterInside()); // Tùy chỉnh hiển thị ảnh
        Glide.with(context)
                .load(items.get(position).getPicUrl().get(0)) // Lấy ảnh đầu tiên trong danh sách
                .apply(options)
                .into(holder.binding.pic); // Gắn vào ImageView

        // Bắt sự kiện click: khi người dùng nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            intent.putExtra("user", currentUser); // Truyền UserModel sang DetailActivity
            context.startActivity(intent);
        });
    }
    // Trả về tổng số phần tử trong danh sách
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Trong lớp PopularAdapter.java
    public void setItems(ArrayList<ItemsModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Lớp ViewHolder chứa các thành phần giao diện của từng item
    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;

        // Constructor nhận binding của viewholder_popular.xml
        public ViewHolder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

