package com.pivotal.bootcamp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by administrator on 2015-07-16.
 */
public class StoreLocation {
    LatLng location;
    String name;
    double distanceFromCurrentLocation;

    public StoreLocation() {
        //Empty constructor
    }

    public StoreLocation(LatLng location, String name, double distanceFromCurrentLocation) {
        this.location = location;
        this.name = name;
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
    }
}
