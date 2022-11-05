package com.example.socalbeach4life.maps;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, PointsParser.ParsedResults> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    final class ParsedResults {
        private final String duration;
        private final List<List<HashMap<String, String>>> routes;

        public ParsedResults(String duration, List<List<HashMap<String, String>>> routes) {
            this.duration = duration;
            this.routes = routes;
        }

        public String getDuration() {
            return duration;
        }

        public List<List<HashMap<String, String>>> getRoutes() {
            return routes;
        }
    }

    // Parsing the data in non-ui thread
    @Override
    protected ParsedResults doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        String duration = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("mylog", "Executing routes");
            Log.d("mylog", routes.toString());
            duration = parser.parseDuration(jObject);

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }

        return new ParsedResults(duration, routes);
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(ParsedResults results) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        String duration = results.getDuration();
        List<List<HashMap<String, String>>> routes = results.getRoutes();
        // Traversing through all the routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = routes.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            } else {
                lineOptions.width(20);
                lineOptions.color(Color.BLUE);
            }
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(duration, lineOptions);
        } else {
            Log.d("mylog", "without Polylines drawn");
        }
    }
}
