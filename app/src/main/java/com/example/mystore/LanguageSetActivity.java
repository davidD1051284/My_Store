package com.example.mystore;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mystore.ui.personInfo.PersonInfoFragment;

public class LanguageSetActivity extends AppCompatActivity {

    private Button btnChinese, btnEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_set);

        btnChinese = findViewById(R.id.btn_choose_zh);
        btnEnglish = findViewById(R.id.btn_choose_en);


        btnChinese.setOnClickListener(v -> {
            changeLanguage("zh");
        });

        btnEnglish.setOnClickListener(v -> {
            changeLanguage("en");
        });

    }

    private void changeLanguage(String languageCode) {
        LanguageUtils.saveLanguage(this, languageCode); // 切换为英文
        recreate();
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
    }
}