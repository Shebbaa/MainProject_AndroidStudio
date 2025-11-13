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

public class CreateRequestActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextInputEditText etDescription, etPhone, etModel;
    private TextView tvSelectedDate;
    private String selectedDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Создание заявки");

        dbHelper = new DatabaseHelper(this);
        etDescription = findViewById(R.id.et_description);
        etPhone = findViewById(R.id.et_phone);
        etModel = findViewById(R.id.et_model);
        tvSelectedDate = findViewById(R.id.tv_selected_date);

        MaterialButton btnPickDate = findViewById(R.id.btn_pick_date);
        btnPickDate.setOnClickListener(v -> showDateTimePicker());

        MaterialButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            String description = etDescription.getText().toString();
            String phone = etPhone.getText().toString();
            String model = etModel.getText().toString();
            if (!description.isEmpty() && !phone.isEmpty() && !model.isEmpty() && !selectedDateTime.isEmpty()) {
                dbHelper.insertRequest(phone, model, selectedDateTime, description);
                Toast.makeText(this, "Заявка создана", Toast.LENGTH_SHORT).show();
                finish();
                overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                selectedDateTime = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + hour + ":" + minute;
                tvSelectedDate.setText(selectedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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