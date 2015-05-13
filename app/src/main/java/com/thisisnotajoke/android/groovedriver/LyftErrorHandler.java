package com.thisisnotajoke.android.groovedriver;

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LyftErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            Log.w("Retrofit", "Could not authenticate, invalidating token");
        }
        return cause;
    }

}
