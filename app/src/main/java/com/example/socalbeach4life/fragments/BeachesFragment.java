package com.example.socalbeach4life.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.socalbeach4life.MainActivity;
import com.example.socalbeach4life.R;
import com.example.socalbeach4life.data.model.Beach;
import com.example.socalbeach4life.yelp.YelpAsyncResponse;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;


public class BeachesFragment extends Fragment implements YelpAsyncResponse {
    // create DatabaseReference object to access realtime database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");

    private YelpService yelpService = new YelpService();
    public LinearLayout beachListlayout;
    public LinearLayout beachInfolayout;
    public ScrollView beachesScrollView;
    private TextView globalNumReviewsTV;
    private CheckBox anonymousReview;
    private int globalNumReviews = 0;
    private MainActivity main;
    private Map<Integer, String> days = new HashMap<>();
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
        //view.setBackgroundColor();
        beachesScrollView = (ScrollView) view.findViewById(R.id.beachesScrollView);

        beachListlayout = new LinearLayout(beachesScrollView.getContext());
        beachInfolayout = new LinearLayout(beachesScrollView.getContext());
        // Specify this class as delegate for async yelp call so
        // api call results will be returned to the processFinish() override
        // https://www.yelp.com/developers/documentation/v3/business_search
        yelpService.executeTask(main, this, "businesses/search", "term", "beach", "location", "Los Angeles", "sort_by", "distance");

    }

    @Override
    public void processFinish(MainActivity main, String output) {
        this.main = main;
        beachesScrollView.removeAllViews();
        String endpoint = output.substring(0, output.indexOf('|'));
        output = output.substring(output.indexOf('|') + 1);
        JsonObject convertedObject = new Gson().fromJson(output, JsonObject.class);

        if(endpoint.equals("businesses/search")) {
            JsonArray beaches = (JsonArray) convertedObject.get("businesses");

            beachListlayout.setOrientation(LinearLayout.VERTICAL);
            beachListlayout.setBackgroundColor(Color.parseColor("white"));
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
                name = name.substring(1, name.length() - 1);
                beachName.setPadding(10, 10, 10, 10);
                beachName.setText(name);
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
                line.setOnClickListener(v -> yelpService.executeTask(main,this, "businesses/" + beachObj.get("id").getAsString()));
                beachListlayout.addView(line, layoutParams);

                Double latitude = beachObj.get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
                Double longitude = beachObj.get("coordinates").getAsJsonObject().get("longitude").getAsDouble();
                main.mapsFragment.setMarker(latitude, longitude, name,"Beach " + beachObj.get("id").getAsString());
            }
            beachesScrollView.addView(beachListlayout);
        } else if(endpoint.contains("businesses/")) {
            for (int i = main.mapsFragment.markerArray.size() - 1; i > -1; i--) {
                Marker m = main.mapsFragment.markerArray.get(i);
                String mTag = (String) m.getTag();
                if(mTag.contains("Parking") || mTag.contains("Restaurant")) {
                    m.remove();
                    main.mapsFragment.markerArray.remove(i);
                }
            }

            Double latitude = convertedObject.get("coordinates").getAsJsonObject().get("latitude").getAsDouble();
            Double longitude = convertedObject.get("coordinates").getAsJsonObject().get("longitude").getAsDouble();
            main.currentBeach = new Beach(endpoint.substring(endpoint.indexOf('/') + 1), new LatLng(latitude, longitude));

            // TODO: display information about beach
            beachInfolayout = new LinearLayout(beachesScrollView.getContext());
            beachInfolayout.setOrientation(LinearLayout.VERTICAL);
            beachInfolayout.setBackgroundColor(Color.parseColor("white"));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);
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
            beachInfolayout.addView(returnButton, layoutParams);

            // restaurant radius button
            String resRadius = "Edit Restaurant Radius (meters)";
            TextView resRadiusTV = new TextView(beachesScrollView.getContext());
            resRadiusTV.setText(resRadius);
            beachInfolayout.addView(resRadiusTV);
            LinearLayout radiusLayout = new LinearLayout(beachesScrollView.getContext());
            radiusLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            buttonParams.setMargins(10, 10, 10, 10);
            for(int i = 1; i <= 3; i++) {
                Button radiusButton = new Button(beachesScrollView.getContext());
                String val = String.valueOf(i * 1000);
                radiusButton.setText(val);
                radiusButton.setBackgroundColor(Color.parseColor("grey"));
                radiusButton.setOnClickListener((new View.OnClickListener() {
                    public void onClick(View v) {
                        Button b = (Button)v;
                        String newRadius = b.getText().toString();
                        main.restaurantsFragment.radius = Integer.valueOf(newRadius);
                        yelpService.executeTask(main, main.restaurantsFragment,
                                "businesses/search",
                                "term", "best restaurants", "latitude", String.valueOf(latitude),
                                "longitude", String.valueOf(longitude),
                                "radius", newRadius, "limit", "10", "sort_by", "best_match");
                    }
                }));
                radiusLayout.addView(radiusButton, buttonParams);
            }
            beachInfolayout.addView(radiusLayout, layoutParams);

            // name text
            String name = convertedObject.get("name").getAsString();
            TextView nameTV = new TextView(beachesScrollView.getContext());
            nameTV.setText(name);
            beachInfolayout.addView(nameTV);

            // location text
            JsonObject locationObj = convertedObject.get("location").getAsJsonObject();
            String address = locationObj.get("display_address").getAsJsonArray().get(0).getAsString() + "\n";
            String city = locationObj.get("city").getAsString() + ", CA";
            address = address + ", " + city;
            TextView addressTV = new TextView(beachesScrollView.getContext());
            addressTV.setText(address);
            beachInfolayout.addView(addressTV, layoutParams);
//            System.out.println(convertedObject);
            // hours text
            TextView hoursTV = new TextView(beachesScrollView.getContext());
            String hours = "Hours of Operation: ";
            hoursTV.setText(hours);
            beachInfolayout.addView(hoursTV, layoutParams);
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
                for(int i = 0; i < openHoursArray.size(); i++) {
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
                    }  else if (endInt < 1200) {
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
                    TextView dayTV = new TextView(beachesScrollView.getContext());
                    String dayString = days.get(day) + ":   " + start + " - " + end + "\n";
                    dayTV.setText(dayString);
                    beachInfolayout.addView(dayTV);
                }
            } else {
                TextView noHoursTV = new TextView(beachesScrollView.getContext());
                String noHours = "Hours unavailable";
                noHoursTV.setText(noHours);
                beachInfolayout.addView(noHoursTV);
            }

            beachesScrollView.addView(beachInfolayout);

            // Display random Parking lot markers
            main.mapsFragment.setMarker(latitude + 0.0001 * Math.random() * 50, longitude + 0.0001 * Math.random() * 50, "Parking Lot 1", "Parking");
            main.mapsFragment.setMarker(latitude + 0.0001 * Math.random() * 50, longitude + 0.0001 * Math.random() * 50, "Parking Lot 2", "Parking");

            // TODO: initiate search for nearby restaurants
            // Call restaurants fragment
            // when restaurants are clicked replaceBottomView()
            // main.replaceBottomView(main.restaurantsFragment);
            yelpService.executeTask(main, main.restaurantsFragment,
                    "businesses/search",
                    "term", "best restaurants", "latitude", String.valueOf(latitude),
                    "longitude", String.valueOf(longitude),
                    "radius", String.valueOf(main.restaurantsFragment.radius), "limit", "10", "sort_by", "best_match");

            TextView reviewSection = new TextView(beachesScrollView.getContext());
            reviewSection.setText("Reviews");
            beachInfolayout.addView(reviewSection);

            // leave a review layout
            EditText starsReview = new EditText(beachesScrollView.getContext());
            starsReview.setHint("stars");
            starsReview.setInputType(InputType.TYPE_CLASS_NUMBER);
            beachInfolayout.addView(starsReview);
            CheckBox remainAnonymous = new CheckBox(beachesScrollView.getContext());
            remainAnonymous.setText("hide name in review?");
            beachInfolayout.addView(remainAnonymous);
            anonymousReview = remainAnonymous;
            Button leaveReviewButton = new Button(beachesScrollView.getContext());
            leaveReviewButton.setText("Leave a review");
            beachInfolayout.addView(leaveReviewButton);

            // add new review to database and update user interface with added review
            final int[] numReviews = {0};
            leaveReviewButton.setOnClickListener((new View.OnClickListener() {
                public void onClick(View v) {
                    globalNumReviews += 1;
                    String strNextId = String.valueOf(globalNumReviews);

                    // store review to database
                    databaseReference.child("reviews").child(name).child(strNextId)
                            .child("stars").setValue(Integer.parseInt(starsReview.getText().toString()));

                    if(!anonymousReview.isChecked()) { // user wants name to display
                        databaseReference.child("reviews").child(name).child(strNextId)
                                .child("user_name").setValue(getActivity().getIntent().getExtras().getString("name"));
                    }

                    // add review to layout because rendering from database not immediate
                    TextView review = new TextView(beachesScrollView.getContext());
                    if(!anonymousReview.isChecked()) {
                        review.setText("stars:" + Integer.parseInt(starsReview.getText().toString()) + " user name:"
                                + getActivity().getIntent().getExtras().getString("name"));
                    } else {
                        review.setText("stars:" + Integer.parseInt(starsReview.getText().toString()));
                    }

                    globalNumReviewsTV.setText("Number of reviews: " + globalNumReviews);
                    beachInfolayout.addView(review);

                    starsReview.getText().clear();
                }
            }));

            // display reviews
            databaseReference.child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot nameSnapshot: snapshot.getChildren()) {
                        globalNumReviews = 0;
                        TextView numReviewsTV = new TextView(beachesScrollView.getContext());
                        globalNumReviewsTV = numReviewsTV;
                        beachInfolayout.addView(globalNumReviewsTV);
                        if(nameSnapshot.getKey().equals(name)) {
                            // display number of reviews
                            globalNumReviews = (int) nameSnapshot.getChildrenCount();
                            globalNumReviewsTV.setText("Number of reviews: " + globalNumReviews);
                            // display reviews
                            for (DataSnapshot idSnapshot: nameSnapshot.getChildren()) {
                                int stars = idSnapshot.child("stars").getValue(Integer.class);
                                if(idSnapshot.child("user_name").exists()) {
                                    String user_name = idSnapshot.child("user_name").getValue(String.class);
                                    TextView review = new TextView(beachesScrollView.getContext());
                                    review.setText("stars:" + stars + " user name:" + user_name);
                                    beachInfolayout.addView(review);
                                } else { // no user name stored for this review
                                    TextView review = new TextView(beachesScrollView.getContext());
                                    review.setText("stars:" + stars);
                                    beachInfolayout.addView(review);
                                }
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //




            // move google maps camera location to selected beach
            main.mapsFragment.setLocation(latitude, longitude);
            main.mapsFragment.currentBeachMarker = main.mapsFragment.googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(name));
        }
    }
}
