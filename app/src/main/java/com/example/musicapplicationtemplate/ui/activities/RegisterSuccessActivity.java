package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplicationtemplate.R;

public class RegisterSuccessActivity extends AppCompatActivity {
    Button btnLoginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_success);

        btnLoginHere = findViewById(R.id.btnLoginHere);
        btnLoginHere.setOnClickListener(v -> {
            Intent intentLogin = new Intent(RegisterSuccessActivity.this, LoginActivity.class);
            startActivity(intentLogin);
        });

    }
}