package com.example.isp291_folomeevstepan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private SessionManager session;
    private ListView lvRequests, lvUsers;
    private SimpleAdapter reqAdapter, userAdapter;
    private List<HashMap<String, String>> reqData = new ArrayList<>();
    private List<HashMap<String, String>> userData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin);
        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn() || !db.isAdmin(session.getUserId())) {
            Toast.makeText(this, "Недостаточно прав", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        lvRequests = findViewById(R.id.lv_admin_requests);
        lvUsers = findViewById(R.id.lv_admin_users);

        reqAdapter = new SimpleAdapter(this, reqData, android.R.layout.simple_list_item_2,
                new String[]{"title", "subtitle"}, new int[]{android.R.id.text1, android.R.id.text2});
        userAdapter = new SimpleAdapter(this, userData, android.R.layout.simple_list_item_2,
                new String[]{"title", "subtitle"}, new int[]{android.R.id.text1, android.R.id.text2});
        lvRequests.setAdapter(reqAdapter);
        lvUsers.setAdapter(userAdapter);

        loadRequests();
        loadUsers();

        lvRequests.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = reqData.get(position);
            int rid = Integer.parseInt(item.get("rid"));
            // simple actions menu
            db.completeRequest(rid);
            Toast.makeText(this, "Помечено выполнено", Toast.LENGTH_SHORT).show();
            loadRequests();
        });

        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = userData.get(position);
            int uid = Integer.parseInt(item.get("uid"));
            boolean blocked = "1".equals(item.get("blocked"));
            db.blockUser(uid, !blocked);
            Toast.makeText(this, (blocked ? "Разблокирован" : "Заблокирован"), Toast.LENGTH_SHORT).show();
            loadUsers();
        });
    }

    private void loadRequests() {
        reqData.clear();
        List<Request> list = db.getAllRequests();
        for (Request r : list) {
            HashMap<String, String> m = new HashMap<>();
            m.put("rid", String.valueOf(r.id));
            m.put("title", "ID " + r.id + " (" + r.category + ") " + r.model);
            m.put("subtitle", r.selectedDate + " — " + r.status + " — " + db.getUsernameById(r.userId));
            reqData.add(m);
        }
        reqAdapter.notifyDataSetChanged();
    }

    private void loadUsers() {
        userData.clear();
        List<User> users = db.getAllUsers();
        for (User u : users) {
            HashMap<String, String> m = new HashMap<>();
            m.put("uid", String.valueOf(u.id));
            m.put("title", u.username + (u.isAdmin ? " (admin)" : ""));
            m.put("subtitle", "blocked=" + (u.isBlocked ? "1" : "0"));
            m.put("blocked", u.isBlocked ? "1" : "0");
            userData.add(m);
        }
        userAdapter.notifyDataSetChanged();
    }
}
