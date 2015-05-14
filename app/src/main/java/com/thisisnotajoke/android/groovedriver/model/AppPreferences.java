package com.thisisnotajoke.android.groovedriver.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AppPreferences {
    private static final String PREF_NAME = "groovedriver";
    private static final String KEY_FB_TOKEN = "fbtoken";
    private final SharedPreferences mPreferences;

    public AppPreferences(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public String getFbToken() {
        return mPreferences.getString(KEY_FB_TOKEN, "");
    }

    public void setFbToken(String token) {
        mPreferences.edit().putString(KEY_FB_TOKEN, token).apply();
    }

    public boolean hasFbToken() {
        return !TextUtils.isEmpty(getFbToken());
    }
}
