package com.example.ass1;

import java.io.Serializable;

// Model class for a single Trip item
public class Trip implements Serializable {
// Created by Mahmoud Kafafi 1221974

    private String title;
    private String date;
    private String places;

    // Extra fields used in the form UI (type, checkbox, switch)
    private String type;
    private boolean important;
    private boolean needHotel;

    // Old constructor (kept for compatibility)
    public Trip(String title, String date, String places) {
        this(title, date, places, "Other", false, false);
    }
    //  خليت التنين لانو ضفتهم وما معي وقت اعدل عالكود اللي قبل لاني ضفت اخر 3 عناصر جديد بعد ما وصلت اخر المشروع
    public Trip(String title, String date, String places,
                String type, boolean important, boolean needHotel) {

        this.title = title;
        this.date = date;
        this.places = places;
        this.type = type;
        this.important = important;
        this.needHotel = needHotel;
    }
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPlaces() {
        return places;
    }

    public String getType() {
        return type;
    }

    public boolean isImportant() {
        return important;
    }

    public boolean isNeedHotel() {
        return needHotel;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
    public void setNeedHotel(boolean needHotel) {
        this.needHotel = needHotel;
    }
}
