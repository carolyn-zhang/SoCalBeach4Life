package com.example.socalbeach4life;

import android.os.Bundle;

import com.example.socalbeach4life.fragments.MapsFragment;
import com.example.socalbeach4life.fragments.ProfileFragment;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.socalbeach4life.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceTopView(new MapsFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.maps:
                    replaceTopView(new MapsFragment());
                    break;
                case R.id.profile:
//                    Fragment profile = new ProfileFragment();
//                    profile.setArguments(extras);

                    replaceTopView(ProfileFragment.newInstance(extras.getString("userid")));
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
}