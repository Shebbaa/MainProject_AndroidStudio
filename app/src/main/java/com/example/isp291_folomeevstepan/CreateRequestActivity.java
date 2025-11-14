package com.example.isp291_folomeevstepan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale; // <- добавлен импорт

public class CreateRequestActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private TextInputEditText etDescription, etPhone, etModel;
    private TextView tvSelectedDate, tvCategory;
    private String selectedDateTime = "";
    private String category = "Ремонт";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Создание заявки");

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etDescription = findViewById(R.id.et_description);
        etPhone = findViewById(R.id.et_phone);
        etModel = findViewById(R.id.et_model);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvCategory = findViewById(R.id.tv_category);

        if (getIntent() != null && getIntent().hasExtra("category")) {
            category = getIntent().getStringExtra("category");
            tvCategory.setText("Категория: " + category);
        } else {
            tvCategory.setText("Категория: " + category);
        }

        MaterialButton btnPickDate = findViewById(R.id.btn_pick_date);
        btnPickDate.setOnClickListener(v -> showDateTimePicker());

        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            if (!session.isLoggedIn()) {
                Toast.makeText(this, "Войдите в аккаунт, чтобы создать заявку", Toast.LENGTH_SHORT).show();
                return;
            }
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
            // check slot
            if (!dbHelper.isSlotAvailable(selectedDateTime)) {
                Toast.makeText(this, "Выбранное время занято, выберите другое", Toast.LENGTH_SHORT).show();
                return;
            }
            int userId = session.getUserId();
            dbHelper.insertRequest(userId, phone, model, selectedDateTime, description, category);
            Toast.makeText(this, "Заявка создана", Toast.LENGTH_SHORT).show();
            finish();
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);

            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                // check not in past
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    Toast.makeText(this, "Нельзя выбрать прошедшую дату/время", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
