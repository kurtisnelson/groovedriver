package com.thisisnotajoke.android.groovedriver.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;

import javax.inject.Inject;

public class MainActivity extends GrooveActivity {

    @Inject
    AppPreferences mPreferences;
    private View mLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoginButton = findViewById(R.id.activity_main_login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AuthActivity.newIntent(MainActivity.this));
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
            startActivity(new Intent(this, NearbyActivity.class));
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
