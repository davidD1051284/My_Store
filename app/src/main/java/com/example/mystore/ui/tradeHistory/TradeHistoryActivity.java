package com.example.mystore.ui.tradeHistory;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mystore.R;
import com.example.mystore.database.TradeHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TradeHistoryActivity extends AppCompatActivity {

    private Spinner spTradeType;
    private ListView lvTradeHistory;
    private String selectTradeType;
    private String[] tradeHistType = {"購買紀錄", "販售紀錄"};
    private List<TradeHistory> tradeHistoryList;
    private TradeHistoryAdapter tradeHistoryAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference tradeHistoryRef;

    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_history);

        spTradeType = findViewById(R.id.sp_trade_type_th);
        lvTradeHistory = findViewById(R.id.lv_trade_history);

        tradeHistoryList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
        }

        tradeHistoryRef = FirebaseDatabase.getInstance().getReference("tradeHistory");

        // 交易類型選擇 spinner
        ArrayAdapter<String> spTradeTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tradeHistType);
        spTradeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTradeType.setAdapter(spTradeTypeAdapter);
        spTradeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectTradeType = tradeHistType[position];
                if (selectTradeType.equals("販售紀錄")) {
                    loadSalesHistory();
                } else if (selectTradeType.equals("購買紀錄")) {
                    loadPurchaseHistory();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default selection
                selectTradeType = "購買紀錄";
                loadPurchaseHistory();
            }
        });
    }

    private void loadSalesHistory() {
        Query query = tradeHistoryRef.orderByChild("seller").equalTo(currentUserEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tradeHistoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TradeHistory tradeHistory = snapshot.getValue(TradeHistory.class);
                    if (tradeHistory != null) {
                        tradeHistoryList.add(tradeHistory);
                    }
                }
                tradeHistoryAdapter = new TradeHistoryAdapter(TradeHistoryActivity.this, tradeHistoryList, true);
                lvTradeHistory.setAdapter(tradeHistoryAdapter);
                tradeHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void loadPurchaseHistory() {
        Query query = tradeHistoryRef.orderByChild("buyer").equalTo(currentUserEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tradeHistoryList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TradeHistory tradeHistory = snapshot.getValue(TradeHistory.class);
                    if (tradeHistory != null) {
                        tradeHistoryList.add(tradeHistory);
                    }
                }
                tradeHistoryAdapter = new TradeHistoryAdapter(TradeHistoryActivity.this, tradeHistoryList, false);
                lvTradeHistory.setAdapter(tradeHistoryAdapter);
                tradeHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
