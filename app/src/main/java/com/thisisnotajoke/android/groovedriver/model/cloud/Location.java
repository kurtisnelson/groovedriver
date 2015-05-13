package com.thisisnotajoke.android.groovedriver.model.cloud;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Location implements Serializable {
    public double lat;
    public double lng;

    public Location() {

    }
    public Location(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
    }

    public Location(android.location.Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
    }

    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }
}
