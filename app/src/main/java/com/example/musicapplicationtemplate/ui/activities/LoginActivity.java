package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplicationtemplate.R;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiUserService;
import com.example.musicapplicationtemplate.utils.Utils;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.utils.UserSession;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView txtCreateAccount, txtForgot, txtMsgLogin;
    EditText edtUsername, edtPassword;
    Button btnCheckCon, btnLogin, btnActive;

    UserSession userSession;
    Utils u = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo UserSession
        userSession = new UserSession(getApplicationContext());

        // Kiểm tra nếu user đã đăng nhập trước đó
        if (userSession.isUserLoggedIn()) {
            User user = userSession.getUserSession();
            if (user != null) {
                Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
                intentLogin.putExtra("account", user);
                startActivity(intentLogin);
                finish();
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        txtForgot = findViewById(R.id.txtForgot);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);
        btnCheckCon = findViewById(R.id.checkCon);
        btnActive = findViewById(R.id.btnActive);

        // Xử lý sự kiện khi ForgotAccount được nhấp vào
        txtForgot.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotAccountActivity.class));
            finish();
        });

        // Xử lý sự kiện khi CreateNow được nhấp vào
        txtCreateAccount.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Check connection
        btnCheckCon.setOnClickListener(view -> testDatabaseConnection());

        // Click button login
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        txtMsgLogin = findViewById(R.id.txtMsgLogin);

        String usernameOrEmail = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            btnActive.setVisibility(View.GONE);
            txtMsgLogin.setText("Please enter username and password!");
            return;
        }


        ApiUserService aus = ApiClient.getClient().create(ApiUserService.class);
        Call<User> call = aus.getExistUser(usernameOrEmail, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d("API-USER-RESPONSE-BODY", "RESPONSE: " + response.body());
                    Log.d("API-USER", "User nhận được từ login: " + user.getUsername() + "| user id: " + user.getId() +"| Fullname: "+user.getFirst_name()+" "+user.getLast_name());

                    if (user.isIs_active()) {
                        // Lưu user vào SharedPreferences thông qua UserSession
                        userSession.saveUserSession(user);
                        Intent intentLogin = new Intent(LoginActivity.this, MainActivity.class);
                        intentLogin.putExtra("account", user);
                        startActivity(intentLogin);
                        finish();
                    } else {
                        txtMsgLogin.setText("Your account is not active!");
                        btnActive.setVisibility(View.VISIBLE);

                        btnActive.setOnClickListener(b -> {
                            Intent intentActive = new Intent(LoginActivity.this, EnterCodeActivity.class);
                            String code = u.generateConfirmationCode();
                            Log.d("ConfirmCode", "Confirmation code: " + code);

                            intentActive.putExtra("confirmationCode", code);
                            intentActive.putExtra("username", user.getUsername());
                            intentActive.putExtra("email", user.getEmail());

                            u.sendEmailInBackground(user.getEmail(), "[MUSTIFY] Active Account", user.getUsername(), code, "Mã kích hoạt tài khoản");
                            startActivity(intentActive);
                            finish();
                        });
                    }

                } else {
                    btnActive.setVisibility(View.GONE);
                    txtMsgLogin.setText("Username or password is incorrect!");
                    Log.e("API-USER", "Sai mật khẩu hoặc tài khoản: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API-USER", "Lỗi kết nối: " + t.getMessage());
            }
        });


    }

    private void testDatabaseConnection() {

    }
}
