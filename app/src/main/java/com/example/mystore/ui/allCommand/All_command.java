package com.example.mystore.ui.allCommand;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.database.Command;
import com.example.mystore.write_command;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class All_command extends AppCompatActivity {
    private ListView lvAllCommand;
    private TextView tvCommandAvg;
    private Button btnWriteCommand;
    private DatabaseReference commandReference;
    private CommandAdapter commandAdapter;
    private ArrayList<Command> commandList;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_command);

        productId = getIntent().getStringExtra("PRODUCT_ID");

        lvAllCommand = findViewById(R.id.lv_all_command);
        tvCommandAvg = findViewById(R.id.tv_command_avg);
        btnWriteCommand = findViewById(R.id.write_command_btn);

        commandList = new ArrayList<>();
        commandAdapter = new CommandAdapter(this, R.layout.command_layout, commandList);
        lvAllCommand.setAdapter(commandAdapter);

        commandReference = FirebaseDatabase.getInstance().getReference("commands");

        loadCommands();

        // 寫評論導向
        btnWriteCommand.setOnClickListener(v -> {
            Intent intent = new Intent(All_command.this, write_command.class);
            intent.putExtra("PRODUCT_ID", productId);
            startActivity(intent);
        });
    }

    private void loadCommands() {
        commandReference.orderByChild("productId").equalTo(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commandList.clear();
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Command command = snapshot.getValue(Command.class);
                    if (command != null) {
                        commandList.add(command);
                        totalRating += command.getRating();
                        count++;
                    }
                }

                if (count > 0) {
                    float avgRating = totalRating / count;
                    tvCommandAvg.setText(String.format("評分:%.2f\t%d則評論", avgRating, count));
                } else {
                    tvCommandAvg.setText("尚無評分");
                }

                commandAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(All_command.this, "無法載入評論", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
