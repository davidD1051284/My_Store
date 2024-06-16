package com.example.mystore;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ivProductImage = findViewById(R.id.productImage);
        tvProductName = findViewById(R.id.productName);
        tvProductDetails = findViewById(R.id.productDetails);

        String name = getIntent().getStringExtra("PRODUCT_NAME");
        String description = getIntent().getStringExtra("PRODUCT_DESCRIPTION");
        String url = getIntent().getStringExtra("PRODUCT_IMAGE");

        tvProductName.setText(name);
        tvProductDetails.setText(fromHtml(description));
        tvProductDetails.setMovementMethod(new ScrollingMovementMethod());

        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).into(ivProductImage);
        }

    }

    private Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
