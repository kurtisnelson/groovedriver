package com.thisisnotajoke.android.groovedriver;

import android.content.Context;

import com.thisisnotajoke.android.groovedriver.controller.GrooveApplication;

public class InjectionUtils {
    public static void injectClass(Context context) {
        injectClass(context, context);
    }

    public static void injectClass(Context context, Object obj) {
        ((GrooveApplication) context.getApplicationContext()).inject(obj);
    }

    public static <T> T get(Context context, Class<T> objectClass) {
        return ((GrooveApplication) context.getApplicationContext()).get(objectClass);
    }

}