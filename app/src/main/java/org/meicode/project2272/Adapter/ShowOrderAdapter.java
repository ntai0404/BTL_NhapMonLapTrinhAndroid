package org.meicode.project2272.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.R;

import java.util.ArrayList;
import java.util.Locale;

public class ShowOrderAdapter extends RecyclerView.Adapter<ShowOrderAdapter.ViewHolder> {

    // 1. Interface để xử lý sự kiện click
    public interface OnCancelClickListener {
        void onCancelClick(BillModel bill);
    }

    private ArrayList<BillModel> items;
    private Context context;
    private  boolean isAdmin;
    private OnCancelClickListener cancelClickListener;

    public ShowOrderAdapter(ArrayList<BillModel> items, boolean isAdmin, OnCancelClickListener listener) {
        this.items = items;
        this.isAdmin = isAdmin;
        this.cancelClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillModel bill = items.get(position);

        if (bill.getBillId() != null && bill.getBillId().length() > 8) {
            holder.orderIdTxt.setText("Mã đơn hàng: #" + bill.getBillId().substring(0, 8));
        } else {
            holder.orderIdTxt.setText("Mã đơn hàng: #" + bill.getBillId());
        }

        holder.orderDateTxt.setText("Ngày đặt: " + bill.getCreatedAt());
        holder.totalAmountTxt.setText(String.format(Locale.getDefault(), "Tổng tiền: %,.0fđ", bill.getTotalAmount()));
        holder.shippingAddressTxt.setText("Giao đến: " + bill.getShippingAddress());
        holder.paymentMethodTxt.setText("Thanh toán: " + bill.getPaymentMethod());
        holder.statusTxt.setText(bill.getStatus());

        String status = bill.getStatus().toLowerCase();
        if (status.contains("hủy") || status.contains("cancelled")) {
            holder.statusTxt.setBackgroundColor(Color.parseColor("#E53935")); // Red
            holder.cancelBtn.setVisibility(View.GONE); // Ẩn nút hủy nếu đã hủy
        } else if (status.contains("thành công") || status.contains("completed")) {
            holder.statusTxt.setBackgroundColor(Color.parseColor("#43A047")); // Green
            holder.cancelBtn.setVisibility(View.GONE); // Ẩn nút hủy nếu đã hoàn thành
        } else { // Trạng thái "Pending", "Chờ xác nhận",...
            holder.statusTxt.setBackgroundColor(Color.parseColor("#FB8C00")); // Orange
            holder.cancelBtn.setVisibility(View.VISIBLE); // Hiển thị nút hủy
        }

        if (isAdmin) {
            holder.userIdTxt.setVisibility(View.VISIBLE);
            holder.userIdTxt.setText("Mã người dùng: " + bill.getUserId());
        } else {
            holder.userIdTxt.setVisibility(View.GONE);
        }

        holder.cancelBtn.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.onCancelClick(bill);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt, totalAmountTxt, userIdTxt, shippingAddressTxt, paymentMethodTxt, statusTxt;
        Button cancelBtn; // Thêm nút hủy

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            totalAmountTxt = itemView.findViewById(R.id.totalAmountTxt);
            userIdTxt = itemView.findViewById(R.id.userIdTxt);
            shippingAddressTxt = itemView.findViewById(R.id.shippingAddressTxt);
            paymentMethodTxt = itemView.findViewById(R.id.paymentMethodTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            cancelBtn = itemView.findViewById(R.id.cancelBtn); // Ánh xạ nút hủy
        }
    }
}
