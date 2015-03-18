package com.martin.knowledgebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private String mPassword;
    private ImageButton mFab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        mPassword = i.getStringExtra("password");

        mRecyclerView = (RecyclerView) findViewById(R.id.container);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFab = (ImageButton) findViewById(R.id.fab);
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
            final ProgressDialog progress = ProgressDialog.show(this, "Reading", "Decrypting", true);
            new Thread() {

                @Override
                public void run() {
                    super.run();

                    String plainText = Util.uDecrypt(MainActivity.this, mPassword);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setMessage("Crunching data");
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
            final SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
            final String salt = prefs.getString("salt", "Oh crap");
            final ProgressDialog progress = ProgressDialog.show(this, "Writing", "Encrypting", true);
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
        }

        return super.onOptionsItemSelected(item);
    }

}
