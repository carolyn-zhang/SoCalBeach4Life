package com.example.socalbeach4life.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socalbeach4life.R;
import com.example.socalbeach4life.yelp.YelpAsyncResponse;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BeachesFragment extends Fragment implements YelpAsyncResponse {

    private YelpService asyncYelpService =new YelpService();

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

        // Specify this class as delegate for async yelp call so
        // api call results will be returned to the processFinish() override
        asyncYelpService.delegate = this;
        // https://www.yelp.com/developers/documentation/v3/business_search
        asyncYelpService.execute("businesses/search", "term", "beaches", "location", "Los Angeles", "sort_by", "distance");
    }

    @Override
    public void processFinish(String output) {
        String endpoint = output.substring(0, output.indexOf('|'));
        output = output.substring(output.indexOf('|') + 1);

        if(endpoint.equals("businesses/search")) {
            JsonObject convertedObject = new Gson().fromJson(output, JsonObject.class);
            JsonArray beaches = (JsonArray) convertedObject.get("businesses");

            View view = this.getView();
            ScrollView BeachList = (ScrollView) view.findViewById(R.id.beachesScrollView);

            LinearLayout layout = new LinearLayout(BeachList.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundColor(Color.parseColor("blue"));
            for (JsonElement beach : beaches) {
                JsonObject beachObj = beach.getAsJsonObject();
                LinearLayout line = new LinearLayout(layout.getContext());
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setBackgroundColor(Color.parseColor("white"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);

                TextView beachName = new TextView(layout.getContext());
                String name = beachObj.get("name").toString();
                beachName.setPadding(10, 10, 10, 10);
                beachName.setText(name.substring(1, name.length() - 1));
                beachName.setTextSize(20);
                beachName.setWidth((int) (800));

                TextView distance = new TextView(layout.getContext());
                double distanceInMiles = (beachObj.get("distance").getAsDouble() / 1609);
                distance.setPadding(10, 10, 10, 10);
                distance.setText(String.format("%.2f", distanceInMiles) + " mi");
                distance.setTextSize(20);
                distance.setWidth((int) (300));
                distance.setGravity(Gravity.END);

                line.addView(beachName);
                line.addView(distance);

                // TODO: set on click listener to display selected beach on map based on ID
                // TODO: https://www.yelp.com/developers/documentation/v3/business
                //line.setOnClickListener();
                layout.addView(line, layoutParams);
            }
            BeachList.addView(layout);
        }
    }
}
