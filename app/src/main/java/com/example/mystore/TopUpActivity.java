package com.example.mystore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class TopUpActivity extends AppCompatActivity {
    private Spinner spMoney;
    private Spinner spPaymentMethod;
    private Button btnTopUp;
    private String[] money = {"500", "1000", "2000", "3000", "4000", "5000"};
    private String[] paymentMethods = {"匯款", "信用卡"};
    private int selectMoney;
    private String selectPaymentMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        spMoney = findViewById(R.id.sp_money);
        spPaymentMethod = findViewById(R.id.sp_payment_method);
        btnTopUp = findViewById(R.id.btn_top_up);

        // 金額選擇 spinner
        ArrayAdapter<String> spMoneyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, money);
        spMoneyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMoney.setAdapter(spMoneyAdapter);
        AdapterView.OnItemSelectedListener spMoneyListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectMoney = Integer.parseInt(money[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectMoney = 0;
            }
        };
        spMoney.setOnItemSelectedListener(spMoneyListener);

        // 支付方式選擇 spinner
        ArrayAdapter<String> spPayMethodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        spPayMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(spPayMethodAdapter);
        AdapterView.OnItemSelectedListener spPayMethodListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectPaymentMethod = paymentMethods[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectPaymentMethod = "未選擇";
            }
        };
        spPaymentMethod.setOnItemSelectedListener(spPayMethodListener);

        // 確定儲值 button
        View.OnClickListener btnTopUpListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        btnTopUp.setOnClickListener(btnTopUpListener);
    }
}