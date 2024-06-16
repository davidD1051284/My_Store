package com.example.mystore.ui.personInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mystore.LanguageSetActivity;
import com.example.mystore.LoginActivity;
import com.example.mystore.TopUpActivity;
import com.example.mystore.database.TradeHistory;
import com.example.mystore.insert_product;
import com.example.mystore.databinding.FragmentPersonInfoBinding;
import com.example.mystore.database.UserInfos;
import com.example.mystore.ui.tradeHistory.TradeHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonInfoFragment extends Fragment {

    private FragmentPersonInfoBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Button btnSignOut;
    private Button btnTopUp;
    private Button btnLaunchedProduct;
    private Button btnTradeHistory;
    private Button btnLanguage;
    private TextView tvAccount;
    private TextView tvBalance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("userInfos");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PersonInfoViewModel personInfoViewModel =
                new ViewModelProvider(this).get(PersonInfoViewModel.class);

        binding = FragmentPersonInfoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 設置帳號名稱 目前餘額 textView
        tvAccount = binding.tvAccountPersonInfo;
        tvBalance = binding.tvCurrentBalance;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvAccount.setText(currentUser.getEmail());
            loadUserBalance(currentUser.getEmail());
        }

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

        btnTopUp = binding.btnTopUp;
        btnTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
                startActivity(intent);
            }
        });

        btnLaunchedProduct = binding.btnLaunchedProduct;
        btnLaunchedProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), insert_product.class);
                startActivity(intent);
            }
        });

        btnTradeHistory = binding.btnTradeHistory;
        btnTradeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TradeHistoryActivity.class);
                startActivity(intent);
            }
        });

        btnLanguage = binding.btnLanguage;
        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguageSetActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void loadUserBalance(String email) {
        userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserInfos userInfo = snapshot.getValue(UserInfos.class);
                        if (userInfo != null) {
                            tvBalance.setText(String.format("餘額: NT$%d", userInfo.getBalance()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
