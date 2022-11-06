package com.example.socalbeach4life.yelp;

import android.os.AsyncTask;

import com.example.socalbeach4life.MainActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;


public class YelpService {
    public void executeTask (MainActivity main, YelpAsyncResponse delegate, String... strings) {
        YelpAsyncTask yelpAsyncTask = new YelpAsyncTask();
        yelpAsyncTask.delegate = delegate;
        yelpAsyncTask.execute(main, strings);
    }
}
