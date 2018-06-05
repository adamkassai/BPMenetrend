package com.kassaiweb.bpmenetrend.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kassaiweb.bpmenetrend.R;
import com.kassaiweb.bpmenetrend.model.Route;
import com.kassaiweb.bpmenetrend.model.Stop;

import java.util.ArrayList;
import java.util.List;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.StopsViewHolder> {

    private List<Stop> stops = new ArrayList<>();
    private OnStopSelected stopSelected;

    public StopsAdapter(OnStopSelected onStopSelected) {
        stopSelected = onStopSelected;
    }


    @Override
    public StopsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_element, parent, false);

        return new StopsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StopsViewHolder holder, int position) {

        holder.position = position;
        holder.stopId = stops.get(position).id;
        holder.stopName.setText(stops.get(position).name);
        holder.routeRows.removeAllViews();

        if (stops.get(holder.getAdapterPosition()).routes != null && stops.get(holder.getAdapterPosition()).routes.size() > 0) {
            for (Route route : stops.get(holder.getAdapterPosition()).routes) {
                View rowItem = holder.inflater.inflate(R.layout.route_row, null);
                TextView routeNumber = (TextView) rowItem.findViewById(R.id.route);
                TextView direction = (TextView) rowItem.findViewById(R.id.direction);

                routeNumber.setText(route.route);
                direction.setText(route.direction);
                routeNumber.setBackgroundColor(Color.parseColor("#" + route.background));
                routeNumber.setTextColor(Color.parseColor("#" + route.color));
                holder.routeRows.addView(rowItem);
            }
        }

    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    public void add(Stop element) {
        stops.add(element);
        notifyItemInserted(stops.size() - 1);
    }

    public void clear() {
        int size = stops.size();
        stops.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class StopsViewHolder extends RecyclerView.ViewHolder {

        int position;
        String stopId;
        TextView stopName;
        LinearLayout routeRows;
        LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public StopsViewHolder(View itemView) {
            super(itemView);
            stopName = (TextView) itemView.findViewById(R.id.stopName);
            routeRows = (LinearLayout) itemView.findViewById(R.id.routeRows);
            inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stopSelected != null) {
                        stopSelected.onStopSelected(stopId, stopName.getText().toString());
                    }
                }
            });
        }
    }
}
