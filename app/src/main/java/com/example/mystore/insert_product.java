package com.example.mystore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class insert_product extends AppCompatActivity {
    private static final String DATABASE_NAME = "my_store.db";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_TABLE = "shop_cart";
    private Button btnSelectImage, btnInsert;
    private EditText etProductName, etProductPrice, etProductAmount, etProductDescription;
    private ImageView ivSelectImage;
    private Uri productImageUri;

    private ActivityResultLauncher<Intent> selectImageLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        btnSelectImage = findViewById(R.id.btn_select_image);
        btnInsert = findViewById(R.id.btn_insert);

        ivSelectImage = findViewById(R.id.iv_select_image);
        etProductName = findViewById(R.id.et_insert_product_name);
        etProductPrice = findViewById(R.id.et_insert_product_price);
        etProductAmount = findViewById(R.id.et_insert_product_amount);
        etProductDescription = findViewById(R.id.et_insert_product_description);

        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        productImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = getResizedBitmap(productImageUri, 300);
                            ivSelectImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> openImageChooser());

        btnInsert.setOnClickListener(v -> addProduct());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        selectImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void addProduct() {
        String productName = etProductName.getText().toString().trim();
        int productPrice = Integer.parseInt(etProductPrice.getText().toString().trim());
        int productAmount = Integer.parseInt(etProductAmount.getText().toString().trim());
        String productDescription = etProductDescription.getText().toString().trim();

        if (productName.isEmpty() || productPrice <= 0 || productAmount <= 0 || productDescription.isEmpty()) {
            return;
        }

        try {
            byte[] productImage = getBytesFromUri(productImageUri, 300);

            SqlDataBaseHelper dbHelper = new SqlDataBaseHelper(this, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLE);
            dbHelper.insertProduct(productName, productPrice, productAmount, productDescription, productImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] getBytesFromUri(Uri uri, int maxSize) throws IOException {
        Bitmap bitmap = getResizedBitmap(uri, maxSize);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap getResizedBitmap(Uri uri, int maxSize) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        int inSampleSize = 1;
        if (options.outHeight > maxSize || options.outWidth > maxSize) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            while ((halfHeight / inSampleSize) >= maxSize && (halfWidth / inSampleSize) >= maxSize) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        inputStream = getContentResolver().openInputStream(uri);
        Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return resizedBitmap;
    }
}
