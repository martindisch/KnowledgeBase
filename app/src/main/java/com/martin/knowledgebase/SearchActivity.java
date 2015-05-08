package com.martin.knowledgebase;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;


public class SearchActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Entry> results;
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.container);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ImageButton mFab = (ImageButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, EditActivity.class);
                startActivity(i);
            }
        });

        Intent i = getIntent();
        if (Intent.ACTION_SEARCH.equals(i.getAction())) {
            query = i.getStringExtra(SearchManager.QUERY);
        }
    }

    private void displayData() {
        results = Util.searchList(PlainStorage.getInstance().getmEntries(), query);
        mAdapter = new EntryAdapter(results);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            Intent i = new Intent(this, EditActivity.class);
            int uid = item.getGroupId(); // Retrieve the uid
            i.putExtra("uid", uid);
            startActivity(i);
        } else {
            ArrayList<Entry> realEntries = PlainStorage.getInstance().getmEntries();
            int realIndex = realEntries.indexOf(Util.getWithUid(realEntries, item.getGroupId()));
            realEntries.remove(realIndex);
            PlainStorage.getInstance().setmEntries(realEntries);

            int index = results.indexOf(Util.getWithUid(results, item.getGroupId()));
            results.remove(index);
            mAdapter.notifyItemRemoved(index);
        }
        return super.onContextItemSelected(item);
    }
}
