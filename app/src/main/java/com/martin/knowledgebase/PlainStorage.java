package com.martin.knowledgebase;

import java.util.ArrayList;

public class PlainStorage {
    private static PlainStorage instance;

    private boolean mNewInstance;
    private ArrayList<Entry> mEntries;

    // Restrict the constructor from being instantiated
    private PlainStorage() {
    }

    public static synchronized PlainStorage getInstance() {
        if (instance == null) {
            instance = new PlainStorage();
            instance.mNewInstance = true;
            instance.mEntries = new ArrayList<Entry>();
        } else {
            instance.mNewInstance = false;
        }
        return instance;
    }

    public boolean isNew() {
        boolean before = mNewInstance;
        mNewInstance = false;
        return before;
    }

    public ArrayList<Entry> getmEntries() {
        return mEntries;
    }

    public void setmEntries(ArrayList<Entry> mEntries) {
        this.mEntries = mEntries;
    }
}
