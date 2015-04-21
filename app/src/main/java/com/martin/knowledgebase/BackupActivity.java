package com.martin.knowledgebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BackupActivity extends Activity {

    private RecyclerView mBackupList;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button mBackup, mRestore;
    private TextView mStatus;
    private String mServerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        mBackupList = (RecyclerView) findViewById(R.id.rvBackups);
        mBackupList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mBackupList.setLayoutManager(mLayoutManager);
        mStatus = (TextView) findViewById(R.id.tvStatus);
        mBackup = (Button) findViewById(R.id.bBackup);
        mRestore = (Button) findViewById(R.id.bRestore);

        if (getServerAddress()) {
            connect();
        }
    }

    private boolean getServerAddress() {
        final SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
        if (prefs.contains("server_address")) {
            mServerAddress = prefs.getString("server_address", "Oh crap");
            return true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setTitle(R.string.address_title);
        builder.setMessage(R.string.address_msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mServerAddress = input.getText().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("server_address", mServerAddress);
                editor.commit();
                connect();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
        return false;
    }

    private void connect() {
        mStatus.setText(R.string.server_checking);
    }

}
