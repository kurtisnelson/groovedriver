package com.thisisnotajoke.android.groovedriver.controller;

import android.app.Application;

import com.firebase.client.Firebase;
import com.rollbar.android.Rollbar;
import com.thisisnotajoke.android.groovedriver.BuildConfig;
import com.thisisnotajoke.android.groovedriver.GrooveModule;
import com.thisisnotajoke.android.groovedriver.R;

import dagger.ObjectGraph;

public class GrooveApplication extends Application {
    private static final String TAG = "GrooveApplication";

    @Override
    public void onCreate() {
        if(!BuildConfig.DEBUG) {
            Rollbar.init(this, getString(R.string.rollbar_key), "production");
        }
        setupDagger();
        Firebase.setAndroidContext(this);
    }

    protected GrooveModule getModule() {
        return new GrooveModule(this);
    }

    private static GrooveApplication instance;

    protected ObjectGraph mObjectGraph;


    public final void inject(Object object) {
        mObjectGraph.inject(object);
    }

    public <T> T get(Class<T> klass) {
        return mObjectGraph.get(klass);
    }

    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(getModule());
    }

    protected void setupDagger() {
        mObjectGraph = createObjectGraph();
        if (usesInjection()) {
            inject(this);
        }
    }

    protected boolean usesInjection() {
        return true;
    }


}