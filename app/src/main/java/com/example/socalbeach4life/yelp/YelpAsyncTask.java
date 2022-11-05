package com.example.socalbeach4life.yelp;

import android.os.AsyncTask;

import com.example.socalbeach4life.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YelpAsyncTask extends AsyncTask<Object, Object, ArrayList<Object>> {
    public YelpAsyncResponse delegate = null;

    @Override
    protected ArrayList<Object> doInBackground(Object... objects) {
        MainActivity main = (MainActivity) objects[0];
        String[] strings = (String[]) objects[1];
        String BASE_URL = "https://api.yelp.com/v3/";
        String API_KEY = "hGQVfT2R3h6Ww_y-djRk-G431FSipINxuilnFFBcwFmbJsO2azyVCi2ZEjRUYL4gSi4_D-PdXg6OqVKiotXxCyPWphPDN3D9dfi5cp1EwGEu2KF5XqfhXMSTcpBZY3Yx";
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String endpoint = strings[0]; // i.e. businesses/search
        String builder = BASE_URL + endpoint + "?";
        for(int i = 1; i < strings.length - 1; i += 2) {
            String key = strings[i];
            String value = strings[i + 1];
            value = value.replace(" ", "%20");
            if (i > 1) builder += "&";
            builder += key + "=" + value;
        }
        Request request = new Request.Builder().url(builder).method("GET", null).addHeader(
                        "Authorization",
                        "Bearer " + API_KEY)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String responseString = null;
        try {
            responseString = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        System.out.println(responseString);
        if(responseString.contains("error")){
            System.out.println("Yelp API Failure.");
            return null;
        }
        ArrayList<Object> resObjects = new ArrayList<>();
        resObjects.add(main);
        resObjects.add(endpoint + "|" + responseString);
        return resObjects;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Object> objects) {
        //https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
        delegate.processFinish((MainActivity) objects.get(0), (String) objects.get(1));
    }
}

