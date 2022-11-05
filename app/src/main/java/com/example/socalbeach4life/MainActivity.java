package com.example.socalbeach4life;

import android.os.Bundle;

import com.example.socalbeach4life.fragments.BeachesFragment;
import com.example.socalbeach4life.fragments.MapsFragment;
import com.example.socalbeach4life.fragments.ProfileFragment;
import com.example.socalbeach4life.fragments.RestaurantsFragment;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.socalbeach4life.databinding.ActivityMainBinding;
import com.example.socalbeach4life.fragments.TripsFragment;
import com.example.socalbeach4life.maps.TaskLoadedCallback;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity implements TaskLoadedCallback {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public MapsFragment mapsFragment = new MapsFragment();
    public RestaurantsFragment restaurantsFragment = new RestaurantsFragment();
    public BeachesFragment beachesFragment = new BeachesFragment();
    public String currentBeachID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
//        restaurantsFragment = new RestaurantsFragment();
//        beachesFragment = new BeachesFragment();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceTopView(mapsFragment);

        replaceBottomView(restaurantsFragment);
        replaceBottomView(beachesFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.maps:
                    replaceTopView(mapsFragment);
                    replaceBottomView(beachesFragment);
                    break;
                case R.id.profile:
                    replaceTopView(ProfileFragment.newInstance(extras.getString("userid")));
                    replaceBottomView(TripsFragment.newInstance(extras.getString("userid")));
                    break;
            }
            return true;
        });

    }

    public void replaceTopView(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.top_view, fragment);
        fragmentTransaction.commit();
    }

    public void replaceBottomView(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_view, fragment);
        // fragmentTransaction.add(R.id.bottom_view, restaurantsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onTaskDone(String duration, Object... values) {
        mapsFragment.etaButton.setText(duration);
        if (mapsFragment.currentPolyline != null)
            mapsFragment.currentPolyline.remove();
        mapsFragment.currentPolyline = mapsFragment.googleMap.addPolyline((PolylineOptions) values[0]);
    }
}