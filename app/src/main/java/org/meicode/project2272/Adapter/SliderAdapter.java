package org.meicode.project2272.Adapter;

import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.meicode.project2272.R;
import org.meicode.project2272.Model.BannerModel;
import com.bumptech.glide.Glide;// phai hoc thuoc nguon nay nhe khong auto import duoc:v


import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private ArrayList<BannerModel> bannerModels;
    private ViewPager2 viewPager2;
    private Context context;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bannerModels.addAll(bannerModels); // Nếu muốn tạo hiệu ứng lặp vô hạn
            notifyDataSetChanged();
        }
    };

    public SliderAdapter(ArrayList<BannerModel> bannerModels, ViewPager2 viewPager2) {
        this.bannerModels = bannerModels;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(bannerModels.get(position));
        if (position == bannerModels.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        //return sliderItems.size();
        return bannerModels.size(); // <-- sửa ở đây
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(BannerModel bannerModel) {
            //Glider.with(context).load(bannerModel.getUrl()).into(imageView);
            Glide.with(context).load(bannerModel.getUrl()).into(imageView);

        }
    }
}

