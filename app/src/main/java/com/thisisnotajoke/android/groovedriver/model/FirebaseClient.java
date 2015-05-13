package com.thisisnotajoke.android.groovedriver.model;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.thisisnotajoke.android.groovedriver.model.cloud.DriverPosition;
import com.thisisnotajoke.android.groovedriver.model.cloud.Location;
import com.thisisnotajoke.android.groovedriver.model.lyft.RideTypesResponse;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class FirebaseClient {
    private static final String FIREBASE_NAME = "blinding-fire-9814";
    private static final String TAG = "FirebaseClient";
    private final Firebase mFirebase;

    public FirebaseClient() {
        mFirebase = new Firebase("https://"+FIREBASE_NAME+".firebaseio.com/");
    }

    public void saveDrivers(ArrayList<RideTypesResponse.RideType> rideTypes) {
        Firebase ref = mFirebase.child("scrapedPositions").child("lyft");
        DateTime time = new DateTime();
        for(RideTypesResponse.RideType rideType : rideTypes) {
            if(rideType.drivers == null)
                continue;
            for(RideTypesResponse.Driver driver : rideType.drivers) {
                Firebase carRef = ref.child(driver.id);
                Map<String, Object> map = new DriverPosition(time, driver, rideType.id).toMap();
                carRef.push().setValue(map);
            }
        }
    }

    public void facebookLogin(String token) {
        if(token == null) {
            mFirebase.unauth();
        } else {
            mFirebase.authWithOAuthToken("facebook", token, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.e(TAG, "Could not authenticate to firebase with facebook");
                }
            });
        }
    }

    public void getClosestDriver(ValueEventListener valueEventListener) {
        privateUserData().child("closestDriver").addValueEventListener(valueEventListener);
    }

    public void setClosestDriver(double closestDriver) {
        privateUserData().child("closestDriver").setValue(closestDriver);
    }

    public void getFarthestDriver(ValueEventListener valueEventListener) {
        privateUserData().child("farthestDriver").addValueEventListener(valueEventListener);
    }

    public void setFarthestDriver(double farthestDriver) {
        privateUserData().child("farthestDriver").setValue(farthestDriver);
    }

    public void setLocation(Location location) {
        privateUserData().child("location").setValue(location);
    }

    public void getLocation(ValueEventListener listener) {
        privateUserData().child("location").addValueEventListener(listener);
    }

    private Firebase privateUserData() {
        return mFirebase.child("users_private").child(mFirebase.getAuth().getUid());
    }

    public void setNearbyDrivers(PriorityQueue<LatLng> drivers) {
        List<Location> locations = new ArrayList<>();
        for(LatLng driver : drivers) {
            locations.add(new Location(driver));
        }
        privateUserData().child("drivers").setValue(locations);
    }

    public void getNearbyDrivers(ChildEventListener listener) {
        privateUserData().child("drivers").addChildEventListener(listener);
    }

    public Firebase getClient() {
        return mFirebase;
    }
}
