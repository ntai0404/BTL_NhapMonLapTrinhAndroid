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
                Intent intent = new Intent(ForgotActivity.this, SplashActivity.class);
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
                    Toast.makeText(ForgotActivity.this, "Đã gửi hướng dẫn khôi phục mật khẩu đến email của bạn.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();

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
