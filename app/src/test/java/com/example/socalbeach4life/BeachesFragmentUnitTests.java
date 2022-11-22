package com.example.socalbeach4life;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Text;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.socalbeach4life.data.model.Beach;
import com.example.socalbeach4life.fragments.BeachesFragment;
import com.example.socalbeach4life.fragments.MapsFragment;
import com.example.socalbeach4life.fragments.RestaurantsFragment;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonObject;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BeachesFragment.class)
public class BeachesFragmentUnitTests {
    @Test
    public void testInitInvocations() throws Exception {
        // given
        BeachesFragment beachesFragment = new BeachesFragment();
        YelpService mockYelpService = mock(YelpService.class);
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        MainActivity mockMainActivity = mock(MainActivity.class);
        ScrollView mockScrollView = mock(ScrollView.class);
        beachesFragment.yelpService = mockYelpService;
        beachesFragment.beachesScrollView = mockScrollView;

        PowerMockito.whenNew(LinearLayout.class).withAnyArguments().thenReturn(mockLinearLayout);

        // when
        beachesFragment.init(mockMainActivity);

        // then
        verify(mockYelpService, times(1)).executeTask(mockMainActivity, beachesFragment, "businesses/search", "term", "beach", "location", "Los Angeles", "sort_by", "distance");
    }

    @Test
    public void testProcessFinishedSearchInvocations() throws Exception {
        // given
        BeachesFragment beachesFragment = new BeachesFragment();
        DatabaseReference mockDBReference = mock(DatabaseReference.class);
        YelpService mockYelpService = mock(YelpService.class);
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        ScrollView mockScrollView = mock(ScrollView.class);
        TextView mockTextView = mock(TextView.class);
        MainActivity mockMainActivity = mock(MainActivity.class);
        MapsFragment mockMapsFragment = mock(MapsFragment.class);
        mockMainActivity.mapsFragment = mockMapsFragment;
        String mockOutput = "businesses/search|{\"businesses\": [{\"id\": \"mockID\", " +
                "\"name\": \"mockName\", " +
                "\"coordinates\": {\"latitude\": 1.1, \"longitude\": 2.2}, " +
                "\"distance\": 1609}]}";

        beachesFragment.databaseReference = mockDBReference;
        beachesFragment.yelpService = mockYelpService;
        beachesFragment.beachListlayout = mockLinearLayout;
        beachesFragment.beachInfolayout = mockLinearLayout;
        beachesFragment.beachesScrollView = mockScrollView;
        beachesFragment.globalNumReviewsTV = mockTextView;
        beachesFragment.globalAverageScoreTV = mockTextView;

        LinearLayout mockLineLayout = mock(LinearLayout.class);
        PowerMockito.whenNew(LinearLayout.class).withAnyArguments().thenReturn(mockLineLayout);

        // when
        beachesFragment.processFinish(mockMainActivity, mockOutput);

        // then
        verify(mockLineLayout, times(1)).setOnClickListener(any());
        verify(mockMapsFragment, times(1)).setMarker(1.1, 2.2, "mockName", "Beach mockID");
    }

    @Test
    public void testProcessFinishedSearchByIDInvocations() throws Exception {
        // given
        BeachesFragment beachesFragment = new BeachesFragment();
        DatabaseReference mockDBReference = mock(DatabaseReference.class);
        YelpService mockYelpService = mock(YelpService.class);
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        ScrollView mockScrollView = mock(ScrollView.class);
        TextView mockTextView = mock(TextView.class);
        MainActivity mockMainActivity = mock(MainActivity.class);
        RestaurantsFragment mockRestaurantsFragment = mock(RestaurantsFragment.class);
        MapsFragment mockMapsFragment = mock(MapsFragment.class);
        mockMapsFragment.markerArray = mock(ArrayList.class);
        GoogleMap mockGoogleMap = mock(GoogleMap.class);
        mockMapsFragment.googleMap = mockGoogleMap;
        mockMainActivity.mapsFragment = mockMapsFragment;
        mockMainActivity.restaurantsFragment = mockRestaurantsFragment;
        String mockOutput = "businesses/mock_id|{\"id\": \"mock_id\", " +
                "\"coordinates\": {\"latitude\": 1.1, \"longitude\": 2.2}, " +
                "\"name\": \"mock_name\", " +
                "\"location\": {\"city\": \"mock_city\", \"display_address\": [\"mock_display_address\"]}, " +
                "\"hours\": [{\"open\": [{\"is_overnight\": false, \"start\": \"0900\", \"end\": \"1700\", \"day\": 0}]}]}";

        beachesFragment.databaseReference = mockDBReference;
        beachesFragment.yelpService = mockYelpService;
        beachesFragment.beachListlayout = mockLinearLayout;
        beachesFragment.beachInfolayout = mockLinearLayout;
        beachesFragment.beachesScrollView = mockScrollView;
        beachesFragment.globalNumReviewsTV = mockTextView;
        beachesFragment.globalAverageScoreTV = mockTextView;
        beachesFragment.setMarker = false;

        Button mockButton = mock(Button.class);
        PowerMockito.whenNew(Button.class).withAnyArguments().thenReturn(mockButton);
        PowerMockito.when(mockDBReference.child(anyString())).thenReturn(mock(DatabaseReference.class));

        // when
        beachesFragment.processFinish(mockMainActivity, mockOutput);

        // then
        verify(mockButton, times(5)).setOnClickListener(any());
        verify(mockYelpService, times(1)).executeTask(mockMainActivity, mockRestaurantsFragment,
                "businesses/search",
                "term", "best restaurants", "latitude", String.valueOf(1.1),
                "longitude", String.valueOf(2.2),
                "radius", String.valueOf(0), "limit", "10", "sort_by", "best_match");
    }

    @Test
    public void testParseBeaches() {
        // given
        String testID = "mock ID";
        String testName = "mock name";
        JsonObject testCoordinates = new JsonObject();
        Double testLatitude = 1.1;
        Double testLongitude = 2.2;
        Double distance = 1609.0;
        testCoordinates.addProperty("latitude", testLatitude);
        testCoordinates.addProperty("longitude", testLongitude);

        JsonObject testBeachObject = new JsonObject();
        testBeachObject.addProperty("id", testID);
        testBeachObject.addProperty("name", testName);
        testBeachObject.add("coordinates", testCoordinates);
        testBeachObject.addProperty("distance", distance);

        String expectedID = "mock ID";
        String expectedName = "mock name";
        Double expectedLat = 1.1;
        Double expectedLong = 2.2;
        Double expectedDistanceInMiles = 1.0;

        // when
        Beach actualBeach = BeachesFragment.parseBeach(testBeachObject);

        // then
        assertEquals(expectedID, actualBeach.id);
        assertEquals(expectedName, actualBeach.name);
        assertEquals(expectedLat, (Double) actualBeach.coordinates.latitude);
        assertEquals(expectedLong, (Double) actualBeach.coordinates.longitude);
        assertEquals(expectedDistanceInMiles, actualBeach.distanceInMiles);
    }
}