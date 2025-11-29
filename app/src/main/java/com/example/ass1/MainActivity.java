package com.example.ass1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
// Created by Mahmoud Kafafi 1221974



    // Request codes for starting activities
    private static final int REQUEST_ADD_TRIP     = 100;
    private static final int REQUEST_TRIP_DETAILS = 200;

    // Simple keys for SharedPreferences
    private static final String PREFS_NAME = "trips_prefs";
    private static final String KEY_TRIPS  = "key_trips";

    private TextView app_title;
    private ImageView trip_image;
    private EditText search_trips;
    private RecyclerView trips_list;
    private Button add_trip_button;
    private Button search_button;
    private Button clear_button;

    // List that is shown in RecyclerView
    private ArrayList<Trip> tripList = new ArrayList<>();

    private ArrayList<Trip> originalTripList = new ArrayList<>();
    private TripAdapter tripAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app_title       = findViewById(R.id.tvAppTitle);
        trip_image      = findViewById(R.id.ivTripImage);
        search_trips    = findViewById(R.id.etSearchTrips);
        trips_list      = findViewById(R.id.rvTripsList);
        add_trip_button = findViewById(R.id.btnAddTrip);
        search_button   = findViewById(R.id.btnSearchTrips);
        clear_button    = findViewById(R.id.btnClearSearch);

        // Setup the RecyclerView we used in the app
        trips_list.setLayoutManager(new LinearLayoutManager(this));
        tripAdapter = new TripAdapter(tripList, (trip, position) -> {
            Intent i = new Intent(MainActivity.this, TripDetailsActivity.class);
            i.putExtra(TripDetailsActivity.EXTRA_TRIP, trip);
            i.putExtra(TripDetailsActivity.EXTRA_INDEX, position);
            startActivityForResult(i, REQUEST_TRIP_DETAILS);
        });
        trips_list.setAdapter(tripAdapter);

        loadTripsFromPrefs();

        // First run: add some dummy trips just to see يعني هاي بس هيك بقدر يحذفها عشان يشوف شكل البرنامج او كيف بنعرضو
        if (tripList.isEmpty()) {
            loadDummyTrips();
            saveTripsToPrefs();
        }

        syncOriginalWithCurrent();
        tripAdapter.notifyDataSetChanged();

        add_trip_button.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, TripManagementActivity.class);
            i.putExtra(TripManagementActivity.EXTRA_MODE, TripManagementActivity.MODE_ADD);
            startActivityForResult(i, REQUEST_ADD_TRIP);
        });

        search_button.setOnClickListener(v -> {
            String query = search_trips.getText().toString().trim();
            filterTrips(query);
        });

        clear_button.setOnClickListener(v -> {
            search_trips.setText("");
            filterTrips("");
        });
    }

    private void syncOriginalWithCurrent() {
        originalTripList.clear();
        originalTripList.addAll(tripList);
    }

    // Filter trips by title " search method "
    private void filterTrips(String query) {
        tripList.clear();

        if (query.isEmpty()) {
            tripList.addAll(originalTripList);
            tripAdapter.notifyDataSetChanged();
            return;
        }

        for (Trip t : originalTripList) {
            if (t.getTitle() != null &&
                    t.getTitle().toLowerCase().contains(query.toLowerCase())) {
                tripList.add(t);
            }
        }

        if (tripList.isEmpty()) {
            Toast.makeText(this, "No trip found", Toast.LENGTH_SHORT).show();
        }

        tripAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result from add trip screen
        if (requestCode == REQUEST_ADD_TRIP && resultCode == RESULT_OK && data != null) {
            Trip newTrip = (Trip) data.getSerializableExtra(TripManagementActivity.EXTRA_TRIP);
            if (newTrip != null) {
                tripList.add(newTrip);
                syncOriginalWithCurrent();
                tripAdapter.notifyItemInserted(tripList.size() - 1);
                saveTripsToPrefs();
            }
        }
        // Result from details screen (edit / delete)
        else if (requestCode == REQUEST_TRIP_DETAILS && resultCode == RESULT_OK && data != null) {

            boolean isDelete = data.getBooleanExtra(TripDetailsActivity.EXTRA_DELETE, false);
            int index = data.getIntExtra(TripManagementActivity.EXTRA_INDEX, -1);

            if (isDelete && index >= 0 && index < tripList.size()) {
                tripList.remove(index);
                syncOriginalWithCurrent();
                tripAdapter.notifyItemRemoved(index);
                saveTripsToPrefs();
            }
            else {
                Trip updatedTrip = (Trip) data.getSerializableExtra(TripManagementActivity.EXTRA_TRIP);
                if (updatedTrip != null && index >= 0 && index < tripList.size()) {

                    tripList.set(index, updatedTrip);
                    syncOriginalWithCurrent();
                    tripAdapter.notifyItemChanged(index);
                    saveTripsToPrefs();
                }
            }
        }
    }

    // Dummy data used only the first time زي محكينا فوق بس عشان الشكل
    private void loadDummyTrips() {
        tripList.clear();

        Trip t1 = new Trip(
                "Paris Trip",
                "10-12-2025",
                "Eiffel Tower\nLouvre Museum\nSeine River"
        );

        Trip t2 = new Trip(
                "Istanbul Weekend",
                "05-01-2026",
                "Hagia Sophia\nGrand Bazaar\nGalata Tower"
        );

        Trip t3 = new Trip(
                "Dead Sea Relax",
                "20-02-2026",
                "Hotel Spa\nDead Sea Beach"
        );

        tripList.add(t1);
        tripList.add(t2);
        tripList.add(t3);
    }

    // Save trips as one big string in SharedPreferences
    private void saveTripsToPrefs() {
        StringBuilder sb = new StringBuilder();

        for (Trip t : originalTripList) {
            String title     = t.getTitle()    == null ? "" : t.getTitle().replace("|", " ");
            String date      = t.getDate()     == null ? "" : t.getDate().replace("|", " ");
            String places    = t.getPlaces()   == null ? "" : t.getPlaces().replace("|", " ");
            String type      = t.getType()     == null ? "" : t.getType().replace("|", " ");
            String important = t.isImportant() ? "1" : "0";
            String needHotel = t.isNeedHotel() ? "1" : "0";

            // title|date|places|type|important|needHotel
            sb.append(title)
                    .append("|")
                    .append(date)
                    .append("|")
                    .append(places.replace("\n", "\\n"))
                    .append("|")
                    .append(type)
                    .append("|")
                    .append(important)
                    .append("|")
                    .append(needHotel)
                    .append("\n");
        }

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_TRIPS, sb.toString())
                .apply();
    }

    // Load trips back from SharedPreferences
    private void loadTripsFromPrefs() {
        tripList.clear();
        originalTripList.clear();

        String saved = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_TRIPS, "");

        if (saved == null || saved.isEmpty()) {
            return;
        }

        String[] lines = saved.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split("\\|");

            // Expected: title|date|places|type|important|needHotel
            if (parts.length >= 3) {
                String title  = parts[0];
                String date   = parts[1];
                String places = parts[2].replace("\\n", "\n");

                String type = "Other";
                boolean important = false;
                boolean needHotel = false;

                if (parts.length >= 4) {
                    type = parts[3];
                }
                if (parts.length >= 5) {
                    important = "1".equals(parts[4]);
                }
                if (parts.length >= 6) {
                    needHotel = "1".equals(parts[5]);
                }

                Trip t = new Trip(title, date, places, type, important, needHotel);

                tripList.add(t);
                originalTripList.add(t);
            }
        }
    }
}
