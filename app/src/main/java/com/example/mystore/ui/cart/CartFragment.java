package com.example.mystore.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.mystore.R;
import com.example.mystore.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private ListView lvCartList;
    private TextView tvLumpSum;
    private Button btnPay;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CartViewModel cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //lvCartList = binding.lvCartList;
        tvLumpSum = binding.tvLumpSum;
        btnPay = binding.btnPay;

        // 購物車列表
        //cartItems = new ArrayList<>();
        //cartAdapter = new CartAdapter(getContext(), cartItems, this::updateLumpSum);
        //lvCartList.setAdapter(cartAdapter);

        loadCartItems();

        // 更新總金額
        updateLumpSum();

        btnPay.setOnClickListener(v -> showPaymentDialog());

        return root;
    }

    private void loadCartItems() {

    }

    private void updateLumpSum() {

    }

    private void showPaymentDialog() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
