package com.example.musicapplicationtemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import model.User;
import sqlserver.UserDAO;

public class ForgotAccountActivity extends AppCompatActivity {
    EditText edtForgotUserEmail;
    TextView txtForgotMsg;
    Button btnFindAccount, btnForgotCancel, btnGetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UserDAO adb = new UserDAO();
        edtForgotUserEmail = findViewById(R.id.edtForgotUserEmail);
        txtForgotMsg = findViewById(R.id.txtForgotMsg);
        btnFindAccount = findViewById(R.id.btnFindAccount);
        btnForgotCancel = findViewById(R.id.btnForgotCancel);
        btnGetPassword = findViewById(R.id.btnGetPassword);

        btnFindAccount.setOnClickListener(v -> {
            String usernameOrEmail = edtForgotUserEmail.getText().toString();

            User a = adb.getUserByUsernameOrEmail(usernameOrEmail);
            if (a != null) {
                txtForgotMsg.setText("Account found: " + a.getUsername());
                btnGetPassword.setVisibility(View.VISIBLE);
            } else {
                txtForgotMsg.setText("Account not found");
                btnGetPassword.setVisibility(View.GONE);
            }
        });


        btnGetPassword.setOnClickListener(v -> {
             String usernameOrEmail = edtForgotUserEmail.getText().toString();
             User a = adb.getUserByUsernameOrEmail(usernameOrEmail);
             if (a != null) {
                 Intent intent1 = new Intent(ForgotAccountActivity.this, GetPasswordActivity.class);
                 intent1.putExtra("account", a);
                 startActivity(intent1);
             }else{
                 Toast.makeText(ForgotAccountActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
             }
        });

        btnForgotCancel.setOnClickListener(v -> {
            finish();
        });

    }
}