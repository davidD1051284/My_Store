package com.example.mystore;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView searchResultsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.searchInput);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);


        productList = new ArrayList<>();
        populateProductList();

        filteredList = new ArrayList<>(productList);
        productAdapter = new ProductAdapter(filteredList);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(productAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void populateProductList() {
        productList.add(new Product("ASUS 華碩 ROG Zephyrus G14", "GA403UV-0032H8945HS-NBLO｜鉑月銀", R.drawable.sample_product_img));
        productList.add(new Product("ASUS 華碩 ROG Zephyrus G14", "GA403UV-0042E8945HS-NBLO｜日蝕灰", R.drawable.sample_product_img));
    }

    private void filterProducts(String query) {
        filteredList.clear();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
    }
}
