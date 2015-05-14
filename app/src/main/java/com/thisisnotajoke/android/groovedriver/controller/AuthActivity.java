package com.thisisnotajoke.android.groovedriver.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;
import com.thisisnotajoke.android.groovedriver.model.DataStore;

import javax.inject.Inject;

public class AuthActivity extends GrooveActivity implements AuthenticationService.TokenCallback {
    @Inject
    DataStore mDataStore;
    @Inject AppPreferences mAppPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if(mDataStore.isLoggedIn()) {
            success();
        } else if (mAppPreferences.hasFbToken()){
            token(mAppPreferences.getFbToken());
        } else {
            AuthenticationService auth = new AuthenticationService(this);
            findViewById(R.id.activity_auth_progress).setVisibility(View.GONE);
            WebView webView = (WebView) findViewById(R.id.activity_auth_webview);
            webView.setVisibility(View.VISIBLE);
            auth.getToken(this, webView);
        }
    }

    public void success() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }

    @Override
    public void token(String token) {
        mDataStore.facebookLogin(token);
        success();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthActivity.class);
    }
}
