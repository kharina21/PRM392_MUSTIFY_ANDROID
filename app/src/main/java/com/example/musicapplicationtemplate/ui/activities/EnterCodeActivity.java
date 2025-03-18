package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplicationtemplate.R;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.api.ApiService.ApiUserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        ApiUserService aus = ApiClient.getClient().create(ApiUserService.class);

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
//                    UserDAO adb = new UserDAO();
                    Call<ApiResponse> call = aus.changeActiveStatusByUsername(username,true);
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if(response.isSuccessful() && response.body()!=null){
                                Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EnterCodeActivity.this, RegisterSuccessActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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