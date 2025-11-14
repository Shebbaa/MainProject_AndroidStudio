package com.example.isp291_folomeevstepan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this); // инициализация сессии
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        TextView tvDate = findViewById(R.id.tv_date);
        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
        if (tvDate != null) tvDate.setText(currentDate);

        // Отображаем ник в тулбаре, если залогинен
        if (session.isLoggedIn()) {
            String name = session.getUsername();
            if (name != null) toolbar.setTitle("Привет, " + name);
        } else {
            toolbar.setTitle("Стартовая страница");
        }

        // Кнопки
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        MaterialButton btnRegister = findViewById(R.id.btn_register);
        MaterialButton btnQuestions = findViewById(R.id.btn_questions);
        MaterialButton btnProfile = findViewById(R.id.btn_profile);
        MaterialButton btnStats = findViewById(R.id.btn_statistics);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        btnQuestions.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuestionsActivity.class)));

        btnProfile.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else {
                // перенаправляем на логин, если не залогинен
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnStats.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
        });

        FloatingActionButton fabCreate = findViewById(R.id.fab_create);
        fabCreate.setOnClickListener(v -> {
            // если пользователь залогинен — открываем выбор услуги/создание заявки,
            // иначе — предлагаем войти
            if (session.isLoggedIn()) {
                Intent intent = new Intent(MainActivity.this, SelectServiceActivity.class);
                startActivity(intent);
                overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(MainActivity.this, "Войдите в аккаунт для создания заявки", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // обновляем титул при возвращении (вдруг залогинились)
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (session.isLoggedIn() && toolbar != null) {
            toolbar.setTitle("Привет, " + session.getUsername());
        } else if (toolbar != null) {
            toolbar.setTitle("Стартовая страница");
        }
    }
}
