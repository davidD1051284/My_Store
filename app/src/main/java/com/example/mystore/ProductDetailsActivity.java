package com.example.mystore;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDetails, seeMore, review1, review2;

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

        // Here you can set the actual product details dynamically
        productName.setText("Product Name Example");
        productDetails.setText("商品數量: 1\n品牌: XXXX\n螢幕尺寸: 15吋\n類型: 其他");
        review1.setText("1\nNameAAAA\n評分: 10\n2023/10/31");
        review2.setText("2\nNameBBB\n評分: 9\n2023/10/31");
    }
}
