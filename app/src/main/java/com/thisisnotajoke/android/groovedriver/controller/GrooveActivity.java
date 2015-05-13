package com.thisisnotajoke.android.groovedriver.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thisisnotajoke.android.groovedriver.InjectionUtils;

public abstract class GrooveActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (usesInjection()) {
            InjectionUtils.injectClass(this);
        }
    }

    protected boolean usesInjection() {
        return false;
    }
}
