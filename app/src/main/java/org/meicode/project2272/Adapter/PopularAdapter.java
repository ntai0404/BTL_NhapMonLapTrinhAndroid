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
import org.meicode.project2272.databinding.ViewholderPopularBinding;
import org.meicode.project2272.Activity.DetailActivity;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    // Danh sách dữ liệu sản phẩm
    private ArrayList<ItemsModel> items;

    // Ngữ cảnh (context) để khởi tạo view hoặc gọi activity
    private Context context;

    // Constructor nhận dữ liệu truyền vào từ bên ngoài
    public PopularAdapter(ArrayList<ItemsModel> items) {
        this.items = items;
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

        // Gắn tiêu đề
        holder.binding.titleTxt.setText(items.get(position).getTitle());

        // Gắn giá hiện tại
        holder.binding.priceTxt.setText("$" + items.get(position).getPrice());

        // Gắn điểm đánh giá
        holder.binding.ratingTxt.setText("(" + items.get(position).getRating() + ")");

        // Gắn phần trăm giảm giá
        holder.binding.offPercentTxt.setText(items.get(position).getOffPercent() + " Off");

        // Gắn giá cũ và gạch ngang
        holder.binding.oldPriceTxt.setText("$" + items.get(position).getOldPrice());
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Hiển thị ảnh sản phẩm bằng Glide
        RequestOptions options = new RequestOptions().transform(new CenterInside()); // Tùy chỉnh hiển thị ảnh
        Glide.with(context)
                .load(items.get(position).getPicUrl().get(0)) // Lấy ảnh đầu tiên trong danh sách
                .apply(options)
                .into(holder.binding.pic); // Gắn vào ImageView

        // Bắt sự kiện click: khi người dùng nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang DetailActivity
            Intent intent = new Intent(context, DetailActivity.class);

            // Truyền đối tượng sản phẩm sang activity mới
            intent.putExtra("object", items.get(position));

            // Bắt đầu activity
            context.startActivity(intent);
        });
    }

    // Trả về tổng số phần tử trong danh sách
    @Override
    public int getItemCount() {
        return items.size();
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

