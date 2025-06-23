// File: org/meicode/project2272/Model/ItemsModel.java
package org.meicode.project2272.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemsModel implements Serializable {
    // Các trường dữ liệu gốc từ Firebase "Items"
    private String id;
    private String title;
    private String description;
    private String offPercent;
    private ArrayList<String> size;
    private ArrayList<String> color;
    private ArrayList<String> picUrl;
    private int price;
    private int oldPrice;
    private int review;
    private double rating;

    // Các trường dữ liệu "transient" - được gán lúc runtime để hiển thị trong giỏ hàng
    private String cartId;
    private int numberinCart;
    private String selectedSize; // Size đã chọn trong giỏ hàng
    private String selectedColor; // Màu đã chọn trong giỏ hàng

    public ItemsModel() {}

    // Getters và Setters cho tất cả các trường
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOffPercent() { return offPercent; }
    public void setOffPercent(String offPercent) { this.offPercent = offPercent; }
    public ArrayList<String> getSize() { return size; }
    public void setSize(ArrayList<String> size) { this.size = size; }
    public ArrayList<String> getColor() { return color; }
    public void setColor(ArrayList<String> color) { this.color = color; }
    public ArrayList<String> getPicUrl() { return picUrl; }
    public void setPicUrl(ArrayList<String> picUrl) { this.picUrl = picUrl; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getOldPrice() { return oldPrice; }
    public void setOldPrice(int oldPrice) { this.oldPrice = oldPrice; }
    public int getReview() { return review; }
    public void setReview(int review) { this.review = review; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    // Getters and Setters cho các trường transient
    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public int getNumberinCart() { return numberinCart; }
    public void setNumberinCart(int numberinCart) { this.numberinCart = numberinCart; }
    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }
    public String getSelectedColor() { return selectedColor; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }
}
