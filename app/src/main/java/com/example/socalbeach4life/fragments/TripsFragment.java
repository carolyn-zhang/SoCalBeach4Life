package com.example.socalbeach4life.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.socalbeach4life.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripsFragment extends Fragment {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_userid = "userid";

    // TODO: Rename and change types of parameters
    private String userid;

    public TripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userid Parameter 1.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripsFragment newInstance(String userid) {
        TripsFragment fragment = new TripsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_userid, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userid = getArguments().getString(ARG_userid);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        ScrollView tripsList = (ScrollView) view.findViewById(R.id.tripsScrollView);
        LinearLayout layout = new LinearLayout(tripsList.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // loop through ids to match to logged in user and get profile information
                for (DataSnapshot idSnapshot: snapshot.getChildren()) {
                    if(idSnapshot.getKey().equals(userid)) {
                        // get user trips
                        DataSnapshot tripsSnapshot = idSnapshot.child("trips");
                        // loop through user trips and add to trips scroll view
                        for (DataSnapshot tripSnapshot: tripsSnapshot.getChildren()) {
                            // create list item layout
                            LinearLayout line = new LinearLayout(layout.getContext());
                            line.setOrientation(LinearLayout.HORIZONTAL);
                            line.setBackgroundColor(Color.parseColor("white"));
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(10, 10, 10, 10);

                            // get trip info
                            final String getLocation = tripSnapshot.child("location").getValue(String.class);
                            final String getStartTime = tripSnapshot.child("start_time").getValue(String.class);
                            final String getArrivalTime = tripSnapshot.child("arrival_time").getValue(String.class);

                            // create text views for each parameter
                            TextView location = new TextView(layout.getContext());
                            location.setPadding(10, 10, 10, 10);
                            location.setText(getLocation + " start time:" + getStartTime + " arrival time:" + getArrivalTime);
                            location.setTextSize(20);

                            line.addView(location);

                            layout.addView(line, layoutParams);

                        }

                        tripsList.addView(layout);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
}