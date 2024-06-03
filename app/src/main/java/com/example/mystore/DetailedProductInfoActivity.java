package com.example.myapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailedProductInfoActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDetailedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product_info);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDetailedInfo = findViewById(R.id.productDetailedInfo);

        // Here you can set the actual product details dynamically
        productName.setText("Product Name Example");
        productDetailedInfo.setText("Detailed product information goes here...");
    }
}
