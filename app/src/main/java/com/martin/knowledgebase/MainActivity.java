package com.martin.knowledgebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private RecyclerView mRecyclerView;
    private String mPassword;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        mPassword = i.getStringExtra("password");

        mRecyclerView = (RecyclerView) findViewById(R.id.container);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ImageButton mFab = (ImageButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlainStorage store = PlainStorage.getInstance();
        final SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
        if (store.isNew() && prefs.contains("data")) {
            final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.reading), getString(R.string.decrypting), true);
            new Thread() {

                @Override
                public void run() {
                    super.run();

                    String plainText = Util.uDecrypt(MainActivity.this, mPassword);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setMessage(getString(R.string.crunching));
                        }
                    });
                    PlainStorage.getInstance().setmEntries(Util.listify(plainText));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayData();
                            progress.dismiss();
                        }
                    });
                }

            }.start();
        } else {
            displayData();
        }

    }

    private void displayData() {
        ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
        mAdapter = new EntryAdapter(entries);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.martin.knowledgebase", "com.martin.knowledgebase.SearchActivity")));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.writing), getString(R.string.encrypting), true);
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    Util.uEncrypt(MainActivity.this, mPassword);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                }

            }.start();

            return true;
        } else if (id == R.id.action_backup) {
            Intent i = new Intent(this, BackupActivity.class);
            i.putExtra("password", mPassword);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent i = new Intent(this, EditActivity.class);
            int uid = item.getGroupId(); // Retrieve the uid
            i.putExtra("uid", uid);
            startActivity(i);
        } else {
            ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
            int index = entries.indexOf(Util.getWithUid(entries, item.getGroupId()));
            entries.remove(index);
            PlainStorage.getInstance().setmEntries(entries);
            mAdapter.notifyItemRemoved(index);
        }
        return super.onContextItemSelected(item);
    }
}
