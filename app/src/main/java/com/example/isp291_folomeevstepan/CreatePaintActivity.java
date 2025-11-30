package com.example.isp291_folomeevstepan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class CreatePaintActivity extends AppCompatActivity {
    // ... копируем базовую логику инициализации полей ...
    private DatabaseHelper db;
    private SessionManager session;
    private String selectedDateTime = "";
    private Spinner spColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_paint); // Твой новый layout

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        spColor = findViewById(R.id.sp_color);
        String[] colors = {"Красный", "Синий", "Черный", "Белый", "Металлик", "Матовый"};
        spColor.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colors));

        // ... Инициализация остальных полей (model, phone, description) ...
        // ... Логика выбора даты (как в CreateRequestActivity) ...

        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            // ... Валидация полей ...

            String color = spColor.getSelectedItem().toString();
            String desc = ((TextInputEditText)findViewById(R.id.et_description)).getText().toString();
            String fullDesc = "Покраска. Цвет: " + color + ". " + desc; // Формируем описание

            // Сохраняем в БД
            db.insertRequest(session.getUserId(),
                    ((TextInputEditText)findViewById(R.id.et_phone)).getText().toString(),
                    ((TextInputEditText)findViewById(R.id.et_model)).getText().toString(),
                    selectedDateTime,
                    fullDesc, // Передаем с цветом
                    "Покраска");

            Toast.makeText(this, "Заявка на покраску создана", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
    // ... метод showDateTimePicker ...
}