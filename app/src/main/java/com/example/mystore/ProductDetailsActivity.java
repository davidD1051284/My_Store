package com.example.mystore;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDetails, seeMore, review1, review2;
    private LinearLayout moreDetailsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDetails = findViewById(R.id.productDetails);
        seeMore = findViewById(R.id.seeMore);
        review1 = findViewById(R.id.review1);
        review2 = findViewById(R.id.review2);
        moreDetailsLayout = findViewById(R.id.moreDetailsLayout);

        // Here you can set the actual product details dynamically
        productName.setText("ASUS 華碩 ROG Zephyrus G14 2024 GA403 14吋筆電 R9/4060");
        productDetails.setText("商品型號：GA403UV-0032H8945HS-NBLO｜鉑月銀\nGA403UV-0042E8945HS-NBLO｜日蝕灰\n產品保固：２年\n產品特色：\n搭載 AMD Ryzen™ 9 8945HS 處理器，輕鬆享受 Windows 11 Home 作業系統的 AI 筆電的遊戲和創造體驗\n最高支援 NVIDIA® GeForce RTX™ 4060 筆記型電腦 GPU，讓您高效順暢進行遊戲和創作\nROG 首款 3K OLED 120Hz/0.2ms Nebula 霓真技術電競螢幕，支援 VESA DisplayHDR True Black 500\n超薄 1.59 cm、1.5 kg、14 吋優質鋁製機殼\nROG 智慧散熱技術、Tri-fan 三風扇技術、液態金屬、改良散熱管和第 2 代 Arc Flow Fans™\n全新可自訂 Slash 動態燈效，盡顯個人態度");
        review1.setText("1\nNameAAAA\n評分: 10\n2023/10/31");
        review2.setText("2\nNameBBB\n評分: 9\n2023/10/31");

        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreDetailsLayout.getVisibility() == View.GONE) {
                    moreDetailsLayout.setVisibility(View.VISIBLE);
                    seeMore.setText("隱藏詳細資訊");
                } else {
                    moreDetailsLayout.setVisibility(View.GONE);
                    seeMore.setText("查看更多");
                }
            }
        });
    }
}
