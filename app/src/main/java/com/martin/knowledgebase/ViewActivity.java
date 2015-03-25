package com.martin.knowledgebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ViewActivity extends Activity {

    private TextView mTitle, mDate, mText;
    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mTitle = (TextView) findViewById(R.id.tvTitle);
        mDate = (TextView) findViewById(R.id.tvDate);
        mText = (TextView) findViewById(R.id.tvText);

        uid = getIntent().getIntExtra("uid", -1);
    }

    private void displayData() {
        Entry entry = Util.getWithUid(PlainStorage.getInstance().getmEntries(), uid);
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
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra("uid", uid);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
