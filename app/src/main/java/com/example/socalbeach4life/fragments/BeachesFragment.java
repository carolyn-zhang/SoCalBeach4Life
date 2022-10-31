package com.example.socalbeach4life.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.socalbeach4life.MainActivity;
import com.example.socalbeach4life.R;
import com.example.socalbeach4life.data.model.Beach;
import com.example.socalbeach4life.yelp.YelpAsyncResponse;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class BeachesFragment extends Fragment implements YelpAsyncResponse {

    private YelpService yelpService = new YelpService();
    private LinearLayout beachListlayout;
    private LinearLayout beachInfolayout;
    private ScrollView beachesScrollView;
    private MainActivity main;
    // beachMap maps the beaches name to its corresponding beach object
    // public Map<String, Beach> beachMap = new HashMap<String, Beach>();

    public BeachesFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beaches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        main = (MainActivity) getActivity();
        beachesScrollView = (ScrollView) view.findViewById(R.id.beachesScrollView);

        beachListlayout = new LinearLayout(beachesScrollView.getContext());
        beachInfolayout = new LinearLayout(beachesScrollView.getContext());
        // Specify this class as delegate for async yelp call so
        // api call results will be returned to the processFinish() override
        // https://www.yelp.com/developers/documentation/v3/business_search
        yelpService.executeTask(this, "businesses/search", "term", "beach", "location", "Los Angeles", "sort_by", "distance");
    }

    @Override
    public void processFinish(String output) {
        String endpoint = output.substring(0, output.indexOf('|'));
        output = output.substring(output.indexOf('|') + 1);
        JsonObject convertedObject = new Gson().fromJson(output, JsonObject.class);

        if(endpoint.equals("businesses/search")) {
            JsonArray beaches = (JsonArray) convertedObject.get("businesses");

            beachListlayout.setOrientation(LinearLayout.VERTICAL);
            beachListlayout.setBackgroundColor(Color.parseColor("blue"));
            for (JsonElement beach : beaches) {
                JsonObject beachObj = beach.getAsJsonObject();
                LinearLayout line = new LinearLayout(beachListlayout.getContext());
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setBackgroundColor(Color.parseColor("white"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);

                TextView beachName = new TextView(beachListlayout.getContext());
                String name = beachObj.get("name").toString();
                beachName.setPadding(10, 10, 10, 10);
                beachName.setText(name.substring(1, name.length() - 1));
                beachName.setTextSize(20);
                beachName.setWidth((int) (800));

                TextView distance = new TextView(beachListlayout.getContext());
                double distanceInMiles = (beachObj.get("distance").getAsDouble() / 1609);
                distance.setPadding(10, 10, 10, 10);
                distance.setText(String.format("%.2f", distanceInMiles) + " mi");
                distance.setTextSize(20);
                distance.setWidth((int) (300));
                distance.setGravity(Gravity.END);

//                beachMap.put(name, new Beach(beachObj.get("id").getAsString(),
//                                            beachObj.get("coordinates").getAsJsonObject().get("latitude").getAsDouble(),
//                                            beachObj.get("coordinates").getAsJsonObject().get("longitude").getAsDouble()
//                                            ));

                line.addView(beachName);
                line.addView(distance);

                // TODO: set on click listener to display selected beach on map based on ID
                // TODO: https://www.yelp.com/developers/documentation/v3/business
                line.setOnClickListener(v -> yelpService.executeTask(this, "businesses/" + beachObj.get("id").getAsString()));
                beachListlayout.addView(line, layoutParams);

                Double latitude = beachObj.get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
                Double longitude = beachObj.get("coordinates").getAsJsonObject().get("longitude").getAsDouble();
                main.mapsFragment.setMarker(latitude, longitude);
            }
            beachesScrollView.addView(beachListlayout);
        } else if(endpoint.contains("businesses/")) {
            String beachID = endpoint.substring(endpoint.indexOf('/') + 1);
            Double latitude = convertedObject.get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
            Double longitude = convertedObject.get("coordinates").getAsJsonObject().get("longitude").getAsDouble();

            // TODO: display information about beach
            beachesScrollView.removeAllViews();
            Button returnButton = new Button(beachesScrollView.getContext());
            returnButton.setText("Back To List");
            returnButton.setOnClickListener((new View.OnClickListener() {
                public void onClick(View v) {
                    main.mapsFragment.resetCamera();
                    beachesScrollView.removeAllViews();
                    beachesScrollView.addView(beachListlayout);
                }
            }));
            TextView locationTV = new TextView(beachesScrollView.getContext());
            locationTV.setText(convertedObject.get("location").toString());
            beachInfolayout.addView(returnButton);
            beachInfolayout.addView(locationTV);
            beachesScrollView.addView(beachInfolayout);


            // move google maps camera location to selected beach
            main.mapsFragment.setLocation(latitude, longitude);
            Log.d("a", "a");
        }
    }
}
