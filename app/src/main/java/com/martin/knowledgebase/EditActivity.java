package com.martin.knowledgebase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class EditActivity extends Activity {

    private EditText mTitle, mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mTitle = (EditText) findViewById(R.id.etTitle);
        mText = (EditText) findViewById(R.id.etText);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (!mTitle.getText().toString().contentEquals("") && !mText.getText().toString().contentEquals("")) {
                ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
                entries.add(new Entry(mTitle.getText().toString(), mText.getText().toString(), Entry.getCurrentDate()));
                PlainStorage.getInstance().setmEntries(entries);
                finish();
            }
            else {
                // TODO: Snackbar popup
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
