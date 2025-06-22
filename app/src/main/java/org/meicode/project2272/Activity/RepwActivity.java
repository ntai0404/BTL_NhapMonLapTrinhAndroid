package org.meicode.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import org.meicode.project2272.R; // Đảm bảo đúng package R

public class RepwActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmNewPassword;
    private Button btnResetPassword;
    private TextView tvBackToLoginFromReset;

    private DatabaseReference databaseRef;
    private String userUid;
    private static final String TAG = "RepwActivity";
    private static final String USERS_NODE = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repw);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLoginFromReset = findViewById(R.id.tvBackToLoginFromReset);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent().hasExtra("USER_UID")) {
            userUid = getIntent().getStringExtra("USER_UID");
        }

        if (userUid == null || userUid.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin tài khoản để đặt lại mật khẩu.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RepwActivity.this, ForgotActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            Log.d(TAG, "Received UID for password reset: " + userUid);
        }
        btnResetPassword.setOnClickListener(v -> resetPassword());
        if (tvBackToLoginFromReset != null) {
            tvBackToLoginFromReset.setOnClickListener(v -> {
                Intent intent = new Intent(RepwActivity.this, SplashActivity.class); // Quay về màn hình đăng nhập chính
                startActivity(intent);
                finish();
            });
        }
    }

    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Mật khẩu mới không được để trống!");
            return;
        }
        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmNewPassword.setError("Vui lòng nhập lại mật khẩu!");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmNewPassword.setError("Mật khẩu xác nhận không khớp!");
            return;
        }
        databaseRef.child(USERS_NODE).child(userUid).child("password").setValue(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password successfully reset for UID: " + userUid);
                            Toast.makeText(RepwActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RepwActivity.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "Error resetting password for UID: " + userUid, task.getException());
                            Toast.makeText(RepwActivity.this, "Lỗi khi đặt lại mật khẩu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
