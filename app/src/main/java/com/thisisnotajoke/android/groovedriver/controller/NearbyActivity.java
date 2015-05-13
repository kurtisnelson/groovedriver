package com.thisisnotajoke.android.groovedriver.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
import com.thisisnotajoke.android.groovedriver.model.FirebaseClient;
import com.thisisnotajoke.android.groovedriver.model.cloud.Location;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class NearbyActivity extends GrooveActivity implements OnMapReadyCallback {

    private static final String TAG = "NearbyActivity";
    private static final String KEY_CLOSEST = "ClosestDriver";
    private static final String KEY_FARTHEST = "FarthestDriver";

    private TextView mSinceText;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private int CIRCLE_COLOR;
    private Double mClosestDriver;
    private Double mFarthestDriver;
    private LatLng mMyLocation;
    private boolean mMoveCamera = true;
    private Map<String, Location> mMarkers = new HashMap<>();

    @Inject
    FirebaseClient mFirebase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mClosestDriver = savedInstanceState.getDouble(KEY_CLOSEST, 0);
            mFarthestDriver = savedInstanceState.getDouble(KEY_FARTHEST, 1000);

        }
        CIRCLE_COLOR = getResources().getColor(R.color.my_circle);

        setContentView(R.layout.activity_nearby);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSinceText = (TextView) findViewById(R.id.activity_nearby_closest);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_nearby_map);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_nearby_map, mMapFragment).commit();
        }
        mMapFragment.getMapAsync(this);
    }

    private void updateUI() {
        if (mClosestDriver != null)
            mSinceText.setText(getString(R.string.closest_driver, mClosestDriver.intValue()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(GatherService.newIntent(this), mConnection, Context.BIND_AUTO_CREATE);
        updateUI();
        updateMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(KEY_CLOSEST, mClosestDriver);
        outState.putDouble(KEY_FARTHEST, mFarthestDriver);
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMyLocationEnabled(false);
        map.setTrafficEnabled(true);
        map.setOnMarkerClickListener(null);
        map.clear();
        mMap = map;
        mFirebase.getNearbyDrivers(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mMarkers.put(dataSnapshot.getKey(), dataSnapshot.getValue(Location.class));
                updateMap();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                mMarkers.put(dataSnapshot.getKey(), dataSnapshot.getValue(Location.class));
                updateMap();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mMarkers.remove(dataSnapshot.getKey());
                updateMap();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mFirebase.getLocation(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "New location");
                Location location = dataSnapshot.getValue(Location.class);
                mMyLocation = new LatLng(location.lat, location.lng);
                if (mFarthestDriver == null || mClosestDriver == null)
                    return;
                if (mMoveCamera) {
                    LatLng southeast = SphericalUtil.computeOffset(mMyLocation, (mFarthestDriver + mClosestDriver) / 2, 225);
                    LatLng northwest = SphericalUtil.computeOffset(mMyLocation, (mFarthestDriver + mClosestDriver) / 2, 45);
                    CameraUpdate animate = CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southeast, northwest), 10);
                    mMap.moveCamera(animate);
                    mMoveCamera = false;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mFirebase.getClosestDriver(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mClosestDriver = dataSnapshot.getValue(Double.class);
                updateUI();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mFirebase.getFarthestDriver(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFarthestDriver = dataSnapshot.getValue(Double.class);
                updateUI();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private GatherService mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GatherService.LocalBinder binder = (GatherService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public static Intent newIntent(Context context) {
        return new Intent(context, NearbyActivity.class);
    }

    private void updateMap() {
        if (mMap == null || mMyLocation == null || mMarkers == null)
            return;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mMyLocation).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.cars)));
        for (Location location : mMarkers.values()) {
            LatLng driverLocation = location.toLatLng();
            double dist = SphericalUtil.computeDistanceBetween(mMyLocation, driverLocation);
            mMap.addMarker(new MarkerOptions().position(driverLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            CircleOptions circle = new CircleOptions();
            circle.strokeWidth(0);
            circle.fillColor(CIRCLE_COLOR);
            circle.center(driverLocation);
            circle.radius(dist / 2);
            mMap.addCircle(circle);
        }
    }
}
