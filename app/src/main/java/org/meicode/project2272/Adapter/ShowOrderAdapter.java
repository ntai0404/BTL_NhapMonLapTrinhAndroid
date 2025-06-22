package org.meicode.project2272.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.R;

import java.util.ArrayList;
import java.util.Locale;

public class ShowOrderAdapter extends RecyclerView.Adapter<ShowOrderAdapter.ViewHolder> {

    private ArrayList<BillModel> items;
    private Context context;
    private boolean isAdmin;

    public ShowOrderAdapter(ArrayList<BillModel> items, boolean isAdmin) {
        this.items = items;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_order, parent, false);
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

        // --- PHẦN MỚI ĐƯỢC THÊM VÀO ---
        holder.shippingAddressTxt.setText("Giao đến: " + bill.getShippingAddress());
        holder.paymentMethodTxt.setText("Thanh toán: " + bill.getPaymentMethod());
        holder.statusTxt.setText(bill.getStatus());

        // Thay đổi màu sắc của trạng thái cho dễ nhìn
        String status = bill.getStatus().toLowerCase();
        if (status.contains("hủy")) {
            holder.statusTxt.setBackgroundColor(Color.RED);
        } else if (status.contains("thành công")) {
            holder.statusTxt.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.statusTxt.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
        }
        // --- KẾT THÚC PHẦN MỚI ---

        if (isAdmin) {
            holder.userIdTxt.setVisibility(View.VISIBLE);
            holder.userIdTxt.setText("Mã người dùng: " + bill.getUserId());
        } else {
            holder.userIdTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Thêm các TextView mới vào đây
        TextView orderIdTxt, orderDateTxt, totalAmountTxt, userIdTxt, shippingAddressTxt, paymentMethodTxt, statusTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            totalAmountTxt = itemView.findViewById(R.id.totalAmountTxt);
            userIdTxt = itemView.findViewById(R.id.userIdTxt);

            // Ánh xạ các TextView mới từ layout
            shippingAddressTxt = itemView.findViewById(R.id.shippingAddressTxt);
            paymentMethodTxt = itemView.findViewById(R.id.paymentMethodTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
        }
    }
}
