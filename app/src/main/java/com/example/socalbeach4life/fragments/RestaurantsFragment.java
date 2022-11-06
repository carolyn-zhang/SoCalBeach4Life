package com.example.socalbeach4life.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socalbeach4life.MainActivity;
import com.example.socalbeach4life.R;
import com.example.socalbeach4life.data.model.Restaurant;
import com.example.socalbeach4life.yelp.YelpAsyncResponse;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class RestaurantsFragment extends Fragment implements YelpAsyncResponse {

    private MainActivity main;
    private ScrollView restaurantsScrollView;
    private Map<Integer, String> days = new HashMap<>();
    private int radius = 1000;
    private YelpService yelpService = new YelpService();
    private Context context;
    private FrameLayout parentView;
    public Boolean firstLoad = true;

    public RestaurantsFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        parentView = (FrameLayout) view;
        main = (MainActivity) getActivity();
        this.context = getContext();
        restaurantsScrollView = view.findViewById(R.id.restaurantsScrollView);

        // Specify this class as delegate for async yelp call so
        // api call results will be returned to the processFinish() override
        // https://www.yelp.com/developers/documentation/v3/business_search
        // yelpService.executeTask(this, "businesses/search", "term", "beach", "location", "Los Angeles", "sort_by", "distance");
        // yelpService.executeTask(this, "businesses/search", "term", "restaurants", "location", "Los Angeles", "sort_by", "distance");

    }

    @Override
    public void processFinish(MainActivity main, String output) {
        this.main = main;
        String endpoint = output.substring(0, output.indexOf('|'));
        output = output.substring(output.indexOf('|') + 1);
        JsonObject convertedObject = new Gson().fromJson(output, JsonObject.class);

        if (endpoint.equals("businesses/search")) { // draw markers on map
            JsonArray restaurants = (JsonArray) convertedObject.get("businesses");

            for (JsonElement restaurant : restaurants) {
                JsonObject resObj = restaurant.getAsJsonObject();
                String name = resObj.get("name").toString();
                name = name.substring(1, name.length() - 1);
                String id = resObj.get("id").toString();
                id = id.substring(1, id.length() - 1);
                Double latitude = resObj.get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
                Double longitude = resObj.get("coordinates").getAsJsonObject().get("longitude").getAsDouble();
                main.mapsFragment.setMarker(latitude, longitude, name, "Restaurant " + id);
            }

        } else { // display restaurant info
            main.beachesFragment.beachesScrollView.removeAllViews();
            main.replaceBottomView(main.restaurantsFragment);;
            parentView.removeAllViews();

            String restaurantID = endpoint.substring(endpoint.indexOf('/') + 1);
            Double latitude = convertedObject.get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
            Double longitude = convertedObject.get("coordinates").getAsJsonObject().get("longitude").getAsDouble();

            // Display information about restaurant
            restaurantsScrollView = new ScrollView(context);
            LinearLayout restaurantLayout = new LinearLayout(restaurantsScrollView.getContext());
            restaurantLayout.setBackgroundColor(Color.parseColor("white"));
            restaurantLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);

            // return button
            Button returnButton = new Button(restaurantLayout.getContext());
            returnButton.setText("Back To Beach Info");
            returnButton.setOnClickListener((new View.OnClickListener() {
                public void onClick(View v) {
                    main.mapsFragment.resetCamera();
                    main.replaceBottomView(main.beachesFragment);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            yelpService.executeTask(main, main.beachesFragment, "businesses/" + main.currentBeachID);
                        }
                    }, 500);
                    // main.beachesFragment.setView(main.beachesFragment.beachInfolayout);
                    //main.beachesFragment.setView(main.beachesFragment.beachInfolayout);
                }
            }));
            restaurantLayout.addView(returnButton, layoutParams);

            // name text
            String name = convertedObject.get("name").getAsString();
            TextView nameTV = new TextView(restaurantLayout.getContext());
            nameTV.setText(name);
            restaurantLayout.addView(nameTV);

            // location text
            JsonObject locationObj = convertedObject.get("location").getAsJsonObject();
            String address = locationObj.get("display_address").getAsJsonArray().get(0).getAsString() + "\n";
            String city = locationObj.get("city").getAsString() + "\n";
            TextView addressTV = new TextView(restaurantLayout.getContext());
            TextView cityTV = new TextView(restaurantLayout.getContext());
            addressTV.setText(address);
            cityTV.setText(city);
            restaurantLayout.addView(addressTV, layoutParams);
            restaurantLayout.addView(cityTV, layoutParams);

            // hours text
            if(convertedObject.get("hours") != null) {
                JsonArray hoursArray = convertedObject.get("hours").getAsJsonArray();
                JsonArray openHoursArray = hoursArray.get(0).getAsJsonObject().get("open").getAsJsonArray();
                days.put(0, "Monday");
                days.put(1, "Tuesday");
                days.put(2, "Wednesday");
                days.put(3, "Thursday");
                days.put(4, "Friday");
                days.put(5, "Saturday");
                days.put(6, "Sunday");
                for (int i = 0; i < openHoursArray.size(); i++) {
                    JsonObject dayHours = openHoursArray.get(i).getAsJsonObject();
                    Integer startInt = dayHours.get("start").getAsInt();
                    Integer endInt = dayHours.get("end").getAsInt();
                    String start = "";
                    String end = "";
                    if (startInt < 1000) {
                        start = startInt.toString().substring(0, 1) + ":" + startInt.toString().substring(1) + " AM";
                    } else if (startInt < 1200) {
                        start = startInt.toString().substring(0, 2) + ":" + startInt.toString().substring(2) + " AM";
                    } else if (startInt < 1300) {
                        start = startInt.toString().substring(0, 2) + ":" + startInt.toString().substring(2) + " PM";
                    } else if (startInt < 2200) {
                        startInt -= 1200;
                        start = startInt.toString().substring(0, 1) + ":" + startInt.toString().substring(1) + " PM";
                    } else {
                        startInt -= 1200;
                        start = startInt.toString().substring(0, 2) + ":" + startInt.toString().substring(2) + " PM";
                    }
                    if (endInt < 1000) {
                        end = endInt.toString().substring(0, 1) + ":" + endInt.toString().substring(1) + " AM";
                    } else if (endInt < 1200) {
                        end = endInt.toString().substring(0, 2) + ":" + endInt.toString().substring(2) + " AM";
                    } else if (endInt < 1300) {
                        end = endInt.toString().substring(0, 2) + ":" + endInt.toString().substring(2) + " PM";
                    } else if (startInt < 2200) {
                        endInt -= 1200;
                        end = endInt.toString().substring(0, 1) + ":" + endInt.toString().substring(1) + " PM";
                    } else {
                        endInt -= 1200;
                        end = endInt.toString().substring(0, 2) + ":" + endInt.toString().substring(2) + " PM";
                    }
                    Integer day = dayHours.get("day").getAsInt();
                    TextView dayTV = new TextView(restaurantLayout.getContext());
                    String dayString = days.get(day) + "   " + start + " - " + end + "\n";
                    dayTV.setText(dayString);
                    restaurantLayout.addView(dayTV, layoutParams);
                }
            } else {
                TextView noHoursTV = new TextView(restaurantLayout.getContext());
                String noHours = "Hours unavailable";
                noHoursTV.setText(noHours);
                restaurantLayout.addView(noHoursTV, layoutParams);
            }

            restaurantsScrollView.addView(restaurantLayout);
            parentView.addView(restaurantsScrollView);
            parentView.bringToFront();

            // bug where screen doesnt load on first click

            if(!firstLoad) {
                firstLoad = true;
            } else if (firstLoad) {
                firstLoad = false;
                yelpService.executeTask(main, main.restaurantsFragment, "businesses/" + restaurantID);
            }
            // main.beachesFragment.setView(restaurantsScrollView);
            //main.replaceBottomView(main.restaurantsFragment);
        }
    }
}
