package com.example.isp291_folomeevstepan;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private RecyclerView rvInProgress, rvCompleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Используем старый layout activity_statistics.xml, только уберем тулбар из XML если он там есть, или оставим.
        // Лучше создать fragment_statistics.xml скопировав содержимое NestedScrollView из activity_statistics.xml
        View v = inflater.inflate(R.layout.activity_statistics, container, false);

        // Убираем Toolbar из layout программно, так как он нам не нужен внутри фрагмента (или скрываем его)
        View toolbar = v.findViewById(R.id.toolbar);
        if(toolbar != null) toolbar.setVisibility(View.GONE);

        dbHelper = new DatabaseHelper(getContext());
        session = new SessionManager(getContext());

        rvInProgress = v.findViewById(R.id.rv_in_progress);
        rvCompleted = v.findViewById(R.id.rv_completed);

        rvInProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompleted.setLayoutManager(new LinearLayoutManager(getContext()));

        updateLists();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLists();
    }

    private void updateLists() {
        if (getContext() == null) return;
        List<Request> allRequests = dbHelper.getRequestsByUser(session.getUserId());
        if (dbHelper.isAdmin(session.getUserId())) allRequests = dbHelper.getAllRequests();

        List<Request> inProgress = new ArrayList<>();
        List<Request> completed = new ArrayList<>();

        for (Request req : allRequests) {
            if ("В работе".equals(req.status)) inProgress.add(req);
            else completed.add(req);
        }

        rvInProgress.setAdapter(new RequestAdapter(inProgress, request -> {
            dbHelper.completeRequest(request.id);
            Toast.makeText(getContext(), "Заявка выполнена", Toast.LENGTH_SHORT).show();
            updateLists();
        }));
        rvCompleted.setAdapter(new RequestAdapter(completed, null));
    }
}