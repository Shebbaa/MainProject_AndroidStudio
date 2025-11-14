package com.example.isp291_folomeevstepan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class SelectServiceActivity extends AppCompatActivity {
    private Spinner spCategory;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_select_service);

        spCategory = findViewById(R.id.sp_category);
        btnNext = findViewById(R.id.btn_next);

        String[] cats = new String[]{"Ремонт", "Покраска", "Чистка", "Другое"};
        spCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cats));

        btnNext.setOnClickListener(v -> {
            String cat = (String) spCategory.getSelectedItem();
            Intent i = new Intent(this, CreateRequestActivity.class);
            i.putExtra("category", cat);
            startActivity(i);
        });
    }
}
