package com.thisisnotajoke.android.groovedriver.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;
import com.thisisnotajoke.android.groovedriver.model.DataStore;

import javax.inject.Inject;

public class SplashActivity extends GrooveActivity implements Firebase.AuthStateListener {

    private View mLoginButton;

    @Inject AppPreferences mPreferences;
    @Inject DataStore mFirebase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mLoginButton = findViewById(R.id.activity_main_login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AuthActivity.newIntent(SplashActivity.this));
            }
        });

        mFirebase.getClient().addAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI() {
        if(mPreferences.hasFbToken()) {
            mLoginButton.setVisibility(View.GONE);
            if(mFirebase.getClient().getAuth() == null) {
                mFirebase.facebookLogin(mPreferences.getFbToken());
            } else {
                startService(GatherService.newIntent(this));
                Intent nearIntent = NearbyActivity.newIntent(this);
                nearIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nearIntent);
                finish();
            }
        } else {
            mLoginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }

    @Override
    public void onAuthStateChanged(AuthData authData) {
        updateUI();
    }
}
