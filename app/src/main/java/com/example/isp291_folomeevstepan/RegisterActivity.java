package com.example.isp291_folomeevstepan;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextInputEditText etUsername, etPhone, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_register);
        db = new DatabaseHelper(this);

        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String name = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String pass = etPassword.getText() != null ? etPassword.getText().toString() : "";

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidPhone(phone)) {
                Toast.makeText(this, "Неверный телефон", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidPassword(pass)) {
                Toast.makeText(this, "Пароль слишком простой", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = db.registerUser(name, phone, pass);
            if (id == -1) {
                Toast.makeText(this, "Пользователь с таким именем/телефоном уже существует", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
