package com.example.isp291_folomeevstepan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private SessionManager session;
    private TextInputEditText etUser, etPass;
    private Button btnLogin, btnToRegister;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);
        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        etUser = findViewById(R.id.et_user);
        etPass = findViewById(R.id.et_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnToRegister = findViewById(R.id.btn_to_register);

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText() != null ? etUser.getText().toString().trim() : "";
            String pass = etPass.getText() != null ? etPass.getText().toString() : "";
            int res = db.loginUser(user, pass);
            if (res == -1) {
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show();
            } else if (res == -2) {
                Toast.makeText(this, "Ваша учётная запись заблокирована", Toast.LENGTH_SHORT).show();
            } else {
                // success, save session
                session.saveSession(res, db.getUsernameById(res));
                Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
