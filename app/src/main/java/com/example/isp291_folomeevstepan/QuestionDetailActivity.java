package com.example.isp291_folomeevstepan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuestionDetailActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private int qid;
    private TextView tvQuestion;
    private EditText etAnswer;
    private Button btnAnswer;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_question_detail);
        db = new DatabaseHelper(this);
        qid = getIntent().getIntExtra("qid", -1);

        tvQuestion = findViewById(R.id.tv_question_text);
        etAnswer = findViewById(R.id.et_answer);
        btnAnswer = findViewById(R.id.btn_answer);

        for (Question q : db.getAllQuestions()) {
            if (q.id == qid) {
                tvQuestion.setText("Вопрос: " + q.question);
                if (q.answer != null) etAnswer.setText(q.answer);
                break;
            }
        }

        btnAnswer.setOnClickListener(v -> {
            String ans = etAnswer.getText() != null ? etAnswer.getText().toString().trim() : "";
            if (ans.isEmpty()) { Toast.makeText(this, "Введите ответ", Toast.LENGTH_SHORT).show(); return; }
            db.answerQuestion(qid, ans);
            Toast.makeText(this, "Ответ сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
