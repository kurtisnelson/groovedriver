package com.thisisnotajoke.android.groovedriver.controller;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.thisisnotajoke.android.groovedriver.CloseComparator;
import com.thisisnotajoke.android.groovedriver.R;
import com.thisisnotajoke.android.groovedriver.model.RideTypesResponse;
import com.thisisnotajoke.android.groovedriver.model.AppPreferences;
import com.thisisnotajoke.android.groovedriver.model.LocationBody;
import com.thisisnotajoke.android.groovedriver.model.LyftClient;

import java.util.PriorityQueue;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NearbyActivity extends GrooveActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private static final String TAG = "NearbyActivity";
    private static final String KEY_CLOSEST = "ClosestDriver";
    private TextView mSinceText;


    @Inject
    AppPreferences mPreferences;
    @Inject
    LyftClient mLyftClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private int CIRCLE_COLOR;
    private double mClosestDriver;
    private boolean mMoveCamera = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mClosestDriver = savedInstanceState.getDouble(KEY_CLOSEST, 0);
        }
        CIRCLE_COLOR = getResources().getColor(R.color.my_circle);

        setContentView(R.layout.activity_nearby);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        mSinceText = (TextView) findViewById(R.id.activity_nearby_closest);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_nearby_map);
        if(mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_nearby_map, mMapFragment).commit();
        }
        mMapFragment.getMapAsync(this);
    }

    private void updateUI() {
        mSinceText.setText(getString(R.string.closest_driver, (int) mClosestDriver));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(KEY_CLOSEST, mClosestDriver);
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        onLocationChanged(lastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        if(location == null) {
            Log.w(TAG, "Null location, probably on an emulator");
            return;
        }
        final LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if(mPreferences.hasFbToken()) {
            LocationBody locationBody = new LocationBody(location.getLatitude(), location.getLongitude());
            mLyftClient.getNearbyDrivers(locationBody, new Callback<RideTypesResponse>() {
                @Override
                public void success(RideTypesResponse rideTypesResponse, Response response) {
                    if(mMap != null) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(myLocation).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.cars)));
                    }
                    for (RideTypesResponse.RideType rideType : rideTypesResponse.rideTypes) {
                        if (rideType.id.equals("standard")) {
                            PriorityQueue<LatLng> drivers = new PriorityQueue<>(rideType.drivers.size(), new CloseComparator(myLocation));
                            LatLng driverLocation;
                            double farthest = Double.MIN_VALUE;
                            for (RideTypesResponse.Driver driver : rideType.drivers) {
                                driverLocation = new LatLng(driver.location.lat, driver.location.lng);
                                double dist = SphericalUtil.computeDistanceBetween(myLocation, driverLocation);
                                if(dist > farthest)
                                    farthest = dist;
                                drivers.add(driverLocation);
                                mMap.addMarker(new MarkerOptions().position(driverLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                CircleOptions circle = new CircleOptions();
                                circle.strokeWidth(0);
                                circle.fillColor(CIRCLE_COLOR);
                                circle.center(driverLocation);
                                circle.radius(dist / 2);
                                mMap.addCircle(circle);
                            }
                            mClosestDriver = SphericalUtil.computeDistanceBetween(myLocation, drivers.peek());
                            if(mMoveCamera) {
                                LatLng southeast = SphericalUtil.computeOffset(myLocation, (farthest + mClosestDriver) / 2, 225);
                                LatLng northwest = SphericalUtil.computeOffset(myLocation, (farthest + mClosestDriver) / 2, 45);
                                CameraUpdate animate = CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southeast, northwest), 10);
                                mMap.moveCamera(animate);
                                mMoveCamera = false;
                            }
                            updateUI();
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

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(false);
        map.setTrafficEnabled(true);
        map.setOnMarkerClickListener(null);
        map.clear();
        mMap = map;
    }
}
