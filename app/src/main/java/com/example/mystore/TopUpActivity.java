package com.example.mystore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mystore.database.UserInfos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TopUpActivity extends AppCompatActivity {
    private Spinner spMoney;
    private Spinner spPaymentMethod;
    private Button btnTopUp;
    private String[] money = {"500", "1000", "2000", "3000", "4000", "5000"};
    private String[] paymentMethods = {"匯款", "信用卡"};
    private int selectMoney;
    private String selectPaymentMethod;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        spMoney = findViewById(R.id.sp_money);
        spPaymentMethod = findViewById(R.id.sp_payment_method);
        btnTopUp = findViewById(R.id.btn_top_up);

        mAuth = FirebaseAuth.getInstance();

        // 金額選擇 spinner
        ArrayAdapter<String> spMoneyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, money);
        spMoneyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoney.setAdapter(spMoneyAdapter);
        spMoney.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectMoney = Integer.parseInt(money[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectMoney = 0;
            }
        });

        // 支付方式選擇 spinner
        ArrayAdapter<String> spPayMethodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        spPayMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(spPayMethodAdapter);
        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectPaymentMethod = paymentMethods[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectPaymentMethod = "未選擇";
            }
        });

        // 確定儲值 button
        btnTopUp.setOnClickListener(view -> {
            if (!selectPaymentMethod.equals("未選擇")) {
                topUpBalance(selectMoney);
            }
        });
    }

    private void topUpBalance(int money) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(TopUpActivity.this, "用戶未登錄", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TopUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String userEmail = currentUser.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("userInfos");

        ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User存在，更新balance
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        UserInfos userInfo = userSnapshot.getValue(UserInfos.class);
                        if (userInfo != null) {
                            int newBalance = userInfo.getBalance() + money;
                            userSnapshot.getRef().child("balance").setValue(newBalance)
                                    .addOnSuccessListener(aVoid -> {
                                        showSuccessDialog(newBalance);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(TopUpActivity.this, "儲值失敗", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                } else {
                    // User不存在，新建userInfo
                    String userId = ref.push().getKey();
                    UserInfos newUser = new UserInfos(userEmail, money);
                    ref.child(userId).setValue(newUser)
                            .addOnSuccessListener(aVoid -> {
                                showSuccessDialog(money);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TopUpActivity.this, "儲值失敗", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TopUpActivity.this, "讀取失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog(int balance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TopUpActivity.this);
        builder.setTitle("儲值成功")
                .setMessage("目前餘額：" + balance + "元")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}