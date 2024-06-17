package com.example.mystore.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mystore.R;
import com.example.mystore.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private DatabaseReference tradeNotifyReference;
    private DatabaseReference productReference;
    private String userEmail;

    private ListView productListView;
    private ArrayList<HomeItem> productList;
    private HomeAdapter productAdapter;
    private EditText etSearchInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        }

        // 搜尋
        etSearchInput = binding.etSearchHome;

        // 監聽etSearch 有變動就搜尋
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                loadProducts(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        userReference = FirebaseDatabase.getInstance().getReference("userInfos");
        tradeNotifyReference = FirebaseDatabase.getInstance().getReference("tradeNotify");
        productReference = FirebaseDatabase.getInstance().getReference("products");

        productListView = binding.lvAllProducts;
        productList = new ArrayList<>();
        productAdapter = new HomeAdapter(getContext(), R.layout.home_product_layout, productList);
        productListView.setAdapter(productAdapter);

        loadProducts("");
        processTradeNotifications();

        return root;
    }

    private void loadProducts(String query) {
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("productName").getValue(String.class);
                    Long priceLong = snapshot.child("productPrice").getValue(Long.class);
                    String imageUrl = snapshot.child("productImage").getValue(String.class);
                    String seller = snapshot.child("seller").getValue(String.class);
                    Integer amount = snapshot.child("productAmount").getValue(Integer.class);
                    String description = snapshot.child("productDescription").getValue(String.class);
                    String id = snapshot.getKey();

                    int price = (priceLong != null) ? priceLong.intValue() : 0;

                    if (name != null && imageUrl != null) {
                        HomeItem product = new HomeItem(id, name, price, imageUrl, amount, description, seller);

                        if (query.isEmpty() || name.toLowerCase().contains(query.toLowerCase())) {
                            productList.add(product);
                        }
                    }
                }
                productAdapter.notifyDataSetChanged();

                if (productList.isEmpty()) {
                    Toast.makeText(getContext(), "無搜尋結果", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "無法載入商品", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTradeNotifications() {
        tradeNotifyReference.orderByChild("seller").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AtomicInteger totalPrice = new AtomicInteger(0);
                    int transactionCount = (int) snapshot.getChildrenCount();

                    for (DataSnapshot notifySnapshot : snapshot.getChildren()) {
                        Integer price = notifySnapshot.child("totalPrice").getValue(Integer.class);
                        if (price != null) {
                            totalPrice.addAndGet(price);
                        }
                    }

                    userReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    Integer balance = userSnapshot.child("balance").getValue(Integer.class);
                                    if (balance != null) {
                                        int newBalance = balance + totalPrice.get();
                                        userSnapshot.getRef().child("balance").setValue(newBalance);

                                        // 刪除tradeNotify中相關的記錄
                                        tradeNotifyReference.orderByChild("seller").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot notifySnapshot : snapshot.getChildren()) {
                                                    notifySnapshot.getRef().removeValue();
                                                }

                                                // 顯示完成的交易筆數和總金額
                                                showTransactionSummary(transactionCount, totalPrice.get());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getContext(), "刪除通知失敗", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "更新餘額失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "載入交易通知失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTransactionSummary(int transactionCount, int totalPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("交易完成")
                .setMessage("完成了 " + transactionCount + " 筆交易，共獲得 NT$" + totalPrice)
                .setPositiveButton("確定", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
