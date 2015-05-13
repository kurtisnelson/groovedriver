package com.thisisnotajoke.android.groovedriver.model.lyft;

import com.thisisnotajoke.android.groovedriver.model.cloud.Location;

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
}
