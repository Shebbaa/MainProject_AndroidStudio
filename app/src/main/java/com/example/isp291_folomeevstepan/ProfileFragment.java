package com.example.isp291_folomeevstepan;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private SessionManager session;
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile, container, false); // Используем старый layout
        session = new SessionManager(getContext());
        db = new DatabaseHelper(getContext());

        TextView tvName = v.findViewById(R.id.tv_name);
        TextView tvPhone = v.findViewById(R.id.tv_phone);
        Button btnLogout = v.findViewById(R.id.btn_logout);
        Button btnMyRequests = v.findViewById(R.id.btn_my_requests);

        // Кнопка "Мои заявки" здесь не нужна, так как есть вкладка снизу, скроем её
        btnMyRequests.setVisibility(View.GONE);

        int userId = session.getUserId();
        tvName.setText("Ник: " + session.getUsername());
        String phone = db.getAllUsers().stream().filter(u -> u.id == userId).findFirst().map(u -> u.phone).orElse("");
        tvPhone.setText("Телефон: " + phone);

        btnLogout.setOnClickListener(v1 -> {
            session.clearSession();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        return v;
    }
}