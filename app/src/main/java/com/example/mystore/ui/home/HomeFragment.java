package com.example.mystore.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mystore.LoginActivity;
import com.example.mystore.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private DatabaseReference tradeNotifyReference;
    private String userEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        userReference = FirebaseDatabase.getInstance().getReference("userInfos");
        tradeNotifyReference = FirebaseDatabase.getInstance().getReference("tradeNotify");

        processTradeNotifications();

        return root;
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
