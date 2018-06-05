package com.kassaiweb.bpmenetrend.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.model.Stop;
import com.kassaiweb.bpmenetrend.model.Stops;
import com.kassaiweb.bpmenetrend.network.Network;
import com.kassaiweb.bpmenetrend.utils.OnStopSelected;
import com.kassaiweb.bpmenetrend.utils.StopsAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends android.support.v4.app.Fragment {

    private EditText searchField;
    private Button saveButton;
    private Stops stops;

    private StopsAdapter adapter;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && adapter.getItemCount()==0) {
            searchField.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
        }

    }

    private void initRecyclerView() {

        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.searchRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadStops(String search) {
        Network.getInstance().getStops(search).enqueue(new Callback<Stops>() {

            @Override
            public void onResponse(Call<Stops> call, Response<Stops> response) {
                 if (response.isSuccessful()) {
                     adapter.clear();
                     stops=response.body();

                     if (stops.list!=null) {
                         for (Stop element : stops.list) {
                             if (element.routes != null) {
                                 adapter.add(element);
                             }
                         }
                     }else{
                         Toast.makeText(getContext(), R.string.notFound, Toast.LENGTH_SHORT).show();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchField = (EditText) view.findViewById(R.id.searchField);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        searchField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveButton.performClick();
                    return true;
                }
                return false;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (searchField.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.emptySearch, Toast.LENGTH_SHORT).show();
                }else if (searchField.getText().toString().length()<3)
                {
                    Toast.makeText(getContext(), R.string.tooShort, Toast.LENGTH_SHORT).show();
                }else {
                   saveButton.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    saveButton.setEnabled(false);
                    saveButton.setText(R.string.loading);
                    loadStops(searchField.getText().toString());
                    saveButton.setText(R.string.submit);
                    saveButton.setEnabled(true);
                }
            }
        });

        return view;
    }
}
