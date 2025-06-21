
package org.meicode.project2272.Model;

import java.io.Serializable;
import java.util.ArrayList;

// BillModel cần implement Serializable để có thể truyền giữa các Activity
public class BillModel implements Serializable {
    private String billId;
    private String userId;
    private String createdAt;
    private ArrayList<ItemInBillModel> items; // Danh sách các sản phẩm trong hóa đơn
    private double totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private String status;

    // Constructor rỗng bắt buộc cho Firebase
    public BillModel() {
    }

    // --- Getters và Setters cho tất cả các trường ---

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<ItemInBillModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemInBillModel> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}