package com.example.socalbeach4life.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socalbeach4life.MainActivity;
import com.example.socalbeach4life.maps.FetchURL;
import com.example.socalbeach4life.maps.TaskLoadedCallback;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.socalbeach4life.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        {

    public GoogleMap googleMap;
    private Boolean mapReady = false;
    private MainActivity main;
    private ArrayList<Marker> markerArray = new ArrayList<Marker>();
    public Polyline currentPolyline;
    public Marker currentMarker;
    public Button etaButton;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapReady = true;
        // TODO: Set to user's current location
        LatLng LA = new LatLng(34.0522, -118.2437);
        googleMap.setMinZoomPreference(10);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LA));
        googleMap.setOnMarkerClickListener(this);
    }





    public void resetCamera() {
        // TODO: Also set back to user's current location.
        LatLng LA = new LatLng(34.0522, -118.2437);
        googleMap.setMinZoomPreference(10);
        googleMap.setMaxZoomPreference(10);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LA));
    }

    public void setMarker(double latitude, double longitude, String title, String tag) {
        if(mapReady) {
            LatLng newLocation = new LatLng(latitude, longitude);
            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(newLocation)
                    .title(title));
            // snippets = Parking, Restaurant, Beach
            // colors: Parking = green, Restaurant = blue, Beach = red
            if (newMarker != null) {
                newMarker.setTag(tag);
                if (tag.contains("Parking")) {
                    newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (tag.contains("Restaurant")) {
                    newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                } else if (tag.contains("Beach")) {
                    newMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }
            markerArray.add(newMarker);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        String tag = (String) marker.getTag();
        LatLng pos = marker.getPosition();
        currentMarker = marker;

        double latitude = pos.latitude;
        double longitude = pos.longitude;
        if (tag.contains("Beach")) {
            setLocation(latitude, longitude);

            // TODO: Hide other parking lot and restaurant markers
            for (int i = markerArray.size() - 1; i > -1; i--) {
                Marker m = markerArray.get(i);
                String mTag = (String) m.getTag();
                if(mTag.contains("Parking") || mTag.contains("Restaurant")) {
                    m.remove();
                    markerArray.remove(i);
                }
            }

            // Show beach information in bottom fragment
            main = (MainActivity) getActivity();
            String beachID = tag.substring(tag.indexOf(' ') + 1);
            YelpService yelpService = new YelpService();
            yelpService.executeTask(main.beachesFragment, "businesses/" + beachID);

            // test: show route to beach, TODO: move this, change to for parking lot
            // pos is the location of the beach marker clicked
            LatLng uscLoc = new LatLng(34.0224, -118.2851);
            String url = getRouteURL(pos, uscLoc, "driving");
            System.out.println(url);
            new FetchURL(this.getContext()).execute(url, "driving");
        } else if (tag.contains("Parking")) {
            // TODO: route to parking lot
            ;
        } else if (tag.contains("Restaurant")) {
            // TODO: show restaurant information in bottom fragment
            ;
        }
        return false;
    }

    private String getRouteURL(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Departure time
        String str_departure = "departure_time=now";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + str_departure;
        // Output format
        String output = "json";
        // Building the URL to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?"
                + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    public void setLocation(double latitude, double longitude) {
        if(mapReady) {
            LatLng newLocation = new LatLng(latitude, longitude);
            googleMap.setMinZoomPreference(15);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_maps, container, false);
        etaButton = view.findViewById(R.id.eta_button);
        return view;
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

