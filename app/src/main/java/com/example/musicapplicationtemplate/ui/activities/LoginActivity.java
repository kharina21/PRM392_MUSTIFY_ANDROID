package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;

import Utils.Utils;
import model.User;
import sqlserver.UserDAO;
import sqlserver.DBContext;

import android.util.Log;

import com.example.musicapplicationtemplate.R;

public class LoginActivity extends AppCompatActivity {
    TextView txtCreateAccount;
    TextView txtForgot;
    TextView txtMsgLogin;
    EditText edtUsername;
    EditText edtPassword;
    Button btnCheckCon;
    Button btnLogin, btnActive;

    Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Utils u = new Utils();
        txtForgot = findViewById(R.id.txtForgot);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);
        btnCheckCon = findViewById(R.id.checkCon);
        btnActive = findViewById(R.id.btnActive);
        // Xử lý sự kiện khi ForgotAccount được nhấp vào
        txtForgot.setOnClickListener(v -> {
            Intent intentForgot = new Intent(LoginActivity.this, ForgotAccountActivity.class);
            startActivity(intentForgot);
            finish();
        });
        // Xử lý sự kiện khi CreateNow được nhấp vào
        txtCreateAccount.setOnClickListener(view -> {
            Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intentRegister);
            finish();
        });

        //check connection

        btnCheckCon.setOnClickListener(view -> {
            testDatabaseConnection();
        });

        //click button login
        btnLogin.setOnClickListener(v -> {
            edtUsername = findViewById(R.id.edtUsername);
            edtPassword = findViewById(R.id.edtPassword);
            txtMsgLogin = findViewById(R.id.txtMsgLogin);

            String msgLogin;
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                btnActive.setVisibility(View.GONE);
                txtMsgLogin.setText("Please enter username and password!");
            } else {
                UserDAO adb = new UserDAO();
                User a = adb.getExistUser(username, password);
                if (a != null) {
                    if (a.isIs_active() == true) {
                        Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
                        intentLogin.putExtra("account", a);
                        startActivity(intentLogin);
                        finish();
                    } else {
                        txtMsgLogin.setText("Your account is not active!");
                        btnActive.setVisibility(View.VISIBLE);

                        btnActive.setOnClickListener(b -> {
                            Intent intentActive = new Intent(LoginActivity.this, EnterCodeActivity.class);
                            String code = u.generateConfirmationCode();
                            Log.d("ConfirmCode", "Confirmation code:" + code);
                            intentActive.putExtra("confirmationCode", code);
                            intentActive.putExtra("username", a.getUsername());
                            intentActive.putExtra("email", a.getEmail());
                            u.sendEmailInBackground(a.getEmail(), "[MUSTIFY] Active Account", a.getUsername(), code, "Mã kích hoạt tài khoản");
                            startActivity(intentActive);
                            finish();
                        });

                    }

                } else {
                    btnActive.setVisibility(View.GONE);
                    txtMsgLogin.setText("Username or password is incorrect!");
                }
            }

        });

    }

    private void testDatabaseConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBContext dbContext = new DBContext();
                boolean isConnected = dbContext.testConnection();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnected) {
                            Toast.makeText(LoginActivity.this, "Database connection successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Database connection failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}