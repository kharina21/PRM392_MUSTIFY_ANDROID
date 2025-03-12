package com.example.musicapplicationtemplate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplicationtemplate.utils.Utils;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.sqlserver.UserDAO;
import android.util.Log;

import com.example.musicapplicationtemplate.R;

public class RegisterActivity extends AppCompatActivity {
    Spinner spinnerGender;
    TextView txtMsgRegister;
    TextView txtLogin;
    EditText edtRegUsername;
    EditText edtRegPassword;
    EditText edtRegRePassword;
    EditText edtEmail;
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
        edtEmail = findViewById(R.id.edtEmail);
        edtRegUsername = findViewById(R.id.edtRegUsername);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        edtRegRePassword = findViewById(R.id.edtRegRePassword);
        txtMsgRegister = findViewById(R.id.txtMsgRegister);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = edtRegUsername.getText().toString();
            String password = edtRegPassword.getText().toString();
            String rePassword = edtRegRePassword.getText().toString();
            String email = edtEmail.getText().toString();
            String firstName = edtFirstName.getText().toString();
            String lastName = edtLastName.getText().toString();
            String genderStr = spinnerGender.getSelectedItem().toString();
            boolean gender;
            UserDAO adb = new UserDAO();
            if (genderStr.equals("Male")) {
                gender = true;
            } else gender = false;

            if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty() || email.isEmpty()) {
                txtMsgRegister.setText("Please fill all fields!");
            } else if (!password.equals(rePassword)) {
                edtEmail.setText(email);
                edtRegUsername.setText(username);
                edtRegPassword.setText("");
                edtRegRePassword.setText("");
                txtMsgRegister.setText("Password and Re-password do not match!");
            } else if (adb.checkEmailExist(email)) {
                txtMsgRegister.setText("Email already exists!");
                edtRegUsername.setText(username);
                edtEmail.setText("");
                edtRegPassword.setText("");
                edtRegRePassword.setText("");
            }else if(adb.checkUsernameExist(username)){
                txtMsgRegister.setText("Username already exists!");
                edtRegUsername.setText("");
                edtEmail.setText(email);
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
                a.setAccount_type(4);
                a.setIs_active(false);
                System.out.println(adb.addUser(a));
                Utils u = new Utils();
                String confirmationCode = u.generateConfirmationCode();
                Log.d("RegisterActivity", "Confirmation Code: " + confirmationCode);

                Intent intentRegSuccess = new Intent(RegisterActivity.this, EnterCodeActivity.class);
                intentRegSuccess.putExtra("username", username);
                intentRegSuccess.putExtra("email",email);
                intentRegSuccess.putExtra("confirmationCode", confirmationCode);
                u.sendEmailInBackground(a.getEmail(),"[MUSTIFY] Active Account",a.getUsername(),confirmationCode,"Mã kích hoạt tài khoản");
                startActivity(intentRegSuccess);
            }


        });

        //Login
        txtLogin.setOnClickListener(v -> {
            finish();
        });


    }
}
