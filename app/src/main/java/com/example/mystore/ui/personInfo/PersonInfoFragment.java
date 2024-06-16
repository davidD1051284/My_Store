package com.example.mystore.ui.personInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mystore.LoginActivity;
import com.example.mystore.TopUpActivity;
import com.example.mystore.database.TradeHistory;
import com.example.mystore.insert_product;
import com.example.mystore.databinding.FragmentPersonInfoBinding;
import com.example.mystore.ui.tradeHistory.TradeHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonInfoFragment extends Fragment {

    private FragmentPersonInfoBinding binding;
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private Button btnTopUp;
    private Button btnLaunchedProduct;
    private Button btnTradeHistory;
    private TextView tvAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonInfoViewModel personInfoViewModel =
                new ViewModelProvider(this).get(PersonInfoViewModel.class);

        binding = FragmentPersonInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 設置帳號名稱textView
        tvAccount = binding.tvAccountPersonInfo;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvAccount.setText(currentUser.getEmail());
        } else {
            Toast.makeText(getContext(), "用戶未登入", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return root;
        }

        // 登出
        btnSignOut = binding.btnSignOut;
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // 儲值導向
        btnTopUp = binding.btnTopUp;
        btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
                startActivity(intent);
            }
        });

        // 上架商品導向
        btnLaunchedProduct = binding.btnLaunchedProduct;
        btnLaunchedProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), insert_product.class);
                startActivity(intent);
            }
        });

        // 交易紀錄導向
        btnTradeHistory = binding.btnTradeHistory;
        btnTradeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TradeHistoryActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
