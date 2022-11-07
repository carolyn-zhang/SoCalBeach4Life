package com.example.socalbeach4life.data.model;

import com.google.android.gms.maps.model.LatLng;

public class Beach {
    public String id;
    public LatLng coordinates;

    public Beach(String id, LatLng coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }

}
