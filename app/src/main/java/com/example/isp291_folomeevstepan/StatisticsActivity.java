package com.example.isp291_folomeevstepan;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private RecyclerView rvInProgress, rvCompleted;
    private RequestAdapter adapterInProgress, adapterCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Статистика");

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this); // <- сделали полем и инициализировали

        rvInProgress = findViewById(R.id.rv_in_progress);
        rvInProgress.setLayoutManager(new LinearLayoutManager(this));
        rvInProgress.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        rvCompleted = findViewById(R.id.rv_completed);
        rvCompleted.setLayoutManager(new LinearLayoutManager(this));
        rvCompleted.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        updateLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLists(); // обновляем список при возврате на экран
    }

    private void updateLists() {
        // если не вошли в аккаунт — показываем подсказку и пустые списки
        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Войдите в аккаунт, чтобы увидеть свои заявки", Toast.LENGTH_SHORT).show();
            setAdapters(new ArrayList<>(), new ArrayList<>());
            return;
        }

        List<Request> allRequests = dbHelper.getRequestsByUser(session.getUserId());
        List<Request> inProgress = new ArrayList<>();
        List<Request> completed = new ArrayList<>();
        if (dbHelper.isAdmin(session.getUserId())) allRequests = dbHelper.getAllRequests();
        for (Request req : allRequests) {
            if ("В работе".equals(req.status)) {
                inProgress.add(req);
            } else {
                completed.add(req);
            }
        }

        setAdapters(inProgress, completed);

    }

    private void setAdapters(List<Request> inProgress, List<Request> completed) {
        adapterInProgress = new RequestAdapter(inProgress, request -> {
            dbHelper.completeRequest(request.id);
            Toast.makeText(this, "Заявка выполнена", Toast.LENGTH_SHORT).show();
            updateLists();
        });
        rvInProgress.setAdapter(adapterInProgress);

        adapterCompleted = new RequestAdapter(completed, null);
        rvCompleted.setAdapter(adapterCompleted);
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

