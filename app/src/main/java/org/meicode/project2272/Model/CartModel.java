package org.meicode.project2272.Model;

import java.io.Serializable;

public class CartModel implements Serializable {
    private String cartId; // ID duy nhất của mục trong giỏ hàng (VD: -CART_ENTRY_01)
    private String itemId; // ID của sản phẩm gốc
    private int quantity;
    private String size;
    private String color;

    public CartModel() {
    }

    // Getters and Setters
    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}