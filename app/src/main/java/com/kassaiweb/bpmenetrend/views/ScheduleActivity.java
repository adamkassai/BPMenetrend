package com.kassaiweb.bpmenetrend.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.model.StopTime;
import com.kassaiweb.bpmenetrend.model.Times;
import com.kassaiweb.bpmenetrend.network.Network;
import com.kassaiweb.bpmenetrend.utils.Favourites;
import com.kassaiweb.bpmenetrend.utils.OnStopSelected;
import com.kassaiweb.bpmenetrend.utils.TimesAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleActivity extends AppCompatActivity {
    public static final String ID = "id";
    public static final String NAME = "name";
    private String stopId;
    private Times schedule;

    private RecyclerView recyclerView;
    private TimesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        stopId = getIntent().getStringExtra(ID);
        String stopName = getIntent().getStringExtra(NAME);
        getSupportActionBar().setTitle(stopName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new TimesAdapter(this, new OnStopSelected() {


            @Override
            public void onStopSelected(String id, String name) {
                Intent showDetailsIntent = new Intent();
                showDetailsIntent.setClass(ScheduleActivity.this, TripActivity.class);
                showDetailsIntent.putExtra(ScheduleActivity.ID, id);
                startActivity(showDetailsIntent);
            }


        });


        final CheckBox isFavourite = (CheckBox) findViewById(R.id.favouriteCheckbox);

        if (Favourites.getInstance().contains(stopId)) {
            isFavourite.setChecked(true);
        }

        isFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isFavourite.isChecked()) {
                    Favourites.getInstance().add(stopId);
                    Toast.makeText(ScheduleActivity.this, R.string.saveSuccess, Toast.LENGTH_SHORT).show();
                } else {
                    Favourites.getInstance().remove(stopId);
                    Toast.makeText(ScheduleActivity.this, R.string.delSuccess, Toast.LENGTH_SHORT).show();
                }

            }

        });

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTimes(stopId);
                refreshLayout.setRefreshing(false);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView = (RecyclerView) findViewById(R.id.timesRecycler);
        loadTimes(stopId);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadTimes(String id) {
        Network.getInstance().getTimes(id).enqueue(new Callback<Times>() {

            @Override
            public void onResponse(Call<Times> call, Response<Times> response) {
                if (response.isSuccessful()) {
                    adapter.clear();
                    schedule = response.body();

                    if (schedule.list != null) {
                        for (StopTime element : schedule.list) {
                            adapter.add(element);
                        }
                    }else{
                        Toast.makeText(ScheduleActivity.this, R.string.noTrip, Toast.LENGTH_LONG).show();
                    }

                    initRecyclerView();

                } else {
                    Toast.makeText(ScheduleActivity.this, R.string.responseError, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Times> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ScheduleActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("favourites", Favourites.getInstance().getFavouritesToAPI());
        editor.apply();

    }

}
