package com.example.isp291_folomeevstepan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private SessionManager session;
    private DatabaseHelper db;
    private TextView tvName, tvPhone;
    private Button btnLogout, btnMyRequests;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_profile);
        session = new SessionManager(this);
        db = new DatabaseHelper(this);

        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        btnLogout = findViewById(R.id.btn_logout);
        btnMyRequests = findViewById(R.id.btn_my_requests);

        if (!session.isLoggedIn()) {
            finish();
            return;
        }

        int userId = session.getUserId();
        tvName.setText("Ник: " + session.getUsername());
        // retrieve phone
        String phone = db.getAllUsers().stream().filter(u -> u.id == userId).findFirst().map(u -> u.phone).orElse("");
        tvPhone.setText("Телефон: " + phone);

        btnMyRequests.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            session.clearSession();
            finish();
        });
    }
}
