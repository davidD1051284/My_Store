package com.example.mystore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mystore.database.command_database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class write_command extends AppCompatActivity {

    private EditText commentEditText;
    private RadioGroup ratingRadioGroup;
    private Button submitButton;
    private Button back_to_inform;
    private DatabaseReference databaseReference;
    float rate = 0;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_command);

        Intent intent = getIntent();
        String productID = intent.getStringExtra("PRODUCT_ID");

        commentEditText = findViewById(R.id.comment_edit_text);
        ratingRadioGroup = findViewById(R.id.rating_group);
        submitButton = findViewById(R.id.release_btn);
        back_to_inform = findViewById(R.id.BacktoInform02_btn);

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview(rate, productID, userEmail);
            }
        });

        back_to_inform.setOnClickListener(v -> {
            Intent intent02 = new Intent(write_command.this, product_information.class);
            startActivity(intent02);
        });

        RadioGroup.OnCheckedChangeListener ratingListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.onestar_rb:
                        rate = 1.0f;
                        break;
                    case R.id.twostar_rb:
                        rate = 2.0f;
                        break;
                    case R.id.threestar_rb:
                        rate = 3.0f;
                        break;
                    case R.id.fourstar_rb:
                        rate = 4.0f;
                        break;
                    case R.id.fivestar_rb:
                        rate = 5.0f;
                        break;
                    default:
                        rate = 0;
                        break;
                }
            }
        };

        ratingRadioGroup.setOnCheckedChangeListener(ratingListener);
        submitReview(2, productID, userEmail);//test用
    }

    private void submitReview(float rate, String productID, String userEmail) {
        System.out.println(rate + "AAAA");

        String comment = commentEditText.getText().toString().trim();
        int selectedRadioButtonId = ratingRadioGroup.getCheckedRadioButtonId();

        if (comment.isEmpty()) {
            Toast.makeText(this, "請輸入評論", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rate == 0) {
            Toast.makeText(this, "請選擇評分", Toast.LENGTH_SHORT).show();
            return;
        }

        String reviewId = databaseReference.push().getKey();
        command_database review = new command_database(userEmail, comment, productID, rate);
        if (reviewId != null) {
            databaseReference.child(reviewId).setValue(review);
            Toast.makeText(this, "評論已提交", Toast.LENGTH_SHORT).show();
            commentEditText.setText("");
            ratingRadioGroup.clearCheck();
        }
    }

}