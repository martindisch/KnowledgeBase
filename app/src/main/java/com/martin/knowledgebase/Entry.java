package com.martin.knowledgebase;

import java.io.Serializable;

public class Entry implements Serializable {
    private String date;
    private String title;
    private String text;

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
}
