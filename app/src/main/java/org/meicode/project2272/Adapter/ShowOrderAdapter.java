package org.meicode.project2272.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.R;

import java.util.ArrayList;
import java.util.Locale;

public class ShowOrderAdapter extends RecyclerView.Adapter<ShowOrderAdapter.ViewHolder> {

    // SỬA ĐỔI 1: Mở rộng Interface để xử lý nhiều hành động
    public interface OnOrderActionsListener {
        void onCancelClick(BillModel bill);
        void onUpdateStatusClick(BillModel bill, String newStatus); // Thêm action mới
    }

    private ArrayList<BillModel> items;
    private Context context;
    private boolean isAdmin;
    private OnOrderActionsListener actionsListener; // Đổi tên listener

    // SỬA ĐỔI 2: Constructor nhận vào listener mới
    public ShowOrderAdapter(ArrayList<BillModel> items, boolean isAdmin, OnOrderActionsListener listener) {
        this.items = items;
        this.isAdmin = isAdmin;
        this.actionsListener = listener;
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

        // --- Hiển thị dữ liệu (giữ nguyên) ---
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

        // --- SỬA ĐỔI 3: LOGIC HIỂN THỊ NÚT VÀ MÀU SẮC ---
        String status = bill.getStatus() != null ? bill.getStatus().toLowerCase() : "";

        // Logic cho Admin
        if (isAdmin) {
            holder.actionsLayout.setVisibility(View.VISIBLE);
            holder.userIdTxt.setVisibility(View.VISIBLE);
            holder.userIdTxt.setText("Mã người dùng: " + bill.getUserId());

            holder.updateStatusBtn.setVisibility(View.GONE);
            holder.cancelBtn.setVisibility(View.GONE);

            switch (status) {
                case "pending":
                    holder.statusTxt.setBackgroundColor(Color.parseColor("#FB8C00")); // Orange
                    holder.updateStatusBtn.setVisibility(View.VISIBLE);
                    holder.updateStatusBtn.setText("Xác nhận");
                    holder.updateStatusBtn.setOnClickListener(v -> actionsListener.onUpdateStatusClick(bill, "Confirmed"));
                    holder.cancelBtn.setVisibility(View.VISIBLE);
                    holder.cancelBtn.setOnClickListener(v -> actionsListener.onCancelClick(bill));
                    break;
                case "confirmed":
                    holder.statusTxt.setBackgroundColor(Color.parseColor("#1976D2")); // Blue
                    holder.updateStatusBtn.setVisibility(View.VISIBLE);
                    holder.updateStatusBtn.setText("Hoàn thành");
                    holder.updateStatusBtn.setOnClickListener(v -> actionsListener.onUpdateStatusClick(bill, "Completed"));
                    break;
                case "completed":
                    holder.statusTxt.setBackgroundColor(Color.parseColor("#43A047")); // Green
                    holder.actionsLayout.setVisibility(View.GONE); // Đã hoàn thành, ẩn hết nút
                    break;
                default: // Bao gồm cancelled
                    holder.statusTxt.setBackgroundColor(Color.parseColor("#E53935")); // Red
                    holder.actionsLayout.setVisibility(View.GONE); // Đã hủy, ẩn hết nút
                    break;
            }
        }
        // Logic cho User thường
        else {
            holder.actionsLayout.setVisibility(View.GONE);
            holder.userIdTxt.setVisibility(View.GONE);
            // Có thể thêm nút "Hủy" cho user nếu trạng thái là "Pending"
            if (status.equals("pending")) {
                holder.actionsLayout.setVisibility(View.VISIBLE);
                holder.updateStatusBtn.setVisibility(View.GONE);
                holder.cancelBtn.setVisibility(View.VISIBLE);
                holder.cancelBtn.setOnClickListener(v -> actionsListener.onCancelClick(bill));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // SỬA ĐỔI 4: Thêm các view mới vào ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt, totalAmountTxt, userIdTxt, shippingAddressTxt, paymentMethodTxt, statusTxt;
        Button cancelBtn, updateStatusBtn;
        LinearLayout actionsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            totalAmountTxt = itemView.findViewById(R.id.totalAmountTxt);
            userIdTxt = itemView.findViewById(R.id.userIdTxt);
            shippingAddressTxt = itemView.findViewById(R.id.shippingAddressTxt);
            paymentMethodTxt = itemView.findViewById(R.id.paymentMethodTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);

            cancelBtn = itemView.findViewById(R.id.cancelBtn);
            updateStatusBtn = itemView.findViewById(R.id.updateStatusBtn);
            actionsLayout = itemView.findViewById(R.id.actionsLayout);
        }
    }
}
