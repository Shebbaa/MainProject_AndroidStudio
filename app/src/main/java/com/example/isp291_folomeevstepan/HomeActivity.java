package com.example.isp291_folomeevstepan;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bnv = findViewById(R.id.bottom_nav);

        // Установка начального фрагмента
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ServicesFragment()).commit();

        bnv.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_services) selectedFragment = new ServicesFragment();
            else if (item.getItemId() == R.id.nav_stats) selectedFragment = new StatisticsFragment();
            else if (item.getItemId() == R.id.nav_profile) selectedFragment = new ProfileFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }
}