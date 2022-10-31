package com.example.socalbeach4life.data.model;

public class Beach {
    public Double latitude;
    public Double longitude;
    public String id;

    public Beach(String id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
