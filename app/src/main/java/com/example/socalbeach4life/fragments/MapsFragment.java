package com.example.socalbeach4life.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Looper;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.socalbeach4life.MainActivity;
import com.example.socalbeach4life.maps.FetchURL;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.example.socalbeach4life.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    // create DatabaseReference object to access realtime database
    public DatabaseReference databaseReference;

    public GoogleMap googleMap;
    private Boolean mapReady = false;
    public MainActivity main;
    public Polyline currentPolyline;
    public Marker currentBeachMarker;
    public Button etaButton;
    public Button tripButton;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
    public ArrayList<Marker> markerArray = new ArrayList<>();
    FusedLocationProviderClient client;
    // Initialize current location to LA coordinates
    public double currLocLatitude = 34.0522;
    public double currLocLongitude = -118.2437;

    public YelpService yelpService = new YelpService();

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapReady = true;
        LatLng LA = new LatLng(currLocLatitude, currLocLongitude);
        googleMap.setMinZoomPreference(10);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LA));
        googleMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }


    public void resetCamera() {
        LatLng LA = new LatLng(currLocLatitude, currLocLongitude);
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

    public ArrayList<Integer> handleBeachMarkerClick(ArrayList<String> markerTags, String tag) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = markerTags.size() - 1; i > -1; i--) {
            String mTag = markerTags.get(i);
            if(mTag.contains("Parking") || mTag.contains("Restaurant")) {
                res.add(i);
            }
        }

        // Show beach information in bottom fragment
        String beachID = tag.substring(tag.indexOf(' ') + 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                yelpService.executeTask(main, main.beachesFragment, "businesses/" + beachID);
            }
        }, 500);

        return res;
    }

    public void handleParkingMarkerClick(String url) {
        new FetchURL(this.getContext()).execute(url, "driving");
    }

    public void handleRestaurantMarkerClick(String restaurantID, String url) {
        yelpService.executeTask(main, main.restaurantsFragment, "businesses/" + restaurantID);
        new FetchURL(this.getContext()).execute(url, "walking");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        String tag = (String) marker.getTag();

        // remove any displayed route if applicable
        if(currentPolyline != null)
            currentPolyline.remove();

        LatLng pos = marker.getPosition();

        double latitude = pos.latitude;
        double longitude = pos.longitude;
        if (tag.contains("Beach")) {
            currentBeachMarker = marker;
            etaButton.setVisibility(View.GONE);
            tripButton.setVisibility(View.GONE);

            if(main != null) {
                if(main.restaurantsFragment != null)
                    main.restaurantsFragment.firstLoad = false;
                main.replaceBottomView(main.beachesFragment);
            }

            setLocation(latitude, longitude);
            main = (MainActivity) getActivity();

            // remove other parking lot and restaurant markers
            ArrayList<String> markerTags = new ArrayList<>();
            for (int i = markerArray.size() - 1; i > -1; i--) {
                Marker m = (Marker) markerArray.get(i);
                String mTag = (String) m.getTag();
                markerTags.add(mTag);
            }
            ArrayList<Integer> tagsToRemove = handleBeachMarkerClick(markerTags, tag);
            for (int i = 0; i < tagsToRemove.size(); i++) {
                int index = tagsToRemove.get(i);
                markerArray.remove(index);
            }
        } else if (tag.contains("Parking")) {
            // driving route to parking lot
            etaButton.setVisibility(View.VISIBLE);
            tripButton.setVisibility(View.VISIBLE);
            LatLng currLoc = new LatLng(currLocLatitude, currLocLongitude);
            String url = getRouteURL(pos, currLoc, "driving");

            handleParkingMarkerClick(url);
        } else if (tag.contains("Restaurant")) {
            // show restaurant information in bottom fragment
            main = (MainActivity) getActivity();
            String restaurantID = tag.substring(tag.indexOf(' ') + 1);

            // walking route to restaurant from beach
            etaButton.setVisibility(View.VISIBLE);
            tripButton.setVisibility(View.GONE);
            String url = getRouteURL(pos, currentBeachMarker.getPosition(), "walking");
            handleRestaurantMarkerClick(restaurantID, url);
        }
        return false;
    }

    public String getRouteURL(LatLng origin, LatLng dest, String directionMode) {
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
                + parameters + "&key=AIzaSyBobTTzoNhhHoQQFa9iY7CPG_kYQrxdRtU";
        return url;
    }

    public void setLocation(double latitude, double longitude) {
        if(mapReady) {
            LatLng newLocation = new LatLng(latitude, longitude);
            googleMap.setMinZoomPreference(14);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize view
        View view =  inflater.inflate(R.layout.fragment_maps, container, false);
        etaButton = view.findViewById(R.id.eta_button);
        tripButton = view.findViewById(R.id.start_end_trip_button);

        // Initialize location client
        client = LocationServices
                .getFusedLocationProviderClient(
                        getActivity());
        // check condition
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            getCurrentLocation();

        } else {
            // When permission is not granted
            // Call method
            requestPermissions(
                    new String[] {
                            Manifest.permission
                                    .ACCESS_FINE_LOCATION,
                            Manifest.permission
                                    .ACCESS_COARSE_LOCATION },
                    100);
        }
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
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");
        etaButton.setVisibility(View.GONE);
        tripButton.setVisibility(View.GONE);
        tripButton.setText("Start Trip");
        tripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean start = tripButton.getText() == "Start Trip";
                final int[] numTrips = {0};
                databaseReference.child("users").child(getActivity().getIntent().getExtras().getString("userid"))
                        .child("trips").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        numTrips[0] = (int) snapshot.getChildrenCount();

                        if(start) {
                            // get start time and store to database
                            String currentDateAndTime = sdf.format(new Date());

                            // store location
                            databaseReference.child("users").child(getActivity().getIntent().getExtras().getString("userid"))
                                    .child("trips").child(numTrips[0] + 1 + "").child("location").setValue(currentBeachMarker.getTitle());


                            databaseReference.child("users").child(getActivity().getIntent().getExtras().getString("userid"))
                                    .child("trips").child(numTrips[0] + 1 + "").child("start_time").setValue(currentDateAndTime);


                            tripButton.setText("End Trip");

                        } else {
                            // get end time and store to last entry in database
                            String currentDateAndTime = sdf.format(new Date());

                            databaseReference.child("users").child(getActivity().getIntent().getExtras().getString("userid"))
                                    .child("trips").child(numTrips[0] + "").child("arrival_time").setValue(currentDateAndTime);

                            tripButton.setText("Start Trip");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check condition
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {
            // When permission are granted
            // Call  method
            getCurrentLocation();
        }
        else {
            // When permission are denied
            // Display toast
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation()
    {
        // Initialize Location manager
        LocationManager locationManager
                = (LocationManager)getActivity()
                .getSystemService(
                        Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(
                    task -> {

                        // Initialize location
                        Location location
                                = task.getResult();
                        // Check condition
                        if (location != null) {
                            // When location result is not null set latitude
                            currLocLatitude = location.getLatitude();
                            // set longitude
                            currLocLongitude = location.getLongitude();
                            resetCamera();
                        }
                        else {
                            // When location result is null
                            // initialize location request
                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(
                                            1000)
                                    .setNumUpdates(1);

                            // Initialize location call back
                            LocationCallback
                                    locationCallback
                                    = new LocationCallback() {
                                @Override
                                public void
                                onLocationResult(
                                        LocationResult
                                                locationResult)
                                {
                                    // Initialize
                                    // location
                                    Location location1
                                            = locationResult
                                            .getLastLocation();
                                    // Set latitude
                                    currLocLatitude = location1.getLatitude();
                                    // set longitude
                                    currLocLongitude = location1.getLongitude();
                                    resetCamera();
                                }
                            };

                            // Request location updates
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    });
        }
        else {
            // When location service is not enabled open location setting
            startActivity(
                    new Intent(
                            Settings
                                    .ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}
