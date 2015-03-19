package com.martin.knowledgebase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class ViewActivity extends Activity {

    private TextView mTitle, mDate, mText;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mTitle = (TextView) findViewById(R.id.tvTitle);
        mDate = (TextView) findViewById(R.id.tvDate);
        mText = (TextView) findViewById(R.id.tvText);

        index = getIntent().getIntExtra("index", -1);
    }

    private void displayData() {
        Entry entry = PlainStorage.getInstance().getmEntries().get(index);
        mTitle.setText(entry.getTitle());
        mDate.setText(entry.getDate());
        mText.setText(entry.getText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
