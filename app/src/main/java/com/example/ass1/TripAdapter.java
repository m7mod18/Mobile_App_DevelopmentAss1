package com.example.ass1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
// Created by Mahmoud Kafafi 1221974

    // Simple click interface to send trip + position back to the activity
    public interface OnTripClickListener {
        void onTripClick(Trip trip, int position);
    }

    private List<Trip> trip_list;
    private OnTripClickListener listener;

    public TripAdapter(List<Trip> trip_list, OnTripClickListener listener) {
        this.trip_list = trip_list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip t = trip_list.get(position);

        holder.title_text.setText(t.getTitle());
        holder.date_text.setText(t.getDate());

        // Show only first place as a small preview
        String places = t.getPlaces();
        if (places != null && !places.isEmpty()) {
            String preview = places.split("\n")[0];
            holder.places_text.setText(preview);
        } else {
            holder.places_text.setText("No places added");
        }
    }

    @Override
    public int getItemCount() {
        return trip_list.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        TextView title_text;
        TextView date_text;
        TextView places_text;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind row views
            title_text  = itemView.findViewById(R.id.tvTripTitleRow);
            date_text   = itemView.findViewById(R.id.tvTripDateRow);
            places_text = itemView.findViewById(R.id.tvTripPlacesRow);

            // Handle click on the whole row
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onTripClick(trip_list.get(pos), pos);
                }
            });
        }
    }
}
