package com.martin.knowledgebase;

public class Entry {
    private String title;
    private String text;
    private String date;
    private int uid;

    public Entry(String title, String text, String date, int uid) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
