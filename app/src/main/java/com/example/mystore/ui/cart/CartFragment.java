package com.example.mystore.ui.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.mystore.RegisterActivity;
import com.example.mystore.database.UserInfos;
import com.example.mystore.databinding.FragmentCartBinding;
import com.example.mystore.ui.cart.CartItem;
import com.example.mystore.ui.cart.CartAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
            Toast.makeText(getContext(), "未知的用戶", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return root;
        }

        loadCartItems();
        updateLumpSum();

        btnPay.setOnClickListener(v -> showPaymentDialog());

        return root;
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        cartItems.clear();
        for (String productId : sharedPreferences.getAll().keySet()) {
            productReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartItems.add(cartItem);
                        cartAdapter.notifyDataSetChanged();
                        updateLumpSum();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void updateLumpSum() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getProductPrice() * item.getProductStock();
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
                            int totalAmount = 0;
                            boolean isStockAvailable = true;

                            for (CartItem item : cartItems) {
                                if (item.getProductStock() <= 0) {
                                    isStockAvailable = false;
                                    break;
                                }
                                totalAmount += item.getProductPrice() * item.getProductStock();
                            }

                            if (!isStockAvailable) {
                                Toast.makeText(getContext(), "商品數量不足", Toast.LENGTH_LONG).show();
                            } else if (userBalance < totalAmount) {
                                Toast.makeText(getContext(), "餘額不足", Toast.LENGTH_LONG).show();
                            } else {
                                // 交易成功
                                for (CartItem item : cartItems) {
                                    int newStock = item.getProductStock() - item.getProductStock();
                                    productReference.child(item.getProductId()).child("productStock").setValue(newStock);
                                }
                                int newBalance = userBalance - totalAmount;
                                userReference.child(userSnapshot.getKey()).child("balance").setValue(newBalance);

                                Toast.makeText(getContext(), "交易成功", Toast.LENGTH_LONG).show();
                                // 清空購物車
                                SharedPreferences.Editor editor = getContext().getSharedPreferences("CartPrefs", Context.MODE_PRIVATE).edit();
                                editor.clear();
                                editor.apply();
                                cartItems.clear();
                                cartAdapter.notifyDataSetChanged();
                                updateLumpSum();
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "交易失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "交易失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
