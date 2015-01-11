package com.martin.knowledgebase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class EditActivity extends Activity {

    private EditText mTitle, mText;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mTitle = (EditText) findViewById(R.id.etTitle);
        mText = (EditText) findViewById(R.id.etText);
        mSnackbar = new Snackbar((RelativeLayout) findViewById(R.id.snackbar), (TextView) findViewById(R.id.snackbar_button), this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_done) {
            if (!mTitle.getText().toString().contentEquals("") && !mText.getText().toString().contentEquals("")) {
                ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
                entries.add(new Entry(mTitle.getText().toString(), mText.getText().toString(), Util.getCurrentDate()));
                PlainStorage.getInstance().setmEntries(entries);
                finish();
            } else {
                // TODO: Snackbar popup
            }
            return true;
        }*/
        mSnackbar.showSnackbar();

        return super.onOptionsItemSelected(item);
    }
}
