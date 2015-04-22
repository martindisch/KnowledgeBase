package com.martin.knowledgebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BackupActivity extends Activity {

    private RecyclerView mBackupList;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button mBackup, mRestore, mSetAddress;
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
        mSetAddress = (Button) findViewById(R.id.bSetAddress);

        mSetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAddress();
            }
        });

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
        requestAddress();
        return false;
    }

    private void requestAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_string, null);
        builder.setView(dialogLayout);
        builder.setTitle(R.string.address_title);
        builder.setMessage(R.string.address_msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mServerAddress = ((EditText) dialogLayout.findViewById(R.id.etInput)).getText().toString();
                SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("server_address", mServerAddress);
                editor.commit();
                connect();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void connect() {
        mStatus.setText(R.string.server_checking);
        mStatus.setTextColor(getResources().getColor(android.R.color.primary_text_light));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(mServerAddress, 13373);
                    socket.setSoTimeout(5000);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    out.write("{\"command\": \"ping\"}");
                    out.flush();
                    final String msg = in.readLine();
                    socket.close();
                    JSONObject response = new JSONObject(Util.unescapeJava(msg));
                    if (response.getString("response").contentEquals("pong")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStatus.setText(R.string.server_online);
                                mStatus.setTextColor(getResources().getColor(R.color.green));
                            }
                        });
                    }
                    else {
                        setOffline();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    setOffline();
                } catch (IOException e) {
                    e.printStackTrace();
                    setOffline();
                } catch (JSONException e) {
                    e.printStackTrace();
                    setOffline();
                }
            }
        }).start();
    }

    private void setOffline() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setText(R.string.server_offline);
                mStatus.setTextColor(getResources().getColor(R.color.red));
            }
        });
    }

}
