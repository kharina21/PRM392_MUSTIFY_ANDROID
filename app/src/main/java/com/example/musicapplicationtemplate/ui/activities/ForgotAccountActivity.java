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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapplicationtemplate.R;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiUserService;
import com.example.musicapplicationtemplate.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        edtForgotUserEmail = findViewById(R.id.edtForgotUserEmail);
        txtForgotMsg = findViewById(R.id.txtForgotMsg);
        btnFindAccount = findViewById(R.id.btnFindAccount);
        btnForgotCancel = findViewById(R.id.btnForgotCancel);
        btnGetPassword = findViewById(R.id.btnGetPassword);

//        UserDAO adb = new UserDAO();
        ApiUserService aus = ApiClient.getClient().create(ApiUserService.class);
        btnFindAccount.setOnClickListener(v -> {
            String usernameOrEmail = edtForgotUserEmail.getText().toString();

            Call<User> call = aus.getUserByUsernameOrEmail(usernameOrEmail);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User a = response.body();
                    if (a != null) {
                        txtForgotMsg.setText("Account found: " + a.getUsername());
                        btnGetPassword.setVisibility(View.VISIBLE);
                    } else {
                        txtForgotMsg.setText("Account not found");
                        btnGetPassword.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });

        });


        btnGetPassword.setOnClickListener(v -> {
             String usernameOrEmail = edtForgotUserEmail.getText().toString();
            Call<User> call = aus.getUserByUsernameOrEmail(usernameOrEmail);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User a = response.body();
                    if (a != null) {
                        Intent intent1 = new Intent(ForgotAccountActivity.this, GetPasswordActivity.class);
                        intent1.putExtra("account", a);
                        startActivity(intent1);
                    }else{
                        Toast.makeText(ForgotAccountActivity.this, "Account not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });

        });

        btnForgotCancel.setOnClickListener(v -> {
            finish();
        });

    }
}