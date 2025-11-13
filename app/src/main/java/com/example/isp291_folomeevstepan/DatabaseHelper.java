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
    private static final String DATABASE_NAME = "requests.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_REQUESTS = "requests";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_SELECTED_DATE = "selected_date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CREATION_DATE = "creation_date";
    private static final String COLUMN_COMPLETION_DATE = "completion_date";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_REQUESTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_MODEL + " TEXT, " +
                COLUMN_SELECTED_DATE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CREATION_DATE + " TEXT, " +
                COLUMN_COMPLETION_DATE + " TEXT, " +
                COLUMN_STATUS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        onCreate(db);
    }

    public long insertRequest(String phone, String model, String selectedDate, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_MODEL, model);
        values.put(COLUMN_SELECTED_DATE, selectedDate);
        values.put(COLUMN_DESCRIPTION, description);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        values.put(COLUMN_CREATION_DATE, currentDate);
        values.put(COLUMN_COMPLETION_DATE, (String) null);
        values.put(COLUMN_STATUS, "В работе");
        return db.insert(TABLE_REQUESTS, null, values);
    }

    public List<Request> getAllRequests() {
        List<Request> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REQUESTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Request request = new Request();
                request.id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                request.phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                request.model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL));
                request.selectedDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELECTED_DATE));
                request.description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                request.creationDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE));
                request.completionDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_DATE));
                request.status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                requests.add(request);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requests;
    }

    public void completeRequest(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        values.put(COLUMN_COMPLETION_DATE, currentDate);
        values.put(COLUMN_STATUS, "Выполнено");
        db.update(TABLE_REQUESTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int getInProgressCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REQUESTS, new String[]{"COUNT(*)"}, COLUMN_STATUS + " = ?", new String[]{"В работе"}, null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getCompletedCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REQUESTS, new String[]{"COUNT(*)"}, COLUMN_STATUS + " = ?", new String[]{"Выполнено"}, null, null, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}