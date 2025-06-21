package org.meicode.project2272.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemsModel implements Serializable {
    private String id; // THÊM TRƯỜNG ID
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
    private int NumberinCart;

    public ItemsModel() {}

    // THÊM GETTER VÀ SETTER CHO ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // --- Các getters và setters khác giữ nguyên ---
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
    public int getNumberinCart() { return NumberinCart; }
    public void setNumberinCart(int numberInCart) { NumberinCart = numberInCart; }
}
