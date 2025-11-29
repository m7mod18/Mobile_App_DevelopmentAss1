package com.example.ass1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Screen that shows full details of one trip
public class TripDetailsActivity extends AppCompatActivity {
// Created by Mahmoud Kafafi 1221974

    // Intent keys used between activities
    public static final String EXTRA_TRIP   = "extra_trip";
    public static final String EXTRA_INDEX  = "extra_index";
    public static final String EXTRA_DELETE = "extra_delete";
    private static final int REQUEST_EDIT_TRIP = 300;
    private TextView title_text;
    private TextView date_text;
    private TextView places_text;
    private TextView type_text;
    private TextView important_text;
    private TextView needHotel_text;
    private Button back_button;
    private Button edit_button;
    private Button delete_button;

    private Trip trip;
    private int trip_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        title_text      = findViewById(R.id.tvDetailTitle);
        date_text       = findViewById(R.id.tvDetailDate);
        places_text     = findViewById(R.id.tvDetailPlaces);
        type_text       = findViewById(R.id.tvDetailType);
        important_text  = findViewById(R.id.tvDetailImportant);
        needHotel_text  = findViewById(R.id.tvDetailNeedHotel);
        back_button   = findViewById(R.id.btnBackToMain);
        edit_button   = findViewById(R.id.btnEditTrip);
        delete_button = findViewById(R.id.btnDeleteTrip);

        trip = (Trip) getIntent().getSerializableExtra(EXTRA_TRIP);
        trip_index = getIntent().getIntExtra(EXTRA_INDEX, -1);

        bindTripToViews();

        back_button.setOnClickListener(v -> finish());

        // Open edit form for this trip
        edit_button.setOnClickListener(v -> {
            if (trip == null || trip_index < 0) return;

            Intent i = new Intent(TripDetailsActivity.this, TripManagementActivity.class);
            i.putExtra(TripManagementActivity.EXTRA_MODE, TripManagementActivity.MODE_EDIT);
            i.putExtra(TripManagementActivity.EXTRA_TRIP, trip);
            i.putExtra(TripManagementActivity.EXTRA_INDEX, trip_index);
            startActivityForResult(i, REQUEST_EDIT_TRIP);
        });

        // Delete this trip and send result back to main
        delete_button.setOnClickListener(v -> {
            if (trip_index < 0) {
                finish();
                return;
            }
            Intent result = new Intent();
            result.putExtra(TripManagementActivity.EXTRA_INDEX, trip_index);
            result.putExtra(EXTRA_DELETE, true);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    private void bindTripToViews() {
        if (trip == null) return;

        title_text.setText(trip.getTitle());
        date_text.setText("Date: " + trip.getDate());

        String places = trip.getPlaces();
        if (places == null || places.isEmpty()) {
            places_text.setText("No places added.");
        } else {
            places_text.setText(places);
        }

        String type = trip.getType() == null ? "Other" : trip.getType();
        type_text.setText("Type: " + type);

        String important = trip.isImportant() ? "Yes" : "No";
        important_text.setText("Important: " + important);

        String needHotel = trip.isNeedHotel() ? "Yes" : "No";
        needHotel_text.setText("Need hotel: " + needHotel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_TRIP && resultCode == RESULT_OK && data != null) {
            Trip updated_trip = (Trip) data.getSerializableExtra(TripManagementActivity.EXTRA_TRIP);
            int index = data.getIntExtra(TripManagementActivity.EXTRA_INDEX, -1);

            if (updated_trip != null && index >= 0) {
                trip = updated_trip;
                trip_index = index;
                bindTripToViews();

                Intent result = new Intent();
                result.putExtra(TripManagementActivity.EXTRA_TRIP, trip);
                result.putExtra(TripManagementActivity.EXTRA_INDEX, trip_index);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }
}
