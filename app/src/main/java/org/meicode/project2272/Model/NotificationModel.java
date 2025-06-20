package org.meicode.project2272.Model;


public class NotificationModel {
    private String notificationId;
    private String userId;
    private String content;
    private boolean read; // Đổi tên thuộc tính

    public NotificationModel() {
    }

    // --- Getters và Setters tương ứng ---
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    // Đổi tên getter và setter cho rõ ràng
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}