package com.martin.knowledgebase;

import android.text.format.Time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Entry {
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

    public static String stringify(ArrayList<Entry> entries) {
        String data = "";
        int size = entries.size();
        for (int i = 0; i < (size - 1); i++) {
            data += entries.get(i).getTitle() + "-INNER-";
            data += entries.get(i).getText() + "-INNER-";
            data += entries.get(i).getDate() + "-OUTER-";
        }
        data += entries.get(size - 1).getTitle() + "-INNER-";
        data += entries.get(size - 1).getText() + "-INNER-";
        data += entries.get(size - 1).getDate();
        return data;
    }

    public static ArrayList<Entry> listify(String data) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        String[] sEntries = data.split("-OUTER-");
        String[] sEntry;
        for (int i = 0; i < sEntries.length; i++) {
            sEntry = sEntries[i].split("-INNER-");
            entries.add(new Entry(sEntry[0], sEntry[1], sEntry[2]));
        }
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (date != null ? !date.equals(entry.date) : entry.date != null) return false;
        if (text != null ? !text.equals(entry.text) : entry.text != null) return false;
        if (title != null ? !title.equals(entry.title) : entry.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
