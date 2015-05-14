package com.thisisnotajoke.android.groovedriver;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thisisnotajoke.android.groovedriver.controller.AuthActivity;
import com.thisisnotajoke.android.groovedriver.controller.GatherService;
import com.thisisnotajoke.android.groovedriver.controller.GrooveApplication;
import com.thisisnotajoke.android.groovedriver.controller.SplashActivity;
import com.thisisnotajoke.android.groovedriver.controller.NearbyActivity;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;
import com.thisisnotajoke.android.groovedriver.model.DataStore;
import com.thisisnotajoke.android.groovedriver.model.LyftClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Module(
        injects = {
                GrooveApplication.class,
                NearbyActivity.class,
                AuthActivity.class,
                SplashActivity.class,
                //services
                GatherService.class
        },
        complete = true,
        library = false
)

public final class GrooveModule {
    protected GrooveApplication mApplication;
    protected Context mContext;

    public GrooveModule() {
    }

    public GrooveModule(GrooveApplication application) {
        mApplication = application;
        mContext = application.getApplicationContext();
    }

    //@Provides
    GrooveApplication provideApplication() {
        return mApplication;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    @Singleton
    LyftClient provideLyftClient(final Gson gson, NetworkConnectivityManager ncm, LyftRequestInterceptor interceptor, LyftErrorHandler errorHandler) {
        Endpoint endpoint = Endpoints.newFixedEndpoint(LyftClient.LYFT_URL);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("Retrofit"))
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(interceptor)
                .setErrorHandler(errorHandler)
                .setClient(new ConnectivityAwareUrlClient(ncm))
                .setEndpoint(endpoint)
                .build();
        return restAdapter.create(LyftClient.class);
    }

    @Provides
    @Singleton
    DataStore provideFirebaseClient(AppPreferences preferences) {
        return new DataStore(preferences);
    }

    @Provides
    LyftRequestInterceptor provideRequestInterceptor(AppPreferences preferences) {
        return new LyftRequestInterceptor(preferences);
    }

    @Provides
    LyftErrorHandler provideErrorHandler() {
        return new LyftErrorHandler();
    }

    @Provides
    AppPreferences provideAppPreferences(Context context) {
        return new AppPreferences(context);
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateUtils.DateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new DateUtils.LocalDateTypeAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .create();
    }

    @Provides
    NetworkConnectivityManager provideNetworkConnectivityManager(Context context) {
        return new NetworkConnectivityManager(context);
    }
}