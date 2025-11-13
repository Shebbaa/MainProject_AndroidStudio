package com.example.isp291_folomeevstepan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Стартовая страница");
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);

        TextView tvDate = findViewById(R.id.tv_date);
        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
        if (tvDate != null) tvDate.setText(currentDate);

        updateSummary();

        FloatingActionButton fabCreate = findViewById(R.id.fab_create);
        if (fabCreate != null) {
            fabCreate.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CreateRequestActivity.class);
                startActivity(intent);
                overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        MaterialButton btnStats = findViewById(R.id.btn_statistics);
        if (btnStats != null) {
            btnStats.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
                overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
    }

    private void updateSummary() {
        TextView tvSummary = findViewById(R.id.tv_summary);
        if (tvSummary == null || dbHelper == null) return;
        int inProgress = dbHelper.getInProgressCount();
        int completed = dbHelper.getCompletedCount();
        tvSummary.setText(String.format(Locale.getDefault(),
                "Заявок в работе: %d\nВыполнено: %d", inProgress, completed));
    }
}