package org.meicode.project2272.Activity;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.meicode.project2272.Adapter.CartAdapter;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityCartBinding;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {
    private ActivityCartBinding binding;
    private MainViewModel viewModel;
    private CartAdapter cartAdapter;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        currentUser = (UserModel) getIntent().getSerializableExtra("user");
        initCartList();
        setVariables();
    }

    private void setVariables() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initCartList() {
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(new ArrayList<>(), this);
        binding.cartView.setAdapter(cartAdapter);

        if (currentUser != null) {
            viewModel.getCartItems(currentUser.getUid()).observe(this, itemsInCart -> {
                if (itemsInCart == null || itemsInCart.isEmpty()) {
                    binding.emptyTxt.setVisibility(View.VISIBLE);
                    binding.scrollView3.setVisibility(View.GONE);
                } else {
                    binding.emptyTxt.setVisibility(View.GONE);
                    binding.scrollView3.setVisibility(View.VISIBLE);
                }

                cartAdapter.listItemSelected = itemsInCart;
                cartAdapter.notifyDataSetChanged();
                calculatorCart(itemsInCart);
            });
        }
    }


    private void calculatorCart(ArrayList<ItemsModel> items) {
        // Tạo đối tượng định dạng tiền tệ
        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        double percentTax = 0.02;
        double delivery = 30000;
        double itemTotal = 0;

        if (items != null) {
            for (ItemsModel item : items) {
                itemTotal += item.getPrice() * item.getNumberinCart();
            }
        }

        double tax = Math.round(itemTotal * percentTax * 100.0) / 100.0;
        double total = Math.round((itemTotal + tax + delivery) * 100.0) / 100.0;
        itemTotal = Math.round(itemTotal * 100.0) / 100.0;

        // Sử dụng đối tượng định dạng để hiển thị giá trị cuối cùng
        binding.totalFeeTxt.setText(currencyFormatter.format(itemTotal));
        binding.taxTxt.setText(currencyFormatter.format(tax));
        binding.deliveryTxt.setText(currencyFormatter.format(delivery));
        binding.totalTxt.setText(currencyFormatter.format(total));
    }

    @Override
    public void onPlusClick(String itemId) {
        if (currentUser != null) {
            viewModel.manageCartItem(currentUser.getUid(), itemId, 1);
        }
    }
    @Override
    public void onMinusClick(String itemId) {
        if (currentUser != null) {
            viewModel.manageCartItem(currentUser.getUid(), itemId, -1);
        }
    }
}