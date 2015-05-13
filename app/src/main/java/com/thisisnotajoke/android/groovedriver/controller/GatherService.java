package com.thisisnotajoke.android.groovedriver.controller;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.thisisnotajoke.android.groovedriver.CloseComparator;
import com.thisisnotajoke.android.groovedriver.InjectionUtils;
import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;
import com.thisisnotajoke.android.groovedriver.model.FirebaseClient;
import com.thisisnotajoke.android.groovedriver.model.LyftClient;
import com.thisisnotajoke.android.groovedriver.model.lyft.LocationBody;
import com.thisisnotajoke.android.groovedriver.model.lyft.RideTypesResponse;

import java.util.PriorityQueue;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GatherService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final String TAG = "GatherService";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Inject
    AppPreferences mPreferences;
    @Inject
    LyftClient mLyftClient;
    @Inject
    FirebaseClient mFirebaseClient;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        GatherService getService() {
            return GatherService.this;
        }
    }

    @Override
    public void onCreate() {
        InjectionUtils.injectClass(this);
        super.onCreate();
        goForeground();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals("STOP")) {
            stopForeground(true);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        onLocationChanged(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (location == null) {
            Log.w(TAG, "Null location, probably on an emulator");
            mFirebaseClient.setLocation(null);
            return;
        }
        mFirebaseClient.setLocation(new com.thisisnotajoke.android.groovedriver.model.cloud.Location(location));
        hitLyft(location);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, GatherService.class);
    }

    private void goForeground() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker(getText(R.string.ticker_text));
        builder.setContentTitle(getText(R.string.notification_title));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, NearbyActivity.newIntent(this), 0);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Exit", stopIntent(this));
        startForeground(ONGOING_NOTIFICATION_ID, builder.build());
    }

    private void hitLyft(Location location) {
        if (mPreferences.hasFbToken()) {
            final LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            LocationBody locationBody = new LocationBody(location.getLatitude(), location.getLongitude());
            mLyftClient.getNearbyDrivers(locationBody, new Callback<RideTypesResponse>() {
                @Override
                public void success(RideTypesResponse rideTypesResponse, Response response) {
                    mFirebaseClient.saveDrivers(rideTypesResponse.rideTypes);

                    for (RideTypesResponse.RideType rideType : rideTypesResponse.rideTypes) {

                        if (rideType.id.equals("standard")) {
                            PriorityQueue<LatLng> drivers = new PriorityQueue<>(rideType.drivers.size(), new CloseComparator(myLocation));
                            LatLng driverLocation;
                            double farthest = Double.MIN_VALUE;
                            for (RideTypesResponse.Driver driver : rideType.drivers) {
                                driverLocation = new LatLng(driver.location.lat, driver.location.lng);
                                double dist = SphericalUtil.computeDistanceBetween(myLocation, driverLocation);
                                if (dist > farthest)
                                    farthest = dist;
                                drivers.add(driverLocation);
                            }
                            double closestDriver = SphericalUtil.computeDistanceBetween(myLocation, drivers.peek());
                            mFirebaseClient.setClosestDriver(closestDriver);
                            mFirebaseClient.setFarthestDriver(farthest);
                            mFirebaseClient.setNearbyDrivers(drivers);
                            return;
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private static PendingIntent stopIntent(Context context) {
        Intent intent = new Intent(context, GatherService.class);
        intent.setAction("STOP");
        return PendingIntent.getService(context, 0, intent, 0);
    }
}
