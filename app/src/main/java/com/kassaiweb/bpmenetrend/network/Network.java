package com.kassaiweb.bpmenetrend.network;

import com.kassaiweb.bpmenetrend.model.Stops;
import com.kassaiweb.bpmenetrend.model.Times;
import com.kassaiweb.bpmenetrend.model.Trip;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Network {

    private static Network instance;

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    private Api api;

    private Network() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://kassaiweb.com").client(new OkHttpClient.Builder().build()).addConverterFactory(GsonConverterFactory.create()).build();
        api = retrofit.create(Api.class);
    }


    public Call<Stops> getStops(String search) {
        return api.getStops(search);
    }

    public Call<Times> getTimes(String id) {
        return api.getTimes(id);
    }

    public Call<Trip> getTrip(String id) {
        return api.getTrip(id);
    }

}
