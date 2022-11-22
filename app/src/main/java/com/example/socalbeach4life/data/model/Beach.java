package com.example.socalbeach4life.data.model;

import com.google.android.gms.maps.model.LatLng;

public class Beach {
    public String id;
    public String name;
    public LatLng coordinates;
    public Double distanceInMiles;

    public Beach(String id, String name, LatLng coordinates, Double distanceInMiles) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.distanceInMiles = distanceInMiles;
    }

}
