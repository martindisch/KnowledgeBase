package com.martin.knowledgebase;

import android.test.AndroidTestCase;

import java.util.ArrayList;

public class ConversionTest extends AndroidTestCase {

    private ArrayList<Entry> entries;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        entries = new ArrayList<Entry>();
        entries.add(new Entry("Title", "Text here", "2015-01-04", 0));
        entries.add(new Entry("Title2", "Text here2", "2015-01-05", 1));
        entries.add(new Entry("Title3", "Text here3", "2015-01-06", 2));
    }

    public void testStringify() throws Exception {
        String string = Util.stringify(entries);
        assertEquals("Not the same:", "Title-INNER-Text here-INNER-2015-01-04-INNER-0-OUTER-Title2-INNER-Text here2-INNER-2015-01-05-INNER-1-OUTER-Title3-INNER-Text here3-INNER-2015-01-06-INNER-2", string);
    }

    public void testEquals() throws Exception {
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        entries1.add(new Entry("Title", "Text here", "2015-01-04", 0));

        ArrayList<Entry> entries2 = new ArrayList<Entry>();
        entries2.add(new Entry("Title", "Text here", "2015-01-04", 1));

        assertEquals("Not the same:", entries1, entries2);

        /*ArrayList<String> list1 = new ArrayList<String>();
        list1.add("Hey");

        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("Hey");

        assertEquals("Not the same:", list1, list2);*/
    }

    public void testListify() throws Exception {
        ArrayList<Entry> second = Util.listify(Util.stringify(entries));
        assertEquals("Not the same:", entries, second);
    }
}
