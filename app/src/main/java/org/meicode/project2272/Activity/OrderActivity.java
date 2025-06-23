// File: org/meicode/project2272/Activity/OrderActivity.java
// ĐÃ CẬP NHẬT
package org.meicode.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.meicode.project2272.Adapter.OrderSummaryAdapter;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.Model.ItemInBillModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.ActivityOrderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {

    private ActivityOrderBinding binding;
    private MainViewModel viewModel;
    private UserModel currentUser;
    private ArrayList<ItemsModel> cartItems;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        getDataFromIntent();
        initUI();
        setListeners();
    }

    private void getDataFromIntent() {
        currentUser = (UserModel) getIntent().getSerializableExtra("user");
        cartItems = (ArrayList<ItemsModel>) getIntent().getSerializableExtra("cartItems");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
    }

    private void initUI() {
        if (currentUser != null) {
            binding.usernameTxt.setText(currentUser.getUsername());
            binding.phoneTxt.setText(currentUser.getPhone());
            binding.addressEdt.setText(currentUser.getAddress());
        }

        java.text.NumberFormat currencyFormatter = java.text.NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        binding.totalTxt.setText(currencyFormatter.format(totalAmount));

        // Setup RecyclerView với Adapter mới
        binding.itemsView.setLayoutManager(new LinearLayoutManager(this));
        binding.itemsView.setAdapter(new OrderSummaryAdapter(cartItems));
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.confirmBtn.setOnClickListener(v -> createOrder());
    }

    private void createOrder() {
        String address = binding.addressEdt.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.confirmBtn.setEnabled(false);

        // 1. Tạo danh sách sản phẩm cho hóa đơn
        ArrayList<ItemInBillModel> itemsInBill = new ArrayList<>();
        for (ItemsModel cartItem : cartItems) {
            ItemInBillModel item = new ItemInBillModel();
            item.setItemId(cartItem.getId());
            item.setTitle(cartItem.getTitle());
            item.setQuantity(cartItem.getNumberinCart());
            item.setPriceAtPurchase(cartItem.getPrice());

            // --- THAY ĐỔI QUAN TRỌNG NHẤT ---
            // Lấy size và color từ item trong giỏ hàng và gán vào item của hóa đơn
            item.setSelectedSize(cartItem.getSelectedSize());
            item.setSelectedColor(cartItem.getSelectedColor());
            // ------------------------------------

            if (cartItem.getPicUrl() != null && !cartItem.getPicUrl().isEmpty()) {
                item.setPicUrl(cartItem.getPicUrl().get(0));
            }
            itemsInBill.add(item);
        }

        // 2. Tạo đối tượng BillModel
        BillModel bill = new BillModel();
        bill.setUserId(currentUser.getUid());
        bill.setItems(itemsInBill);
        bill.setTotalAmount(totalAmount);
        bill.setShippingAddress(address);
        bill.setPaymentMethod("Thanh toán khi nhận hàng (COD)");
        bill.setStatus("Pending");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        bill.setCreatedAt(sdf.format(Calendar.getInstance().getTime()));

        // 3. Gọi ViewModel để lưu đơn hàng (logic này không đổi)
        viewModel.placeOrderAndCreateNotification(bill).observe(this, isSuccess -> {
            binding.progressBar.setVisibility(View.GONE);
            if(isSuccess != null && isSuccess) {
                Toast.makeText(OrderActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                viewModel.clearCart(currentUser.getUid());

                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user", currentUser);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(OrderActivity.this, "Đặt hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                binding.confirmBtn.setEnabled(true);
            }
        });
    }
}