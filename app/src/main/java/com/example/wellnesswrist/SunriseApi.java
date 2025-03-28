package com.example.wellnesswrist;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SunriseApi {
    @GET("json?lat=43.4516&lng=-80.4925") // Waterloo, ON
    Call<SunriseResponse> getSunrise();
}