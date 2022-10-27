package com.example.socalbeach4life;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.socalbeach4life.databinding.ActivityMapsBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String BASE_URL = "https://api.yelp.com/v3/";
    private String API_KEY = "hGQVfT2R3h6Ww_y-djRk-G431FSipINxuilnFFBcwFmbJsO2azyVCi2ZEjRUYL4gSi4_D-PdXg6OqVKiotXxCyPWphPDN3D9dfi5cp1EwGEu2KF5XqfhXMSTcpBZY3Yx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.TopView);
        mapFragment.getMapAsync(this);


        TextView BottomView = (TextView) findViewById(R.id.BottomView);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        YelpService yelpService = retrofit.create(YelpService.class);
        Call<Object> call = yelpService.search(
                "Bearer " + API_KEY, "beaches", "Los Angeles"
        );
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                // TROUBLESHOOT: must restart emulator after computer is closed or asleep
                LinkedTreeMap res = (LinkedTreeMap<String, Object>) response.body();
                ArrayList<LinkedTreeMap> beaches = (ArrayList<LinkedTreeMap>) res.get("businesses");
                Log.d("a", res.toString());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("a", call.toString());
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}