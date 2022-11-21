package com.example.socalbeach4life;

import com.example.socalbeach4life.fragments.BeachesFragment;
import com.example.socalbeach4life.fragments.MapsFragment;
import com.example.socalbeach4life.fragments.RestaurantsFragment;
import com.example.socalbeach4life.maps.DataParser;
import com.example.socalbeach4life.maps.FetchURL;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.location.Location;
import android.location.LocationManager;

import androidx.fragment.app.Fragment;

import org.junit.runner.RunWith;
import org.mockito.plugins.MockMaker;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MapsFragment.class)
public class MapsFragmentUnitTests {

    @Test
    public void testBeachMarkerOnClick() {
        MapsFragment mapsFragment = new MapsFragment();
        RestaurantsFragment mockRestaurantsFragment = mock(RestaurantsFragment.class);
        BeachesFragment mockBeachesFragment = mock(BeachesFragment.class);
        YelpService mockYelpService = mock(YelpService.class);
        DatabaseReference mockDBReference = mock(DatabaseReference.class);
        MainActivity mockMainActivity = mock(MainActivity.class);

        mockMainActivity.restaurantsFragment = mockRestaurantsFragment;
        mockMainActivity.beachesFragment = mockBeachesFragment;

        mapsFragment.databaseReference = mockDBReference;
        mapsFragment.main = mockMainActivity;
        mapsFragment.yelpService = mockYelpService;
        ArrayList<String> markerTags = new ArrayList<>();
        markerTags.add("Parking fake");
        markerTags.add("Beach fake");
        markerTags.add("Restaurant fake");

        // when
        ArrayList<Integer> actual = mapsFragment.handleBeachMarkerClick(markerTags, "Beach mock_id");
        for (int i = 0; i < actual.size(); i++) {
            int index = actual.get(i);
            markerTags.remove(index);
        }

        // then
        assertEquals("Beach fake", markerTags.get(0));
    }

    @Test
    public void testParkingMarkerOnClick() throws Exception {
        MapsFragment mapsFragment = new MapsFragment();
        RestaurantsFragment mockRestaurantsFragment = mock(RestaurantsFragment.class);
        BeachesFragment mockBeachesFragment = mock(BeachesFragment.class);
        YelpService mockYelpService = mock(YelpService.class);
        DatabaseReference mockDBReference = mock(DatabaseReference.class);
        MainActivity mockMainActivity = mock(MainActivity.class);

        mockMainActivity.restaurantsFragment = mockRestaurantsFragment;
        mockMainActivity.beachesFragment = mockBeachesFragment;

        mapsFragment.databaseReference = mockDBReference;
        mapsFragment.main = mockMainActivity;
        mapsFragment.yelpService = mockYelpService;
        String mock_url = "mock URL";
        FetchURL mockFetchURL = mock(FetchURL.class);
        PowerMockito.whenNew(FetchURL.class).withAnyArguments().thenReturn(mockFetchURL);
        //PowerMockito.when(mockFetchURL.execute(mock_url, "driving")).thenReturn();

        // when
        mapsFragment.handleParkingMarkerClick(mock_url);

        // then
        verify(mockFetchURL, times(1)).execute(mock_url, "driving");
    }

    @Test
    public void testRestaurantMarkerOnClick() throws Exception {
        MapsFragment mapsFragment = new MapsFragment();
        RestaurantsFragment mockRestaurantsFragment = mock(RestaurantsFragment.class);
        BeachesFragment mockBeachesFragment = mock(BeachesFragment.class);
        YelpService mockYelpService = mock(YelpService.class);
        DatabaseReference mockDBReference = mock(DatabaseReference.class);
        MainActivity mockMainActivity = mock(MainActivity.class);

        mockMainActivity.restaurantsFragment = mockRestaurantsFragment;
        mockMainActivity.beachesFragment = mockBeachesFragment;

        mapsFragment.databaseReference = mockDBReference;
        mapsFragment.main = mockMainActivity;
        mapsFragment.yelpService = mockYelpService;
        String mock_restaurant_id = "mock_id";
        String mock_url = "mock URL";
        FetchURL mockFetchURL = mock(FetchURL.class);
        PowerMockito.whenNew(FetchURL.class).withAnyArguments().thenReturn(mockFetchURL);

        // when
        mapsFragment.handleRestaurantMarkerClick(mock_restaurant_id, mock_url);

        // then
        verify(mockYelpService, times(1)).executeTask(mockMainActivity, mockRestaurantsFragment, "businesses/mock_id");
        verify(mockFetchURL, times(1)).execute(mock_url, "walking");
    }

    @Test
    public void testGetRouteURL() {
        // given
        MapsFragment mapsFragment = new MapsFragment();
        LatLng testOrigin = new LatLng(1.1, 2.2);
        LatLng testDest = new LatLng(3.3, 4.4);
        String testMode = "test_mode";
        String expected = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=1.1,2.2&destination=3.3,4.4&mode=test_mode&departure_time=now&key=AIzaSyBobTTzoNhhHoQQFa9iY7CPG_kYQrxdRtU";

        // when
        String actual = mapsFragment.getRouteURL(testOrigin, testDest, testMode);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void testDefaultLocation() throws Exception {
        MapsFragment mapsFragment = new MapsFragment();

        assertEquals(34.0522, mapsFragment.currLocLatitude, 0.001);
        assertEquals(-118.2437, mapsFragment.currLocLongitude, 0.001);
    }

    @Test
    public void testParseDuration() {
        // given
        DataParser dataParser = new DataParser();

        try {
            // modeling data from : https://maps.googleapis.com/maps/api/directions/json?departure_time=now&origin=33.902099,-118.358474&destination=34.0224,-118.2851&mode=driving&key=AIzaSyBobTTzoNhhHoQQFa9iY7CPG_kYQrxdRtU
            JSONObject jsonObject = new JSONObject("{\"routes\":[{\"legs\":[{\"duration\":{\"text\":\"24 mins\"}}]}]}");

            String expected = "24 mins";

            // when
            String actual = dataParser.parseDuration(jsonObject);

            // then
            assertEquals(expected, actual);
        }catch (Exception err){
            System.out.println(err);
        }
    }
}
