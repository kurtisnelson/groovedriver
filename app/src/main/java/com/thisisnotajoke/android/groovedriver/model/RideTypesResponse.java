package com.thisisnotajoke.android.groovedriver.model;

import java.util.ArrayList;

public class RideTypesResponse {
    public ArrayList<RideType> rideTypes;

    public class RideType {
        public String id;
        public ArrayList<Driver> drivers;
    }

    public class Driver {
        String id;
        public Location location;
    }

    public class Location {
        public double lat;
        public double lng;
    }
}
