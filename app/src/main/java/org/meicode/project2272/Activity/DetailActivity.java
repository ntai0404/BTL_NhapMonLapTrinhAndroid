package org.meicode.project2272.Activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import org.meicode.project2272.Adapter.PicListAdapter;
import org.meicode.project2272.Domain.ItemsModel;
import org.meicode.project2272.Helper.ManagmentCart;
import org.meicode.project2272.R;
import org.meicode.project2272.databinding.ActivityDetailBinding;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private ItemsModel object;
    private int numberOrder=1;
    private ManagmentCart managmentCart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        getBundle();
        initPicList();

    }

    private void initPicList() {
        ArrayList<String> picList = new ArrayList<>(object.getPicUrl());

        Glide.with(this)
                .load(object.getPicUrl().get(0)) // là String URL
                .into(binding.pic);              // là ImageView (Shapeable vẫn OK nếu truyền đúng)
        binding.picList.setAdapter(new PicListAdapter(picList, binding.pic));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

    }

    private void getBundle() {
        object = (ItemsModel) getIntent().getSerializableExtra("object");
        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("$"+String.valueOf(object.getPrice()));
        binding.oldPriceTxt.setText("$"+object.getOldPrice());
        binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.decriptionTxt.setText(object.getDescription());

        binding.addToCartBtn.setOnClickListener(v -> {
            object.setNumberinCart(numberOrder);
            managmentCart.insertItem(object);
        });

        binding.backBtn.setOnClickListener(v -> finish());

    }
}