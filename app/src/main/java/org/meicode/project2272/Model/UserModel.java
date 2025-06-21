package org.meicode.project2272.Model;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String uid; // <-- THÊM DÒNG NÀY
    private String imageUrl; // <-- THÊM DÒNG NÀY
    private String username;
    private String password;
    private String role;
    private String phone;
    private String email;
    private String address;

    // Constructor mặc định
    public UserModel() {
    }

    // Getters
    public String getUid() { return uid; } // <-- THÊM METHOD NÀY
    public String getImageUrl() { return imageUrl; } // <-- THÊM METHOD NÀY
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }

    // Setters
    public void setUid(String uid) { this.uid = uid; } // <-- THÊM METHOD NÀY
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // <-- THÊM METHOD NÀY
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
}