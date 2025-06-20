package org.meicode.project2272.Model;

import java.io.Serializable;

// ItemInBillModel cũng cần implement Serializable
public class ItemInBillModel implements Serializable {
    private String itemId; // Dùng String để khớp với ID của sản phẩm
    private String title;
    private double priceAtPurchase; // Giá tại thời điểm mua
    private int quantity;
    private String picUrl;

    // Constructor rỗng bắt buộc cho Firebase
    public ItemInBillModel() {
    }

    // --- Getters và Setters ---

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}

