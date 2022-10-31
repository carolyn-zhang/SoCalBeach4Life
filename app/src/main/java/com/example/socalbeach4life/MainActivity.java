package com.example.socalbeach4life;

import android.os.Bundle;

import com.example.socalbeach4life.fragments.BeachesFragment;
import com.example.socalbeach4life.fragments.MapsFragment;
import com.example.socalbeach4life.fragments.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.socalbeach4life.databinding.ActivityMainBinding;
import com.example.socalbeach4life.fragments.TripsFragment;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    public MapsFragment mapsFragment = new MapsFragment();
    public BeachesFragment beachesFragment = new BeachesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceTopView(mapsFragment);

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

    private void replaceTopView(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.top_view, fragment);
        fragmentTransaction.commit();
    }

    private void replaceBottomView(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_view, fragment);
        fragmentTransaction.commit();
    }
}