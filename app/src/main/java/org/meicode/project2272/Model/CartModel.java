// File: app/src/main/java/org/meicode/project2272/Model/CartModel.java
package org.meicode.project2272.Model;

public class CartModel {
    private ItemsModel item; // Đối tượng sản phẩm đầy đủ
    private int quantity;    // Số lượng của sản phẩm đó trong giỏ

    public CartModel() {
    }

    public ItemsModel getItem() {
        return item;
    }

    public void setItem(ItemsModel item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}