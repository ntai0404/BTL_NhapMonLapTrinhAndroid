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

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.meicode.project2272.R;

public class ForgotActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnSendResetLink;
    private TextView tvBackToLogin;

    private DatabaseReference databaseRef;
    private static final String TAG = "ForgotActivity";
    private static final String USERS_NODE = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSendResetLink.setOnClickListener(v -> sendResetLink());
        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> {
                Intent intent = new Intent(ForgotActivity.this, SplashActivity.class); // Vẫn giữ quay lại Splash (màn hình đăng nhập)
                startActivity(intent);
                finish();
            });
        }
    }
    private void sendResetLink() {
        String email = etForgotEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etForgotEmail.setError("Email không được để trống!");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etForgotEmail.setError("Email không hợp lệ!");
            return;
        }

        Query emailQuery = databaseRef.child(USERS_NODE).orderByChild("email").equalTo(email);
        emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userUid = null;
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        userUid = userSnapshot.getKey();
                        break;
                    }
                    if (userUid != null) {
                        Toast.makeText(ForgotActivity.this, "Đã tìm thấy tài khoản. Chuyển hướng để đặt lại mật khẩu.", Toast.LENGTH_LONG).show();

                        // Chuyển sang RepwActivity và truyền UID của người dùng
                        Intent intent = new Intent(ForgotActivity.this, RepwActivity.class);
                        intent.putExtra("USER_UID", userUid); // Truyền UID qua Intent
                        startActivity(intent);
                        finish(); // Đóng ForgotActivity
                    } else {
                        Log.e(TAG, "User found by email but UID is null. Data structure might be unexpected.");
                        Toast.makeText(ForgotActivity.this, "Lỗi: Không thể xác định tài khoản người dùng.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ForgotActivity.this, "Không tìm thấy tài khoản với email này.", Toast.LENGTH_LONG).show();
                    etForgotEmail.setError("Email không tồn tại.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error during email lookup: " + error.getMessage());
                Toast.makeText(ForgotActivity.this, "Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
