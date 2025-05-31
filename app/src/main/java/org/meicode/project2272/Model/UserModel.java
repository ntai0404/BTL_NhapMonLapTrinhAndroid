package org.meicode.project2272.Model;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String username;
    private String password;
    private String role;
    private String phone;
    private String email;
    private String address;

    // Constructor mặc định
    public UserModel() {
    }

    // Constructor với đầy đủ tham số
    public UserModel(String username, String password, String role, String phone, String email, String address) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // (Tùy chọn) Ghi đè phương thức toString() để dễ dàng gỡ lỗi hoặc hiển thị
    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" + // Không nên log password thực tế
                ", role='" + role + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}