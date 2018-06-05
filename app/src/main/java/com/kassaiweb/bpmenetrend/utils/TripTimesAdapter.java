package com.kassaiweb.bpmenetrend.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.model.Trip;
import com.kassaiweb.bpmenetrend.model.TripTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TripTimesAdapter extends RecyclerView.Adapter<TripTimesAdapter.TimesViewHolder> {

    private List<TripTime> times = new ArrayList<>();
    private Trip trip = new Trip();
    private OnStopSelected stopSelected;
    private Context context;

    public TripTimesAdapter(Context cont, OnStopSelected onStopSelected) {
        stopSelected = onStopSelected;
        context=cont;
    }


    @Override
    public TimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_element, parent, false);

        return new TimesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimesViewHolder holder, int position) {

        holder.position = position;
        holder.stopId=times.get(position).stopId;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(times.get(position).time * 1000);
        String time = DateFormat.format("HH:mm", cal).toString();

        holder.departure.setText(time);
        holder.predicted.setText("");

        long predicted = 0;
        if (times.get(position).predictedTime != 0 && times.get(position).time != 0) {
            predicted = (times.get(position).predictedTime - times.get(position).time) / 60;
        }

        if (predicted > 0) {
            holder.predicted.setText("+" + predicted + "'");
            holder.predicted.setTextColor(ContextCompat.getColor(context, R.color.late));
        } else if (predicted < 0) {
            holder.predicted.setText(predicted + "'");
            holder.predicted.setTextColor(ContextCompat.getColor(context, R.color.early));
        }


        holder.name.setText(times.get(position).name);

        if ((times.get(position).predictedTime!=0 && Calendar.getInstance().getTimeInMillis()>times.get(position).predictedTime*1000)
                || (times.get(position).predictedTime==0 && Calendar.getInstance().getTimeInMillis()>times.get(position).time*1000)) {
            holder.line.setBackgroundColor(ContextCompat.getColor(context, R.color.passed));
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.passed));
            holder.departure.setTextColor(ContextCompat.getColor(context, R.color.passed));
            holder.predicted.setTextColor(ContextCompat.getColor(context, R.color.passed));
        }else{
        holder.line.setBackgroundColor(Color.parseColor("#" + trip.background));
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.departure.setTextColor(ContextCompat.getColor(context, R.color.black));}

    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public void createTrip(Trip t) {
        trip=t;
    }

    public void add(TripTime element) {
        times.add(element);
        notifyItemInserted(times.size() - 1);
    }

    public void clear() {
        int size = times.size();
        times.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class TimesViewHolder extends RecyclerView.ViewHolder {

        int position;
        TextView departure;
        TextView predicted;
        TextView line;
        TextView name;
        String stopId;

        public TimesViewHolder(View itemView) {
            super(itemView);
            departure = (TextView) itemView.findViewById(R.id.departure);
            predicted = (TextView) itemView.findViewById(R.id.predicted);
            line = (TextView) itemView.findViewById(R.id.line);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stopSelected != null) {
                        stopSelected.onStopSelected(stopId, name.getText().toString());
                    }
                }
            });
        }
    }
}
