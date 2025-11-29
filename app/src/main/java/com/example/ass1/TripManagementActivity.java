package com.example.ass1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

// Screen for adding or editing a trip
public class TripManagementActivity extends AppCompatActivity {
// Created by Mahmoud Kafafi 1221974

    public static final String EXTRA_MODE  = "extra_mode";
    public static final String EXTRA_TRIP  = "extra_trip";
    public static final String EXTRA_INDEX = "extra_index";
    public static final String MODE_ADD  = "add";
    public static final String MODE_EDIT = "edit";

    private String screen_mode = MODE_ADD;
    private Trip trip_to_edit = null;
    private int item_index = -1;

    private TextView title_text;
    private EditText title_input;
    private TextView date_text;
    private Button date_button;
    private EditText places_input;
    private Button save_button;
    private Button cancel_button;
    private RadioGroup type_group;
    private CheckBox important_check;
    private Switch hotel_switch;

    private String selected_date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_management);

        title_text    = findViewById(R.id.tvTripFormTitle);
        title_input   = findViewById(R.id.etTripTitle);
        date_text     = findViewById(R.id.tvTripDate);
        date_button   = findViewById(R.id.btnPickDate);
        places_input  = findViewById(R.id.etTripPlaces);
        save_button   = findViewById(R.id.btnSaveTrip);
        cancel_button = findViewById(R.id.btnCancelTrip);
        type_group      = findViewById(R.id.rgTripType);
        important_check = findViewById(R.id.cbImportantTrip);
        hotel_switch    = findViewById(R.id.swNeedHotel);

        if (getIntent() != null) {
            String mode = getIntent().getStringExtra(EXTRA_MODE);
            if (mode != null) {
                screen_mode = mode;
            }

            trip_to_edit = (Trip) getIntent().getSerializableExtra(EXTRA_TRIP);
            item_index   = getIntent().getIntExtra(EXTRA_INDEX, -1);
        }

        if (MODE_EDIT.equals(screen_mode) && trip_to_edit != null) {
            title_text.setText("Edit Trip");
            title_input.setText(trip_to_edit.getTitle());
            date_text.setText(trip_to_edit.getDate());
            places_input.setText(trip_to_edit.getPlaces());
            selected_date = trip_to_edit.getDate();

            String type = trip_to_edit.getType();
            if (type != null) {
                if (type.equals("Tourism")) {
                    type_group.check(R.id.rbTypeTourism);
                } else if (type.equals("Business")) {
                    type_group.check(R.id.rbTypeBusiness);
                } else if (type.equals("Family")) {
                    type_group.check(R.id.rbTypeFamily);
                } else {
                    type_group.check(R.id.rbTypeOther);
                }
            }

            important_check.setChecked(trip_to_edit.isImportant());
            hotel_switch.setChecked(trip_to_edit.isNeedHotel());
        } else {
            // Add mode
            title_text.setText("Add Trip");
        }

        date_button.setOnClickListener(v -> pickDate());
        cancel_button.setOnClickListener(v -> finish());
        save_button.setOnClickListener(v -> saveTrip());
    }

    private void pickDate() {
        Calendar now = Calendar.getInstance();
        int y = now.get(Calendar.YEAR);
        int m = now.get(Calendar.MONTH);
        int d = now.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    month += 1;
                    selected_date = day + "-" + month + "-" + year;
                    date_text.setText(selected_date);
                },
                y, m, d
        ).show();
    }

    private void saveTrip() {
        String title  = title_input.getText().toString().trim();
        String places = places_input.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter trip title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selected_date == null) {
            Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = "Other";
        int checked_id = type_group.getCheckedRadioButtonId();
        if (checked_id == R.id.rbTypeTourism) {
            type = "Tourism";
        } else if (checked_id == R.id.rbTypeBusiness) {
            type = "Business";
        } else if (checked_id == R.id.rbTypeFamily) {
            type = "Family";
        } else if (checked_id == R.id.rbTypeOther) {
            type = "Other";
        }

        boolean important = important_check.isChecked();
        boolean need_hotel = hotel_switch.isChecked();

        Trip result_trip = new Trip(title, selected_date, places, type, important, need_hotel);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TRIP, result_trip);

        if (MODE_EDIT.equals(screen_mode)) {
            intent.putExtra(EXTRA_INDEX, item_index);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
