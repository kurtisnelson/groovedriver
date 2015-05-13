package com.thisisnotajoke.android.groovedriver.model.cloud;

import com.github.davidmoten.geo.GeoHash;
import com.thisisnotajoke.android.groovedriver.model.lyft.RideTypesResponse;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DriverPosition implements Serializable {
    public String geohash;
    public String id;
    public String type;
    public double lat;
    public double lng;
    public DateTime time;

    //from lyft
    public DriverPosition(DateTime time, RideTypesResponse.Driver driver, String type) {
        this.id = driver.id;
        this.lat = driver.location.lat;
        this.lng = driver.location.lng;
        this.geohash = GeoHash.encodeHash(lat, lng);
        this.time = time.toDateTimeISO();
        this.type = type;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("lat", lat);
        map.put("lng", lng);
        map.put("time", time.toString());
        map.put("geohash", geohash);
        map.put("type", type);
        return map;
    }
}
