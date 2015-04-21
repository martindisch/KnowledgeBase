package com.martin.knowledgebase;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

public class BackupActivity extends Activity {

    private RecyclerView mBackupList;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button mBackup, mRestore;
    private TextView mCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        mBackupList = (RecyclerView) findViewById(R.id.rvBackups);
        mBackupList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mBackupList.setLayoutManager(mLayoutManager);
        mCheck = (TextView) findViewById(R.id.tvStatus);
        mBackup = (Button) findViewById(R.id.bBackup);
        mRestore = (Button) findViewById(R.id.bRestore);
    }

}
