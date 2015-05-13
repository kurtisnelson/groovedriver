package com.thisisnotajoke.android.groovedriver.model;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.thisisnotajoke.android.groovedriver.model.cloud.DriverPosition;
import com.thisisnotajoke.android.groovedriver.model.lyft.RideTypesResponse;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseClient {
    private static final String FIREBASE_NAME = "blinding-fire-9814";
    private static final String TAG = "FirebaseClient";
    private final Firebase mFirebase;

    public FirebaseClient() {
        mFirebase = new Firebase("https://"+FIREBASE_NAME+".firebaseio.com/");
    }

    public void saveDrivers(ArrayList<RideTypesResponse.RideType> rideTypes) {
        Firebase ref = mFirebase.child("scrapedPositions/lyft");
        DateTime time = new DateTime();
        for(RideTypesResponse.RideType rideType : rideTypes) {
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
}
