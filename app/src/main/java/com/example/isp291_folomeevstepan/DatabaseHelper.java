package com.example.isp291_folomeevstepan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "appdata.db";
    private static final int DATABASE_VERSION = 2;

    // users
    public static final String TABLE_USERS = "users";
    public static final String U_ID = "id";
    public static final String U_NAME = "username";
    public static final String U_PHONE = "phone";
    public static final String U_PASSWORD = "password";
    public static final String U_IS_ADMIN = "is_admin";
    public static final String U_IS_BLOCKED = "is_blocked";
    public static final String U_CREATED_AT = "created_at";

    // requests
    public static final String TABLE_REQUESTS = "requests";
    public static final String R_ID = "id";
    public static final String R_USER_ID = "user_id";
    public static final String R_PHONE = "phone";
    public static final String R_MODEL = "model";
    public static final String R_SELECTED_DATE = "selected_date";
    public static final String R_DESCRIPTION = "description";
    public static final String R_CREATION_DATE = "creation_date";
    public static final String R_COMPLETION_DATE = "completion_date";
    public static final String R_STATUS = "status";
    public static final String R_CATEGORY = "category";

    // questions
    public static final String TABLE_QUESTIONS = "questions";
    public static final String Q_ID = "id";
    public static final String Q_USER_ID = "user_id";
    public static final String Q_TEXT = "question";
    public static final String Q_ANSWER = "answer";
    public static final String Q_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                U_NAME + " TEXT UNIQUE, " +
                U_PHONE + " TEXT, " +
                U_PASSWORD + " TEXT, " +
                U_IS_ADMIN + " INTEGER DEFAULT 0, " +
                U_IS_BLOCKED + " INTEGER DEFAULT 0, " +
                U_CREATED_AT + " TEXT)";
        db.execSQL(createUsers);


        String createRequests = "CREATE TABLE " + TABLE_REQUESTS + " (" +
                R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_USER_ID + " INTEGER, " +
                R_PHONE + " TEXT, " +
                R_MODEL + " TEXT, " +
                R_SELECTED_DATE + " TEXT, " +
                R_DESCRIPTION + " TEXT, " +
                R_CREATION_DATE + " TEXT, " +
                R_COMPLETION_DATE + " TEXT, " +
                R_STATUS + " TEXT, " +
                R_CATEGORY + " TEXT, " +
                "FOREIGN KEY(" + R_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + U_ID + "))";
        db.execSQL(createRequests);


        String createQuestions = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
                Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Q_USER_ID + " INTEGER, " +
                Q_TEXT + " TEXT, " +
                Q_ANSWER + " TEXT, " +
                Q_CREATED_AT + " TEXT, " +
                "FOREIGN KEY(" + Q_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + U_ID + "))";
        db.execSQL(createQuestions);


        ContentValues v = new ContentValues();
        v.put(U_NAME, "admin");
        v.put(U_PHONE, "");
        v.put(U_PASSWORD, "admin123"); // as requested, no hash
        v.put(U_IS_ADMIN, 1);
        v.put(U_CREATED_AT, now());
        db.insert(TABLE_USERS, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // simple migration: drop and recreate (safe for dev)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }


    public long registerUser(String username, String phone, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(U_NAME, username);
        v.put(U_PHONE, phone);
        v.put(U_PASSWORD, password);
        v.put(U_CREATED_AT, now());
        return db.insert(TABLE_USERS, null, v);
    }


    public int loginUser(String usernameOrPhone, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + U_ID + "," + U_IS_BLOCKED + "," + U_IS_ADMIN + " FROM " + TABLE_USERS +
                " WHERE (" + U_NAME + "=? OR " + U_PHONE + "=?) AND " + U_PASSWORD + "=?", new String[]{usernameOrPhone, usernameOrPhone, password});
        if (c.moveToFirst()) {
            int blocked = c.getInt(c.getColumnIndexOrThrow(U_IS_BLOCKED));
            if (blocked == 1) {
                c.close();
                return -2;
            }
            int id = c.getInt(c.getColumnIndexOrThrow(U_ID));
            c.close();
            return id;
        }
        c.close();
        return -1;
    }

    public boolean isAdmin(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{U_IS_ADMIN}, U_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        boolean res = false;
        if (c.moveToFirst()) {
            res = c.getInt(c.getColumnIndexOrThrow(U_IS_ADMIN)) == 1;
        }
        c.close();
        return res;
    }

    public void blockUser(int userId, boolean block) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(U_IS_BLOCKED, block ? 1 : 0);
        db.update(TABLE_USERS, v, U_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, null, null, null, null, U_CREATED_AT + " DESC");
        if (c.moveToFirst()) {
            do {
                User u = new User();
                u.id = c.getInt(c.getColumnIndexOrThrow(U_ID));
                u.username = c.getString(c.getColumnIndexOrThrow(U_NAME));
                u.phone = c.getString(c.getColumnIndexOrThrow(U_PHONE));
                u.isAdmin = c.getInt(c.getColumnIndexOrThrow(U_IS_ADMIN)) == 1;
                u.isBlocked = c.getInt(c.getColumnIndexOrThrow(U_IS_BLOCKED)) == 1;
                users.add(u);
            } while (c.moveToNext());
        }
        c.close();
        return users;
    }


    public boolean isSlotAvailable(String selectedDateTime) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_REQUESTS, new String[]{R_ID}, R_SELECTED_DATE + "=?", new String[]{selectedDateTime}, null, null, null);
        boolean available = !c.moveToFirst();
        c.close();
        return available;
    }

    public long insertRequest(int userId, String phone, String model, String selectedDate, String description, String category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(R_USER_ID, userId);
        v.put(R_PHONE, phone);
        v.put(R_MODEL, model);
        v.put(R_SELECTED_DATE, selectedDate);
        v.put(R_DESCRIPTION, description);
        v.put(R_CREATION_DATE, now());
        v.put(R_COMPLETION_DATE, (String) null);
        v.put(R_STATUS, "В работе");
        v.put(R_CATEGORY, category);
        return db.insert(TABLE_REQUESTS, null, v);
    }

    public List<Request> getRequestsByUser(int userId) {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_REQUESTS, null, R_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, R_CREATION_DATE + " DESC");
        if (c.moveToFirst()) {
            do {
                Request r = mapRequest(c);
                list.add(r);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Request> getAllRequests() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_REQUESTS, null, null, null, null, null, R_CREATION_DATE + " DESC");
        if (c.moveToFirst()) {
            do {
                list.add(mapRequest(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private Request mapRequest(Cursor c) {
        Request r = new Request();
        r.id = c.getInt(c.getColumnIndexOrThrow(R_ID));
        r.userId = c.getInt(c.getColumnIndexOrThrow(R_USER_ID));
        r.phone = c.getString(c.getColumnIndexOrThrow(R_PHONE));
        r.model = c.getString(c.getColumnIndexOrThrow(R_MODEL));
        r.selectedDate = c.getString(c.getColumnIndexOrThrow(R_SELECTED_DATE));
        r.description = c.getString(c.getColumnIndexOrThrow(R_DESCRIPTION));
        r.creationDate = c.getString(c.getColumnIndexOrThrow(R_CREATION_DATE));
        r.completionDate = c.getString(c.getColumnIndexOrThrow(R_COMPLETION_DATE));
        r.status = c.getString(c.getColumnIndexOrThrow(R_STATUS));
        r.category = c.getString(c.getColumnIndexOrThrow(R_CATEGORY));
        return r;
    }

    public void completeRequest(int id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(R_COMPLETION_DATE, now());
        v.put(R_STATUS, "Выполнено");
        db.update(TABLE_REQUESTS, v, R_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteRequest(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_REQUESTS, R_ID + "=?", new String[]{String.valueOf(id)});
    }

    public int getInProgressCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_REQUESTS, new String[]{"COUNT(*)"}, R_STATUS + "=?", new String[]{"В работе"}, null, null, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getCompletedCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_REQUESTS, new String[]{"COUNT(*)"}, R_STATUS + "=?", new String[]{"Выполнено"}, null, null, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }


    public long insertQuestion(int userId, String questionText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Q_USER_ID, userId);
        v.put(Q_TEXT, questionText);
        v.put(Q_CREATED_AT, now());
        return db.insert(TABLE_QUESTIONS, null, v);
    }

    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_QUESTIONS, null, null, null, null, null, Q_CREATED_AT + " DESC");
        if (c.moveToFirst()) {
            do {
                Question q = new Question();
                q.id = c.getInt(c.getColumnIndexOrThrow(Q_ID));
                q.userId = c.getInt(c.getColumnIndexOrThrow(Q_USER_ID));
                q.question = c.getString(c.getColumnIndexOrThrow(Q_TEXT));
                q.answer = c.getString(c.getColumnIndexOrThrow(Q_ANSWER));
                q.createdAt = c.getString(c.getColumnIndexOrThrow(Q_CREATED_AT));
                list.add(q);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void answerQuestion(int questionId, String answer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(Q_ANSWER, answer);
        db.update(TABLE_QUESTIONS, v, Q_ID + "=?", new String[]{String.valueOf(questionId)});
    }

    // helper: get username by id
    public String getUsernameById(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{U_NAME}, U_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        String name = null;
        if (c.moveToFirst()) name = c.getString(c.getColumnIndexOrThrow(U_NAME));
        c.close();
        return name != null ? name : "User#" + userId;
    }
}
