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
        /* At this point, the current instance of PlainStorage may have been lost when the activity has been in the background in a
        low memory environment for an extended period of time. This could actually happen in all the places getting a new instance
        of PlainStorage. I could enforce writing all the content to disk when briefly exiting the app to prevent data loss. However,
        I believe the user (which is currently and will most likely remain me) should be free to leave the app running without all
        this taking place, as the user is in fact aware of how the app works and will consider the consequences. */
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
