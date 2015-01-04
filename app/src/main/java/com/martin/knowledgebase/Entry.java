package com.martin.knowledgebase;

import android.text.format.Time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Entry implements Serializable {
    private String title;
    private String text;
    private String date;

    public Entry(String title, String text, String date) {
        this.title = title;
        this.text = text;
        this.date = date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }
}
