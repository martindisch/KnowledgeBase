package com.martin.knowledgebase;

import android.test.AndroidTestCase;
import android.util.Log;

public class EntryTest extends AndroidTestCase {

    public void testDate() throws Exception {
        Log.d("FFF", Util.getCurrentDate());
    }

    public void testUid() {
        Log.e("FFF", Util.getUid(getContext()) + "");
        Log.e("FFF", Util.getUid(getContext()) + "");
        Log.e("FFF", Util.getUid(getContext()) + "");
        Log.e("FFF", Util.getUid(getContext()) + "");
    }
}
