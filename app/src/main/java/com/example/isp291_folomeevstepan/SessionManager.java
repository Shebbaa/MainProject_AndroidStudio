package com.example.isp291_folomeevstepan;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "app_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences prefs;

    public SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveSession(int userId, String username) {
        prefs.edit().putInt(KEY_USER_ID, userId).putString(KEY_USERNAME, username).apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }
}
