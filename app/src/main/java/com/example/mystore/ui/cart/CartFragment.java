package com.example.mystore.ui.cart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mystore.LoginActivity;
import com.example.mystore.R;
import com.example.mystore.database.UserInfos;
import com.example.mystore.databinding.FragmentCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private ListView lvCartList;
    private TextView tvLumpSum;
    private Button btnPay;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DatabaseReference productReference;
    private DatabaseReference userReference;
    private String userEmail;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CartViewModel cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lvCartList = binding.lvItemListCart;
        tvLumpSum = binding.tvLumpSum;
        btnPay = binding.btnPay;

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItems, this::updateLumpSum);
        lvCartList.setAdapter(cartAdapter);

        productReference = FirebaseDatabase.getInstance().getReference("products");
        userReference = FirebaseDatabase.getInstance().getReference("userInfos");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userEmail = currentUser.getEmail();
        } else {
            Toast.makeText(getContext(), "用戶未登入", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return root;
        }

        saveAllProductIdsToPrefs();

        loadCartItems();
        updateLumpSum();

        btnPay.setOnClickListener(v -> showPaymentDialog());

        return root;
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        cartItems.clear();

        // 顯示載入中視窗
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("載入中...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Set<String> productIds = sharedPreferences.getStringSet("productIds", new HashSet<>());

        AtomicInteger itemCount = new AtomicInteger(productIds.size());

        for (String productId : productIds) {
            productReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("FirebaseData", "DataSnapshot: " + snapshot.getValue());
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartItem.setProductId(productId);
                        cartItems.add(cartItem);
                        cartAdapter.notifyDataSetChanged();
                        updateLumpSum();
                    } else {
                        Log.e("CartItemError", "CartItem is null for productId: " + productId);
                    }

                    // 檢查商品是否都載入完畢
                    if (itemCount.decrementAndGet() == 0) {
                        // 隱藏載入中視窗
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "DatabaseError: " + error.getMessage());
                    // 隱藏載入中視窗
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void updateLumpSum() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getProductPrice() * item.getProductAmount();
        }
        tvLumpSum.setText("總金額 NT$" + total);
    }

    private void showPaymentDialog() {
        userReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        UserInfos userInfo = userSnapshot.getValue(UserInfos.class);
                        if (userInfo != null) {
                            int userBalance = userInfo.getBalance();
                            AtomicInteger totalBalance = new AtomicInteger(0);
                            final boolean[] isStockAvailable = {true};

                            AtomicInteger itemCount = new AtomicInteger(cartItems.size());

                            for (CartItem item : cartItems) {
                                getProductStockFromDb(item.getProductId(), new StockCallback() {
                                    @Override
                                    public void onStockRetrieved(int stock) {
                                        if (item.getProductAmount() > stock) {
                                            isStockAvailable[0] = false;
                                            Toast.makeText(getContext(), item.getProductName() + " 數量不足", Toast.LENGTH_LONG).show();
                                        } else {
                                            totalBalance.addAndGet(item.getProductPrice() * item.getProductAmount());
                                        }

                                        // 如果商品都處理完畢
                                        if (itemCount.decrementAndGet() == 0) {
                                            if (!isStockAvailable[0]) {
                                                return;
                                            }

                                            if (userBalance >= totalBalance.get()) {
                                                // 交易成功
                                                AtomicInteger count = new AtomicInteger(cartItems.size());

                                                for (CartItem item : cartItems) {
                                                    getProductStockFromDb(item.getProductId(), new StockCallback() {
                                                        @Override
                                                        public void onStockRetrieved(int stock) {
                                                            int newStock = stock - item.getProductAmount();
                                                            updateProductStock(item.getProductId(), newStock);

                                                            // 如果所有商品的庫存都更新完畢
                                                            if (count.decrementAndGet() == 0) {
                                                                // 更新並顯示餘額
                                                                int newBalance = userBalance - totalBalance.get();
                                                                updateUserBalance(userSnapshot.getKey(), newBalance);
                                                                showTransactionSuccessDialog(newBalance);

                                                                // 清空購物車
                                                                clearCart();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(DatabaseError error) {
                                                            Toast.makeText(getContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                Toast.makeText(getContext(), "餘額不足", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(DatabaseError error) {
                                        Toast.makeText(getContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // 用戶未儲值過
                            Toast.makeText(getContext(), "餘額不足", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // 沒有商品在購物車中
                    Toast.makeText(getContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
                Toast.makeText(getContext(), "交易失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 更新商品庫存
    private void updateProductStock(String productId, int newStock) {
        productReference.child(productId).child("productAmount").setValue(newStock);
    }

    // 更新用戶餘額
    private void updateUserBalance(String userId, int newBalance) {
        userReference.child(userId).child("balance").setValue(newBalance);
    }

    // 清空購物車
    private void clearCart() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateLumpSum();
    }

    // 交易成功視窗
    private void showTransactionSuccessDialog(int newBalance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("交易成功")
                .setMessage("目前餘額 NT$" + newBalance)
                .setPositiveButton("確定", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public interface StockCallback {
        void onStockRetrieved(int stock);
        void onError(DatabaseError error);
    }

    // 從Database取得商品庫存
    private void getProductStockFromDb(String productId, StockCallback callback) {
        productReference.child(productId).child("productAmount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int stock = snapshot.getValue(Integer.class);
                    callback.onStockRetrieved(stock);
                } else {
                    callback.onError(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 測試用
    private void saveAllProductIdsToPrefs() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> productIds = new HashSet<>();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String productId = productSnapshot.getKey();
                    if (productId != null) {
                        productIds.add(productId);
                    }
                }

                // 更新SharedPreferences
                editor.putStringSet("productIds", productIds);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
