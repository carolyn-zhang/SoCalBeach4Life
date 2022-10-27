package com.example.socalbeach4life.yelp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface YelpService {

    @GET("businesses/search")
    Call<Object> search(
            @Header("Authorization") String authHeader,
            @Query("term") String searchTerm,
            @Query("location") String location/*,
            @Query("latitude") Double latitude,
            @Query("longitude") Double longitude,
            @Query("radius") Integer radius,
            @Query("sort_by") String sortBy*/
    );

    @GET("businesses/{id}")
    Call<Object> getResultInfo(@Path("id") String search);

}
