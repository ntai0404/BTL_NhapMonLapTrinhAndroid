package org.meicode.project2272.Activity; // Hoặc package gốc của bạn

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo Cloudinary một lần duy nhất tại đây
        try {
            Map<String, String> config = new HashMap<>();
            // THAY THẾ BẰNG THÔNG TIN TÀI KHOẢN CLOUDINARY
            config.put("cloud_name", "dkmirc7nw");
            config.put("api_key", "788284419763534");
            config.put("api_secret", "sEai3hdFKVA-pNXh9N2QY7mESLQ");
            MediaManager.init(this, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
