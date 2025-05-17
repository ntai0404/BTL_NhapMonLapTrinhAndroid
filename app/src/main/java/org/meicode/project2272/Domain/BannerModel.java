package org.meicode.project2272.Domain;

public class BannerModel {
    private String url;

    // Constructor mặc định bắt buộc cho Firebase
    public BannerModel() {}

    // Constructor khởi tạo với tham số
    public BannerModel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}


