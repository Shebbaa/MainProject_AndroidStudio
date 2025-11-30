package com.example.isp291_folomeevstepan;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class CreateCleaningActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager session;
    private TextInputEditText etDescription, etPhone, etModel;
    private TextView tvSelectedDate;
    private Spinner spCleaningType; // Наш спинер
    private String selectedDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cleaning);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Заявка на чистку");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etDescription = findViewById(R.id.et_description);
        etPhone = findViewById(R.id.et_phone);
        etModel = findViewById(R.id.et_model);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        spCleaningType = findViewById(R.id.sp_cleaning_type);

        // Наполняем спинер вариантами
        String[] types = {"Комплексная чистка", "Чистка от пыли", "Химчистка корпуса", "Замена термопасты"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spCleaningType.setAdapter(adapter);

        MaterialButton btnPickDate = findViewById(R.id.btn_pick_date);
        btnPickDate.setOnClickListener(v -> showDateTimePicker());

        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";
            String type = spCleaningType.getSelectedItem().toString(); // Берем выбранный тип

            if (phone.isEmpty() || model.isEmpty() || selectedDateTime.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля и дату", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidPhone(phone)) {
                Toast.makeText(this, "Неверный телефон", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!dbHelper.isSlotAvailable(selectedDateTime)) {
                Toast.makeText(this, "Время занято", Toast.LENGTH_SHORT).show();
                return;
            }

            // Формируем полное описание: Тип чистки + комментарий пользователя
            String fullDescription = "Тип: " + type + ". " + desc;

            dbHelper.insertRequest(session.getUserId(), phone, model, selectedDateTime, fullDescription, "Чистка");
            Toast.makeText(this, "Заявка на чистку принята!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDateTimePicker() {
        // ... (Код такой же, как в RepairActivity, можно скопировать оттуда полностью) ...
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) return;
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