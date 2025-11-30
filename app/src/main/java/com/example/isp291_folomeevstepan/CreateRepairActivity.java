package com.example.isp291_folomeevstepan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class CreateRepairActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager session;
    private TextInputEditText etDescription, etPhone, etModel;
    private TextView tvSelectedDate;
    private String selectedDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repair);

        // Настройка Toolbar (кнопка назад)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Заявка на ремонт");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etDescription = findViewById(R.id.et_description);
        etPhone = findViewById(R.id.et_phone);
        etModel = findViewById(R.id.et_model);
        tvSelectedDate = findViewById(R.id.tv_selected_date);

        // Кнопка выбора даты
        MaterialButton btnPickDate = findViewById(R.id.btn_pick_date);
        btnPickDate.setOnClickListener(v -> showDateTimePicker());

        // Кнопка отправки
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";

            if (description.isEmpty() || phone.isEmpty() || model.isEmpty() || selectedDateTime.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidPhone(phone)) {
                Toast.makeText(this, "Неверный телефон", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!dbHelper.isSlotAvailable(selectedDateTime)) {
                Toast.makeText(this, "Это время уже занято", Toast.LENGTH_SHORT).show();
                return;
            }

            // Сохраняем с категорией "Ремонт"
            dbHelper.insertRequest(session.getUserId(), phone, model, selectedDateTime, description, "Ремонт");
            Toast.makeText(this, "Заявка создана!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    Toast.makeText(this, "Нельзя выбрать прошлое", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                        hour, minute);
                tvSelectedDate.setText(selectedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();
    }
}