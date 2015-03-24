package com.martin.knowledgebase;

import android.test.AndroidTestCase;

import java.util.ArrayList;

public class SearchTest extends AndroidTestCase {

    private ArrayList<Entry> entries;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        entries = new ArrayList<Entry>();
        entries.add(new Entry("Title", "Text here", "2015-01-04"));
        entries.add(new Entry("Title2", "Text here2", "2015-01-05"));
        entries.add(new Entry("Title3", "Text here3", "2015-01-06"));
    }

    public void testSingleSearch() {
        ArrayList<Entry> resultTitle = Util.searchList(entries, "Title2");
        ArrayList<Entry> resultText = Util.searchList(entries, "here3");

        ArrayList<Entry> expectedTitleResult = new ArrayList<Entry>();
        expectedTitleResult.add(new Entry("Title2", "Text here2", "2015-01-05"));

        ArrayList<Entry> expectedTextResult = new ArrayList<Entry>();
        expectedTextResult.add(new Entry("Title3", "Text here3", "2015-01-06"));

        assertEquals("Not the searched result: ", resultTitle, expectedTitleResult);
        assertEquals("Not the expected result: ", resultText, expectedTextResult);
    }

    public void testMultipleSearch() {
        ArrayList<Entry> result = Util.searchList(entries, "Text here");

        ArrayList<Entry> expectedResult = new ArrayList<Entry>();
        expectedResult.add(new Entry("Title", "Text here", "2015-01-04"));
        expectedResult.add(new Entry("Title2", "Text here2", "2015-01-05"));
        expectedResult.add(new Entry("Title3", "Text here3", "2015-01-06"));

        assertEquals("Not the expected result: ", result, expectedResult);
    }

    public void testEmpty() {
        ArrayList<Entry> result = Util.searchList(entries, "Yeah, probably not.");

        ArrayList<Entry> expectedResult = new ArrayList<Entry>();

        assertEquals("Not the expected result: ", result, expectedResult);
    }

}
