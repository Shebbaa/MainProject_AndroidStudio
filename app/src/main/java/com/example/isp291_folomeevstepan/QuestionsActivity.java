package com.example.isp291_folomeevstepan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private SessionManager session;
    private EditText etQuestion;
    private Button btnSend;
    private ListView lvQuestions;
    private List<HashMap<String, String>> dataList = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_questions);
        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        etQuestion = findViewById(R.id.et_question);
        btnSend = findViewById(R.id.btn_send_question);
        lvQuestions = findViewById(R.id.lv_questions);

        adapter = new SimpleAdapter(this, dataList, android.R.layout.simple_list_item_2,
                new String[]{"title", "subtitle"}, new int[]{android.R.id.text1, android.R.id.text2});
        lvQuestions.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            if (!session.isLoggedIn()) {
                Toast.makeText(this, "Войдите для отправки вопроса", Toast.LENGTH_SHORT).show();
                return;
            }
            String text = etQuestion.getText() != null ? etQuestion.getText().toString().trim() : "";
            if (text.isEmpty()) { Toast.makeText(this, "Введите вопрос", Toast.LENGTH_SHORT).show(); return; }
            db.insertQuestion(session.getUserId(), text);
            etQuestion.setText("");
            loadQuestions();
            Toast.makeText(this, "Вопрос отправлен", Toast.LENGTH_SHORT).show();
        });

        // if admin -> item click opens detail to answer
        lvQuestions.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = dataList.get(position);
            int qid = Integer.parseInt(item.get("qid"));
            if (db.isAdmin(session.getUserId())) {
                Intent i = new Intent(this, QuestionDetailActivity.class);
                i.putExtra("qid", qid);
                startActivity(i);
            } else {
                Toast.makeText(this, "Просмотр / ответы — только для админа", Toast.LENGTH_SHORT).show();
            }
        });

        loadQuestions();
    }

    private void loadQuestions() {
        dataList.clear();
        List<Question> list = db.getAllQuestions();
        for (Question q : list) {
            HashMap<String, String> m = new HashMap<>();
            m.put("qid", String.valueOf(q.id));
            String name = db.getUsernameById(q.userId);
            m.put("title", name + ": " + q.question);
            m.put("subtitle", q.answer != null ? "Ответ: " + q.answer : "Ожидает ответа");
            dataList.add(m);
        }
        adapter.notifyDataSetChanged();
    }
}
