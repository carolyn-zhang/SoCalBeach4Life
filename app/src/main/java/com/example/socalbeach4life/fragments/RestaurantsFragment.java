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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantsFragment extends Fragment implements YelpAsyncResponse {

    private MainActivity main;
    private ScrollView restaurantsScrollView;
    private Map<Integer, String> days = new HashMap<>();
    private YelpService yelpService = new YelpService();
    private Context context;
    private FrameLayout parentView;
    public Boolean firstLoad = true;
    public int radius = 1000;
    private String[] menuItems = new String[] {"Barbecue Plate", "Biscuits and Gravy", "Chicken Fried Rice", "Tonkatsu Ramen", "Fish and Chips", "Ranchera Steak Burrito", "Pesto Chicken Sandwich", "Black Cod with Miso", "Portebello Mushroom Burger", "Blackend Redfish", "Chicken Gumbo", "Breaded Pork Tenderloin", "Buffalo Wings", "Lemon Pepper Wings", "Caesar Salad", "Seasoned Fries", "Charbroiled Oysters", "Carbonara Pasta", "Chicago Deep Dish Pizza", "Clam Chowder", "Margherita Pizza", "Double-Double Cheeseburger", "Philly Cheesesteak", "Pork Cutlet Rice", "Chicken and Waffles", "Blue Crab Fried Rice", "Alfredo Linguini", "Chicken Noodle Soup", "Salmon Teriyaki Bowl", "Chicken Teriyaki Bowl", "Goat Cheese Salad", "Lobster Roll", "French Onion Soup", "Rib-eye Steak", "Chicken Tenders", "Center-cut Sirloin", "BBQ Chicken Pizza", "Chicken Madeira", "Alfredo Chicken", "Beef Lasagna", "Shredded Beef Sandwich"};

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

            // draw circle of restaurants
            if(main.currentCircle != null) main.currentCircle.remove();
            // Instantiating CircleOptions to draw a circle around the marker
            CircleOptions circleOptions = new CircleOptions();
            // Specifying the center of the circle
            circleOptions.center(main.currentBeach.coordinates);
            // Radius of the circle
            circleOptions.radius(radius * 1.3);
            // Border color of the circle
            circleOptions.strokeColor(Color.BLACK);
            // Fill color of the circle
            circleOptions.fillColor(0x30ff0000);
            // Border width of the circle
            circleOptions.strokeWidth(2);
            // Adding the circle to the GoogleMap
            main.currentCircle = main.mapsFragment.googleMap.addCircle(circleOptions);

            for (int i = main.mapsFragment.markerArray.size() - 1; i > -1; i--) {
                Marker m = main.mapsFragment.markerArray.get(i);
                String mTag = (String) m.getTag();
                if(mTag.contains("Restaurant")) {
                    m.remove();
                    main.mapsFragment.markerArray.remove(i);
                }
            }

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
                            yelpService.executeTask(main, main.beachesFragment, "businesses/" + main.currentBeach.id);
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
            String city = locationObj.get("city").getAsString() + ", CA";
            address = address + ", " + city;
            TextView addressTV = new TextView(restaurantLayout.getContext());
            addressTV.setText(address);
            restaurantLayout.addView(addressTV);

            // menu text
            TextView menuTV = new TextView(restaurantLayout.getContext());
            String menu = "Menu: ";
            menuTV.setText(menu);
            restaurantLayout.addView(menuTV, layoutParams);
            ArrayList<Integer> indices = new ArrayList<Integer>();
            for(int i = 0; i < 3; i++) {
                Integer randIndex = (int) (Math.random() * menuItems.length);
                while(indices.contains(randIndex))
                    randIndex = (int) (Math.random() * menuItems.length);
                TextView menuItemTV = new TextView(restaurantLayout.getContext());
                String menuItem = menuItems[randIndex];
                menuItemTV.setText(menuItem);
                restaurantLayout.addView(menuItemTV);
                indices.add(randIndex);
            }

            // hours text
            TextView hoursTV = new TextView(restaurantLayout.getContext());
            String hours = "Hours of Operation: ";
            hoursTV.setText(hours);
            restaurantLayout.addView(hoursTV, layoutParams);
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
