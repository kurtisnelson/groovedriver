package com.thisisnotajoke.android.groovedriver.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;

import javax.inject.Inject;

public class SplashActivity extends GrooveActivity {

    @Inject
    AppPreferences mPreferences;
    private View mLoginButton;

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
            startService(GatherService.newIntent(this));
            startActivity(NearbyActivity.newIntent(this));
            finish();
        } else {
            mLoginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }
}
