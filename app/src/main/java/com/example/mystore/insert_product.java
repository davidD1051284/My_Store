package com.example.mystore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class insert_product extends AppCompatActivity {
    private static final String DATABASE_NAME = "my_store.db";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_TABLE = "shop_cart";
    private Button btnSelectImage, btnInsert;
    private EditText etProductName, etProductPrice, etProductAmount, etProductDescription;
    private ImageView ivSelectImage;
    private Uri productImageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        productImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = getResizedBitmap(productImageUri, 300);
                            ivSelectImage.setImageBitmap(bitmap);
                            ivSelectImage.setBackgroundResource(0); // clean ImageView border
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
            Toast.makeText(this, "請填入所有商品資訊和選擇商品圖片", Toast.LENGTH_SHORT).show();
            return;
        }

        btnInsert.setEnabled(false);
        uploadToFirebase(productName, productPrice, productAmount, productDescription, productImageUri);
    }

    private void uploadToFirebase(String productName, int productPrice, int productAmount, String productDescription, Uri productImageUri) {
        String uniqueID = UUID.randomUUID().toString();
        StorageReference imageRef = storageReference.child("images/" + uniqueID);

        try {
            Bitmap bitmap = getResizedBitmap(productImageUri, 300);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveProductToDataBase(productName, productPrice, productAmount, productDescription, imageUrl);
            })).addOnFailureListener(e -> {
                Toast.makeText(insert_product.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                btnInsert.setEnabled(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
            btnInsert.setEnabled(true);
        }
    }

    private void saveProductToDataBase(String productName, int productPrice, int productAmount, String productDescription, String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            Map<String, Object> productData = new HashMap<>();
            productData.put("productName", productName);
            productData.put("productPrice", productPrice);
            productData.put("productAmount", productAmount);
            productData.put("productDescription", productDescription);
            productData.put("productImage", imageUrl);
            productData.put("seller", userEmail);

            databaseReference.child("products").push().setValue(productData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(insert_product.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    // Clear the fields
                    etProductName.setText("");
                    etProductPrice.setText("");
                    etProductAmount.setText("");
                    etProductDescription.setText("");
                    ivSelectImage.setImageResource(0);
                } else {
                    Toast.makeText(insert_product.this, "Failed to add product", Toast.LENGTH_SHORT);
                }
                btnInsert.setEnabled(true);
            });
        } else {
            Toast.makeText(insert_product.this, "User not logged in", Toast.LENGTH_SHORT).show();
            btnInsert.setEnabled(true);
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