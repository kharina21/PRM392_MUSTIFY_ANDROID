package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.api.ApiService.ApiUserService;
import com.example.musicapplicationtemplate.utils.Utils;
import com.example.musicapplicationtemplate.model.User;
import android.util.Log;

import com.example.musicapplicationtemplate.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    Spinner spinnerGender;
    TextView txtMsgRegister;
    TextView txtLogin;
    EditText edtRegUsername;
    EditText edtRegPassword;
    EditText edtRegRePassword;
    EditText edtEmail, edtAddress;
    EditText edtFirstName, edtLastName;

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerGender = findViewById(R.id.spinnerGender);
        txtLogin = findViewById(R.id.txtLogin);

        //spinner
        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Layout khi dropdown hiển thị
        spinnerGender.setAdapter(adapter);

        //
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtRegUsername = findViewById(R.id.edtRegUsername);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        edtRegRePassword = findViewById(R.id.edtRegRePassword);
        txtMsgRegister = findViewById(R.id.txtMsgRegister);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String address = edtAddress.getText().toString();
            String username = edtRegUsername.getText().toString();
            String password = edtRegPassword.getText().toString();
            String rePassword = edtRegRePassword.getText().toString();
            String email = edtEmail.getText().toString();
            String firstName = edtFirstName.getText().toString();
            String lastName = edtLastName.getText().toString();
            String genderStr = spinnerGender.getSelectedItem().toString();
            boolean gender = genderStr.equals("Male");

            if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty() || email.isEmpty()
                    || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()) {
                txtMsgRegister.setText("Please fill all fields!");
                return;
            }
            if (!password.equals(rePassword)) {
                edtEmail.setText(email);
                edtRegUsername.setText(username);
                edtAddress.setText(address);
                edtFirstName.setText(firstName);
                edtLastName.setText(lastName);
                edtRegPassword.setText("");
                edtRegRePassword.setText("");
                txtMsgRegister.setText("Password and Re-password do not match!");
                return;
            }

            ApiUserService aus = ApiClient.getClient().create(ApiUserService.class);
            Call<ApiResponse> call = aus.checkEmailExist(email);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if(response.isSuccessful() && response.body()!=null && response.body().isExists())  {
                        txtMsgRegister.setText("Email already exists!");
                        edtRegUsername.setText(username);
                        edtAddress.setText(address);
                        edtFirstName.setText(firstName);
                        edtLastName.setText(lastName);
                        edtEmail.setText("");
                        edtRegPassword.setText("");
                        edtRegRePassword.setText("");
                    }else{
                        Call<ApiResponse> call1 = aus.checkUsernameExist(username);
                        call1.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if(response.isSuccessful() && response.body() != null && response.body().isExists()) {
                                    txtMsgRegister.setText("Username already exists!");
                                    edtRegUsername.setText("");
                                    edtEmail.setText(email);
                                    edtAddress.setText(address);
                                    edtFirstName.setText(firstName);
                                    edtLastName.setText(lastName);
                                    edtRegPassword.setText("");
                                    edtRegRePassword.setText("");
                                }else if(!password.equals(rePassword)){
                                    edtRegPassword.setText("");
                                    edtRegRePassword.setText("");
                                    txtMsgRegister.setText("Password and Re-password do not match!");
                                }else {
                                    User a = new User();
                                    a.setEmail(email);
                                    a.setUsername(username);
                                    a.setPassword(password);
                                    a.setFirst_name(firstName);
                                    a.setLast_name(lastName);
                                    a.setGender(gender);
                                    a.setAddress(address);
                                    a.setAccount_type(2);
                                    a.setIs_active(false);
                                    aus.addUser(a).enqueue(new Callback<ApiResponse>() {
                                        @Override
                                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                            if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                                                Log.d("ADD-USER","ADD USER SUCCESS - Username: "+username);
                                            }else{
                                                Log.d("ADD-USER","ADD USER Failed - Username: "+username);
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                                        }
                                    });
                                    Utils u = new Utils();
                                    String confirmationCode = u.generateConfirmationCode();
                                    Log.d("RegisterActivity", "Confirmation Code: " + confirmationCode);

                                    Intent intentRegSuccess = new Intent(RegisterActivity.this, EnterCodeActivity.class);
                                    intentRegSuccess.putExtra("username", username);
                                    intentRegSuccess.putExtra("email",email);
                                    intentRegSuccess.putExtra("confirmationCode", confirmationCode);
                                    u.sendEmailInBackground(a.getEmail(),"[MUSTIFY] Active Account",a.getUsername(),confirmationCode,"Mã kích hoạt tài khoản: ");
                                    startActivity(intentRegSuccess);
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {

                }
            });





        });

        //Login
        txtLogin.setOnClickListener(v -> {
            finish();
        });


    }
}
