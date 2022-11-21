package com.example.socalbeach4life.data.model;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant {
    public String id;
    public String name;
    public LatLng coordinates;

    public Restaurant(String id, String name, LatLng coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }
}
