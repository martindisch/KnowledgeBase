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
    private ImageButton mFab;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Entry> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.container);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mFab = (ImageButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, EditActivity.class);
                startActivity(i);
            }
        });

        Intent i = getIntent();
        if (Intent.ACTION_SEARCH.equals(i.getAction())) {
            String query = i.getStringExtra(SearchManager.QUERY);
            results = Util.searchList(PlainStorage.getInstance().getmEntries(), query);
            displayData(results);
        }
    }
    // TODO: Refresh list after edit
    private void displayData(ArrayList<Entry> entries) {
        mAdapter = new EntryAdapter(entries);
        mRecyclerView.setAdapter(mAdapter);
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
