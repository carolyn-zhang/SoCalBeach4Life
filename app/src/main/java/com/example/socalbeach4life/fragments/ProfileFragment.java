package com.example.socalbeach4life.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socalbeach4life.Login;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.example.socalbeach4life.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_userid = "userid";

    // TODO: Rename and change types of parameters
    private String userid;

    public ProfileFragment() {
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
    public static ProfileFragment newInstance(String userid) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView displayuserid = (TextView) view.findViewById(R.id.user_id);
        displayuserid.setText(userid);


        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // loop through ids to match to logged in user and get profile information
                for (DataSnapshot idSnapshot: snapshot.getChildren()) {
                    if(idSnapshot.getKey().equals(userid)) {
                        final String getName = idSnapshot.child("name").getValue(String.class);
                        final String getEmail = idSnapshot.child("email").getValue(String.class);
                        final int getNumTrips = (int) idSnapshot.child("trips").getChildrenCount();

                        TextView displayusername = (TextView) view.findViewById(R.id.user_name);
                        displayusername.setText(getName);

                        TextView displayuseremail = (TextView) view.findViewById(R.id.user_email);
                        displayuseremail.setText(getEmail);

                        TextView displayusernumtrips = (TextView) view.findViewById(R.id.user_num_trips);
                        displayusernumtrips.setText("number of trips: " + getNumTrips);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        view.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Login.class));
            }
        });

        view.findViewById(R.id.invite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });

        return view;
    }

    public void composeEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "DOWNLOAD SOCALBEACH4LIFE");
        intent.putExtra(Intent.EXTRA_TEXT, "Start discovering beaches and restaurants today!\n\n"
                + "https://play.google.com/store/apps/details?id=us.socalbeach4life");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}