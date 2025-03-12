package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicapplicationtemplate.R;

import com.example.musicapplicationtemplate.utils.Utils;
import com.example.musicapplicationtemplate.model.User;

public class GetPasswordActivity extends AppCompatActivity {
    Utils u = new Utils();
    String confirmationCode = u.generateConfirmationCode();

    TextView txtGetPassMsg1, txtGetPassMsg, txtGetPassLogin;
    EditText edtGetPassEnterCode;
    Button btnGetPassConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent1 = getIntent();
        User a = (User) intent1.getSerializableExtra("account");
        u.sendEmailInBackground(a.getEmail(), "Get your password", a.getUsername(), confirmationCode, "Your confirmation code is: ");

        txtGetPassMsg1 = findViewById(R.id.edtGetPassMsg1);
        txtGetPassMsg = findViewById(R.id.txtGetPassMsg);
        txtGetPassLogin = findViewById(R.id.txtGetPassLogin);
        edtGetPassEnterCode = findViewById(R.id.edtGetPassEnterCode);
        btnGetPassConfirm = findViewById(R.id.btnGetPassConfirm);

        txtGetPassMsg1.setText("Confirmation code has sent to your email!\n" + a.getEmail());

        //Button confirm
        btnGetPassConfirm.setOnClickListener(v -> {
            String code = edtGetPassEnterCode.getText().toString();
            Log.e("code", code);
            if (code.equals(confirmationCode)) {
                u.sendEmailInBackground(a.getEmail(), "Get your password", a.getUsername(), a.getPassword(), "Your password is: ");
               edtGetPassEnterCode.setVisibility(View.GONE);
               btnGetPassConfirm.setVisibility(View.GONE);
               txtGetPassMsg.setVisibility(View.VISIBLE);
               txtGetPassMsg.setText("Your password has sent to your email!");
            }
        });

        //Text Login
        txtGetPassLogin.setOnClickListener(v -> {
            Intent intent2 = new Intent(GetPasswordActivity.this, LoginActivity.class);
            startActivity(intent2);
        });

    }
}