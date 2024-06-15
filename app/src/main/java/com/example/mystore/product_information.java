package com.example.mystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.database.Products;
import com.example.mystore.ui.home.HomeItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.Set;

public class product_information extends AppCompatActivity {

    private static final String TAG = "pro_info";
    private ImageView productImage;
    private Button btnAddCart;
    private SharedPreferences sharedPreferences;
    private static final String CART_PREFS = "CartPrefs";
    private static final String CART_ITEMS_KEY = "productIds";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information);

        sharedPreferences = getSharedPreferences(CART_PREFS, MODE_PRIVATE);

        productImage = findViewById(R.id.iv_product_information_image);
        btnAddCart = findViewById(R.id.btn_add_cart);


        String product_id = getIntent().getStringExtra("PRODUCT_ID");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products").child(product_id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "DataSnapshot exists: " + dataSnapshot.toString());
                    HomeItem product = new HomeItem();
                    product.setImageUrl(dataSnapshot.child("productImage").getValue(String.class));
                    if (product != null) {
                        String url = product.getImageUrl();
                        Log.d(TAG, "Image URL: " + url);
                        if (url != null && !url.isEmpty()) {
                            Glide.with(product_information.this).load(url).into(productImage);
                        } else {
                            Log.w(TAG, "Image URL is null or empty");
                        }
                    } else {
                        Log.w(TAG, "Product is null");
                    }
                } else {
                    Log.w(TAG, "DataSnapshot does not exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnAddCart.setOnClickListener(v -> {
            addProductToCart(product_id);
        });
    }

    // 添加至購物車
    private void addProductToCart(String productId) {
        Set<String> cartItems = sharedPreferences.getStringSet(CART_ITEMS_KEY, new HashSet<>());

        if (cartItems.contains(productId)) {
            Toast.makeText(this, "已有相同商品", Toast.LENGTH_SHORT).show();
        } else {
            cartItems.add(productId);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(CART_ITEMS_KEY, cartItems);
            editor.apply();
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }
    }
}
