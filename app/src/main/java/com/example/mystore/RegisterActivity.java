package com.example.mystore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {
    private EditText etAccountRegister;
    private EditText etPasswordRegister;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView tvMsgRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etAccountRegister = findViewById(R.id.et_account_register);
        etPasswordRegister = findViewById(R.id.et_password_register);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvMsgRegister = findViewById(R.id.tv_msg_register);
        tvMsgRegister.setText("");

        // 註冊按鈕
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etAccountRegister.getText().toString();
                String password = etPasswordRegister.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    tvMsgRegister.setText("請填寫所有欄位");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    tvMsgRegister.setText("密碼與確認密碼不同");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // 註冊成功
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // 註冊失敗，顯示錯誤信息
                                String errorMessage = translateFirebaseError(task.getException());
                                tvMsgRegister.setText(errorMessage);
                            }
                        });
            }
        });
    }

    private String translateFirebaseError(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            switch (authException.getErrorCode()) {
                case "ERROR_WEAK_PASSWORD":
                    return "密碼太弱，請使用更強的密碼";
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "該電子郵件地址已被註冊";
                case "ERROR_INVALID_EMAIL":
                    return "無效的信箱格式";
                case "ERROR_TOO_MANY_REQUESTS":
                    return "請求過多，請稍後再試";
                default:
                    return "註冊失敗";
            }
        } else {
            return "註冊失敗";
        }
    }
}
