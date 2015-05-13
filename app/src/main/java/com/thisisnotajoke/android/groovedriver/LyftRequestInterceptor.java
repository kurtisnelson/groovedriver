package com.thisisnotajoke.android.groovedriver;

import android.text.TextUtils;

import com.thisisnotajoke.android.groovedriver.model.AppPreferences;

import retrofit.RequestInterceptor;

public class LyftRequestInterceptor implements RequestInterceptor {
    private final AppPreferences mPreferences;

    public LyftRequestInterceptor(AppPreferences preferences) {
        mPreferences = preferences;
    }
    public void intercept(RequestFacade request) {
        if(!TextUtils.isEmpty(mPreferences.getFbToken())) {
            request.addHeader("Authorization", "fbAccessToken " + mPreferences.getFbToken());
        }
        request.addHeader("User-Agent", "android-retrofit");
        request.addHeader("Accept", "application/vnd.lyft.app+json;version=28");
    }
}
