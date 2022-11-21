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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.socalbeach4life.data.model.Beach;
import com.example.socalbeach4life.data.model.Restaurant;
import com.example.socalbeach4life.fragments.BeachesFragment;
import com.example.socalbeach4life.fragments.MapsFragment;
import com.example.socalbeach4life.fragments.RestaurantsFragment;
import com.example.socalbeach4life.yelp.YelpService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.Distribution;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestaurantsFragment.class)
public class RestaurantsFragmentUnitTests {

    @Test
    public void testPlaceRestaurantMarkersInvocations() throws Exception {
        // given
        RestaurantsFragment restaurantsFragment = new RestaurantsFragment();
        MainActivity mockMainActivity = mock(MainActivity.class);
        MapsFragment mockMapsFragment = mock(MapsFragment.class);
        mockMapsFragment.markerArray = mock(ArrayList.class);
        GoogleMap mockGoogleMap = mock(GoogleMap.class);
        mockMapsFragment.googleMap = mockGoogleMap;
        mockMainActivity.mapsFragment = mockMapsFragment;
        mockMainActivity.currentBeach = new Beach("mock_beach_id", "mock_beach_name", new LatLng(1.1, 2.2), 1.0);
        String mockOutput = "{\"businesses\": [{\"id\": \"mockID\", " +
                "\"name\": \"mockName\", " +
                "\"coordinates\": {\"latitude\": 1.1, \"longitude\": 2.2}, " +
                "\"distance\": 1609}]}";
        JsonObject convertedObject = new Gson().fromJson(mockOutput, JsonObject.class);
        JsonArray restaurants = (JsonArray) convertedObject.get("businesses");

        restaurantsFragment.main = mockMainActivity;

        // when
        restaurantsFragment.placeRestaurantMarkers(restaurants);

        // then
        verify(mockMapsFragment, times(1)).setMarker(1.1, 2.2, "mockName", "Restaurant mockID");
    }

    @Test
    public void testProcessFinishedSearchByIDInvocations() throws Exception {
        // given
        RestaurantsFragment restaurantsFragment = new RestaurantsFragment();
        YelpService mockYelpService = mock(YelpService.class);
        ScrollView mockScrollView = mock(ScrollView.class);
        MainActivity mockMainActivity = mock(MainActivity.class);
        BeachesFragment mockBeachesFragment = mock(BeachesFragment.class);
        mockBeachesFragment.beachesScrollView = mockScrollView;
        MapsFragment mockMapsFragment = mock(MapsFragment.class);
        mockMapsFragment.markerArray = mock(ArrayList.class);
        GoogleMap mockGoogleMap = mock(GoogleMap.class);
        mockMapsFragment.googleMap = mockGoogleMap;
        mockMainActivity.mapsFragment = mockMapsFragment;
        mockMainActivity.beachesFragment = mockBeachesFragment;
        mockMainActivity.restaurantsFragment = restaurantsFragment;
        String mockOutput = "businesses/mock_id|{\"id\": \"mock_id\", " +
                "\"coordinates\": {\"latitude\": 1.1, \"longitude\": 2.2}, " +
                "\"name\": \"mock_name\", " +
                "\"location\": {\"city\": \"mock_city\", \"display_address\": [\"mock_display_address\"]}, " +
                "\"hours\": [{\"open\": [{\"is_overnight\": false, \"start\": \"0900\", \"end\": \"1700\", \"day\": 0}]}]}";

        restaurantsFragment.main = mockMainActivity;
        restaurantsFragment.parentView = mock(FrameLayout.class);
        restaurantsFragment.yelpService = mockYelpService;

        Button mockButton = mock(Button.class);
        PowerMockito.whenNew(Button.class).withAnyArguments().thenReturn(mockButton);
        LinearLayout mockLinearLayout = mock(LinearLayout.class);
        PowerMockito.whenNew(LinearLayout.class).withAnyArguments().thenReturn(mockLinearLayout);

        // when
        restaurantsFragment.processFinish(mockMainActivity, mockOutput);

        // then
        verify(mockButton, times(1)).setOnClickListener(any());
        verify(mockYelpService, times(1)).executeTask(mockMainActivity, restaurantsFragment,
                "businesses/mock_id");
    }

    @Test
    public void testParseRestaurantHours () {
        // given
        String test1 = "{\"is_overnight\": false, \"start\": \"0800\", \"end\": \"0900\", \"day\": 0}";
        JsonObject test1Json =  new Gson().fromJson(test1, JsonObject.class);
        String test1ExpectedStart = "8:00 AM";
        String test1ExpectedEnd = "9:00 AM";

        String test2 = "{\"is_overnight\": false, \"start\": \"1000\", \"end\": \"1100\", \"day\": 0}";
        JsonObject test2Json =  new Gson().fromJson(test2, JsonObject.class);
        String test2ExpectedStart = "10:00 AM";
        String test2ExpectedEnd = "11:00 AM";

        String test3 = "{\"is_overnight\": false, \"start\": \"1500\", \"end\": \"1600\", \"day\": 0}";
        JsonObject test3Json =  new Gson().fromJson(test3, JsonObject.class);
        String test3ExpectedStart = "3:00 PM";
        String test3ExpectedEnd = "4:00 PM";

        String test4 = "{\"is_overnight\": false, \"start\": \"2200\", \"end\": \"2300\", \"day\": 0}";
        JsonObject test4Json =  new Gson().fromJson(test4, JsonObject.class);
        String test4ExpectedStart = "10:00 PM";
        String test4ExpectedEnd = "11:00 PM";

        // when
        RestaurantsFragment restaurantsFragment = new RestaurantsFragment();
        ArrayList<String> test1Actual = restaurantsFragment.parseRestaurantHours(test1Json);
        ArrayList<String> test2Actual = restaurantsFragment.parseRestaurantHours(test2Json);
        ArrayList<String> test3Actual = restaurantsFragment.parseRestaurantHours(test3Json);
        ArrayList<String> test4Actual = restaurantsFragment.parseRestaurantHours(test4Json);

        assertEquals(test1ExpectedStart, test1Actual.get(0));
        assertEquals(test1ExpectedEnd, test1Actual.get(1));
        assertEquals(test2ExpectedStart, test2Actual.get(0));
        assertEquals(test2ExpectedEnd, test2Actual.get(1));
        assertEquals(test3ExpectedStart, test3Actual.get(0));
        assertEquals(test3ExpectedEnd, test3Actual.get(1));
        assertEquals(test4ExpectedStart, test4Actual.get(0));
        assertEquals(test4ExpectedEnd, test4Actual.get(1));
    }

    @Test
    public void testParseBeaches() {
        // given
        String testID = "mock ID";
        String testName = "mock name";
        JsonObject testCoordinates = new JsonObject();
        Double testLatitude = 1.1;
        Double testLongitude = 2.2;
        testCoordinates.addProperty("latitude", testLatitude);
        testCoordinates.addProperty("longitude", testLongitude);

        JsonObject testRestaurantObject = new JsonObject();
        testRestaurantObject.addProperty("id", testID);
        testRestaurantObject.addProperty("name", testName);
        testRestaurantObject.add("coordinates", testCoordinates);

        String expectedID = "mock ID";
        String expectedName = "mock name";
        Double expectedLat = 1.1;
        Double expectedLong = 2.2;

        // when
        Restaurant actualRestaurant = RestaurantsFragment.parseRestaurant(testRestaurantObject);

        // then
        assertEquals(expectedID, actualRestaurant.id);
        assertEquals(expectedName, actualRestaurant.name);
        assertEquals(expectedLat, (Double) actualRestaurant.coordinates.latitude);
        assertEquals(expectedLong, (Double) actualRestaurant.coordinates.longitude);
    }
}