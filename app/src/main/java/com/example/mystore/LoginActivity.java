package com.example.mystore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private EditText etAccount;
    private EditText etPassword;
    private Button btnCreateAccount;
    private Button btnLogin;
    private TextView tvMsgLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取保存的语言设置
        String languageCode = LanguageUtils.getSavedLanguage(this);

        // 应用设置的语言，加载对应的资源
        Locale locale = new Locale(languageCode);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(locale);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, displayMetrics);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 檢查是否已經登入
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // 已登入 導入主頁
            Intent intent = new Intent(LoginActivity.this, NavigationBarActivity.class);
            startActivity(intent);
            finish(); // 避免返回登入頁面
        }

        etAccount = findViewById(R.id.et_account_register);
        etPassword = findViewById(R.id.et_password_register);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnLogin = findViewById(R.id.btn_register);
        tvMsgLogin = findViewById(R.id.tv_msg_login);
        tvMsgLogin.setText("");

        // 註冊按鈕導向
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 登入按鈕
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etAccount.getText().toString();
                String password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    tvMsgLogin.setText("請輸入信箱或密碼");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // 登入成功
                                Intent intent = new Intent(LoginActivity.this, NavigationBarActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // 登入失敗
                                String errorMessage = translateFirebaseError(task.getException());
                                tvMsgLogin.setText(errorMessage);
                            }
                        });
            }
        });
    }

    private String translateFirebaseError(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            switch (authException.getErrorCode()) {
                case "ERROR_INVALID_EMAIL":
                    return "無效的信箱格式";
                case "ERROR_WRONG_PASSWORD":
                    return "密碼錯誤";
                case "ERROR_USER_NOT_FOUND":
                    return "用戶不存在";
                case "ERROR_USER_DISABLED":
                    return "用戶被禁用";
                case "ERROR_TOO_MANY_REQUESTS":
                    return "請求過多，請稍後再試";
                default:
                    return "登入失敗";
            }
        } else {
            return "登入失敗";
        }
    }
}