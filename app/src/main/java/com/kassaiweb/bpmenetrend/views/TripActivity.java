package com.kassaiweb.bpmenetrend.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.model.Trip;
import com.kassaiweb.bpmenetrend.model.TripTime;
import com.kassaiweb.bpmenetrend.network.Network;
import com.kassaiweb.bpmenetrend.utils.OnStopSelected;
import com.kassaiweb.bpmenetrend.utils.TripTimesAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripActivity extends AppCompatActivity {
    public static final String ID = "id";
    private String tripId;
    private Trip trip;

    private TripTimesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        tripId = getIntent().getStringExtra(ID);
        getSupportActionBar().setTitle(R.string.tripDetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new TripTimesAdapter(this, new OnStopSelected() {
            @Override
            public void onStopSelected(String id, String name) {
                Intent showDetailsIntent = new Intent();
                showDetailsIntent.setClass(TripActivity.this, ScheduleActivity.class);
                showDetailsIntent.putExtra(ScheduleActivity.ID, id);
                showDetailsIntent.putExtra(ScheduleActivity.NAME, name);
                startActivity(showDetailsIntent);
            }
        });


        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTimes(tripId);
                refreshLayout.setRefreshing(false);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimes(tripId);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.tripRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadTimes(String id) {
        Network.getInstance().getTrip(id).enqueue(new Callback<Trip>() {

            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if (response.isSuccessful()) {
                    adapter.clear();
                    trip = response.body();
                    adapter.createTrip(trip);

                    if (trip.route != null && trip.headsign != null) {
                        getSupportActionBar().setTitle(trip.route + " > " + trip.headsign);
                    }

                    TextView vehicle = (TextView) findViewById(R.id.vehicle);
                    TextView model = (TextView) findViewById(R.id.model);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.tripLinear);

                    if (trip.vehicle != null && trip.model != null) {
                        vehicle.setText(trip.vehicle);
                        model.setText(trip.model);
                    }else{
                        linearLayout.removeView(vehicle);
                        linearLayout.removeView(model);
                    }


                    if (trip.list != null) {
                        for (TripTime element : trip.list) {
                            adapter.add(element);
                        }
                    }

                    initRecyclerView();

                } else {
                    Toast.makeText(TripActivity.this, R.string.responseError, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(TripActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
