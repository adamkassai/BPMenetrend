package com.kassaiweb.bpmenetrend.network;

import com.kassaiweb.bpmenetrend.model.Stops;
import com.kassaiweb.bpmenetrend.model.Times;
import com.kassaiweb.bpmenetrend.model.Trip;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Api {

    @GET("/api/stops.php")
    Call<Stops> getStops(@Query("search") String search);

    @GET("/api/schedule.php")
    Call<Times> getTimes(@Query("id") String id);

    @GET("/api/trip.php")
    Call<Trip> getTrip(@Query("id") String id);
}
