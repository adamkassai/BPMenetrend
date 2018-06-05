package com.kassaiweb.bpmenetrend.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.model.Stop;
import com.kassaiweb.bpmenetrend.model.Stops;
import com.kassaiweb.bpmenetrend.network.Network;
import com.kassaiweb.bpmenetrend.utils.Favourites;
import com.kassaiweb.bpmenetrend.utils.OnStopSelected;
import com.kassaiweb.bpmenetrend.utils.StopsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesFragment extends android.support.v4.app.Fragment {

    private Stops stops;

    private RecyclerView recyclerView;
    private StopsAdapter adapter;
    private LinearLayout welcomeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new StopsAdapter(new OnStopSelected() {
            @Override
            public void onStopSelected(String id, String name) {
                Intent showDetailsIntent = new Intent();
                showDetailsIntent.setClass(getContext(), ScheduleActivity.class);
                showDetailsIntent.putExtra(ScheduleActivity.ID, id);
                showDetailsIntent.putExtra(ScheduleActivity.NAME, name);
                startActivity(showDetailsIntent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadStops(Favourites.getInstance().getFavouritesToAPI());
    }



    private void initRecyclerView() {

        recyclerView = (RecyclerView) getView().findViewById(R.id.favouritesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadStops(String search) {
        Network.getInstance().getStops(search).enqueue(new Callback<Stops>() {

            @Override
            public void onResponse(Call<Stops> call, Response<Stops> response) {
                if (response.isSuccessful()) {
                    adapter.clear();
                    stops = response.body();

                    if (stops.list != null) {
                        for (Stop element : stops.list) {
                            if (element.routes != null) {
                                adapter.add(element);
                            }
                        }
                        welcomeLayout.setVisibility(LinearLayout.GONE);
                        recyclerView.setVisibility(RecyclerView.VISIBLE);
                    }

                    initRecyclerView();

                } else {
                    Toast.makeText(getContext(), R.string.responseError, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Stops> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), R.string.networkError, Toast.LENGTH_SHORT).show();
            }
        });

        if (Favourites.getInstance().count()==0 || stops==null || stops.list==null){
            recyclerView = (RecyclerView) getView().findViewById(R.id.favouritesRecycler);
            recyclerView.setVisibility(RecyclerView.GONE);
            welcomeLayout.setVisibility(LinearLayout.VISIBLE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        welcomeLayout = (LinearLayout) view.findViewById(R.id.welcomeLinear);
        Button gotoSearch = (Button) view.findViewById(R.id.gotoSearch);
        gotoSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.mainViewPager);
                viewPager.setCurrentItem(1);
            }
        });
        return view;
    }

}
