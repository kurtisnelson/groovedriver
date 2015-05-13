package com.thisisnotajoke.android.groovedriver.model;

/**
 * Created by kurt on 5/4/15.
 */
public class LocationBody {

    public LocationBody(double lat, double lng) {
        rideType = "standard";
        location = new Location();
        location.lat = lat;
        location.lng = lng;
    }
    String rideType;
    Location location;

    class Location {
        double lat;
        double lng;
    }
}
