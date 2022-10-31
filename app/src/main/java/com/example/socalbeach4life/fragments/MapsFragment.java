package com.example.socalbeach4life.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.socalbeach4life.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    public GoogleMap googleMap;
    private Boolean mapReady = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapReady = true;
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void setLocation(double latitude, double longitude) {
        if(mapReady) {
            LatLng newLocation = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(newLocation));
            googleMap.setMinZoomPreference(15);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
}