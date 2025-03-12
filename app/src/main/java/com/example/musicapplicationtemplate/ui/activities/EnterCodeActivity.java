package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplicationtemplate.R;

import com.example.musicapplicationtemplate.sqlserver.UserDAO;

public class EnterCodeActivity extends AppCompatActivity {

    TextView txtCodeMsg, textView4;
    EditText edtEnterCode;
    Button btnConfirm, btnCancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_code);

        Intent intentCode = getIntent();
        String username = intentCode.getStringExtra("username");
        String email = intentCode.getStringExtra("email");
        String confirmationCode = intentCode.getStringExtra("confirmationCode");

        textView4 = findViewById(R.id.textView4);
        textView4.setText("Enter the code we sent to " + email + " to activate your account " + username + ".");

        btnConfirm = findViewById(R.id.btnConfirm);
        //confirm code
        btnConfirm.setOnClickListener(v -> {
            txtCodeMsg = findViewById(R.id.txtCodeMsg);
            edtEnterCode = findViewById(R.id.edtEnterCode);
            String codeUserEnter = edtEnterCode.getText().toString();
            if (codeUserEnter.isEmpty()) {
                txtCodeMsg.setText("Please enter your code!");
            } else {
                if (codeUserEnter.equals(confirmationCode)) {
                    UserDAO adb = new UserDAO();
                    System.out.println(adb.changeActiveStatusByUserId(username, true));
                    Intent intent = new Intent(EnterCodeActivity.this, RegisterSuccessActivity.class);
                    startActivity(intent);
                } else {
                    txtCodeMsg.setText("Code is incorrect!");
                }
            }

        });

        //cancle
        btnCancle = findViewById(R.id.btnCancle);
        btnCancle.setOnClickListener(v -> {
            Intent intentLogin = new Intent(EnterCodeActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        });


    }
}