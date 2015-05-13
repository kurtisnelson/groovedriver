package com.thisisnotajoke.android.groovedriver;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Comparator;

public class CloseComparator implements Comparator<LatLng> {
    private final LatLng mBase;

    public CloseComparator(LatLng base) {
        mBase = base;
    }

    @Override
    public int compare(LatLng lhs, LatLng rhs) {
        double left = SphericalUtil.computeDistanceBetween(mBase, lhs);
        double right = SphericalUtil.computeDistanceBetween(mBase, rhs);
        return (int) (left - right);
    }
}
