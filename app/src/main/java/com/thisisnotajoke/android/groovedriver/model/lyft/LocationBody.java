package com.thisisnotajoke.android.groovedriver.model.lyft;

import java.io.Serializable;

public class LocationBody implements Serializable {

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
