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
import com.kassaiweb.bpmenetrend.model.StopTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.TimesViewHolder> {

    private List<StopTime> times = new ArrayList<>();
    private OnStopSelected stopSelected;
    private Context context;

    public TimesAdapter(Context cont, OnStopSelected onStopSelected) {
        stopSelected = onStopSelected;
        context=cont;
    }


    @Override
    public TimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_element, parent, false);

        return new TimesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimesViewHolder holder, int position) {

        holder.position = position;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(times.get(position).time * 1000);
        String time = DateFormat.format("HH:mm", cal).toString();

        holder.departure.setText(time);
        holder.tripId=times.get(position).tripId;
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


        holder.route.setText(times.get(position).route);
        holder.direction.setText(times.get(position).direction);
        holder.route.setBackgroundColor(Color.parseColor("#" + times.get(position).background));
        holder.route.setTextColor(Color.parseColor("#" + times.get(position).color));

    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    public void add(StopTime element) {
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
        String tripId;
        TextView departure;
        TextView predicted;
        TextView route;
        TextView direction;

        public TimesViewHolder(View itemView) {
            super(itemView);
            departure = (TextView) itemView.findViewById(R.id.departure);
            predicted = (TextView) itemView.findViewById(R.id.predicted);
            route = (TextView) itemView.findViewById(R.id.timeRoute);
            direction = (TextView) itemView.findViewById(R.id.timeDirection);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stopSelected != null) {
                        stopSelected.onStopSelected(tripId, "");
                    }
                }
            });
        }
    }
}
