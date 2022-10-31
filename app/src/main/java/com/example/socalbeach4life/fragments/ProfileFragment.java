package com.example.socalbeach4life.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socalbeach4life.Login;
import com.example.socalbeach4life.MainActivity;
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

                        TextView displayusername = (TextView) view.findViewById(R.id.user_name);
                        displayusername.setText(getName);

                        TextView displayuseremail = (TextView) view.findViewById(R.id.user_email);
                        displayuseremail.setText(getEmail);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        return view;

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}