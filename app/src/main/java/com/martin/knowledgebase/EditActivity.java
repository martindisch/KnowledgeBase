package com.martin.knowledgebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class EditActivity extends Activity {

    private EditText mTitle, mText;
    private Snackbar mSnackbar;
    private int uid = -1;
    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mTitle = (EditText) findViewById(R.id.etTitle);
        mText = (EditText) findViewById(R.id.etText);
        mSnackbar = new Snackbar((RelativeLayout) findViewById(R.id.snackbar), "Title and/or text missing", this);

        Intent i = getIntent();
        if (i.hasExtra("uid")) {
            uid = i.getIntExtra("uid", -1);
            Entry entry = Util.getWithUid(PlainStorage.getInstance().getmEntries(), uid);
            index = PlainStorage.getInstance().getmEntries().indexOf(entry);
            mTitle.setText(entry.getTitle());
            mText.setText(entry.getText());
        }
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

        if (id == R.id.action_done) {
            if (!mTitle.getText().toString().contentEquals("") && !mText.getText().toString().contentEquals("")) {
                ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
                if (index != -1) {
                    entries.set(index, new Entry(mTitle.getText().toString(), mText.getText().toString(), Util.getCurrentDate(), uid));
                }
                else {
                    entries.add(new Entry(mTitle.getText().toString(), mText.getText().toString(), Util.getCurrentDate(), Util.getUid(this)));
                }
                PlainStorage.getInstance().setmEntries(entries);
                finish();
            } else {
                mSnackbar.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
