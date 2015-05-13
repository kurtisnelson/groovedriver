package com.thisisnotajoke.android.groovedriver.controller;

import android.app.Application;

import com.firebase.client.Firebase;
import com.thisisnotajoke.android.groovedriver.GuberModule;

import dagger.ObjectGraph;

public class GrooveApplication extends Application {

    private static final String TAG = "GrooveApplication";
    @Override
    public void onCreate() {
        setupDagger();
        Firebase.setAndroidContext(this);
    }

    protected GuberModule getModule() {
        return new GuberModule(this);
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