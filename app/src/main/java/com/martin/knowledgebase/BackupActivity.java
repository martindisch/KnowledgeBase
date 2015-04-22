package com.martin.knowledgebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackupActivity extends Activity {

    private RecyclerView mBackupList;
    private RecyclerView.LayoutManager mLayoutManager;
    private SimpleAdapter mAdapter;
    private Button mBackup, mRestore, mSetAddress;
    private TextView mStatus;
    private String mServerAddress, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        mLayoutManager = new LinearLayoutManager(this);
        mBackupList = (RecyclerView) findViewById(R.id.rvBackups);
        mBackupList.setHasFixedSize(true);
        mBackupList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
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
        mBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store();
            }
        });

        if (getServerAddress()) {
            ping();
        }

        Intent i = getIntent();
        mPassword = i.getStringExtra("password");
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
                ping();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void ping() {
        mStatus.setText(R.string.server_checking);
        mStatus.setTextColor(getResources().getColor(android.R.color.primary_text_light));

        new Thread(new Runnable() {
            @Override
            public void run() {

                String response = Util.sendCommand(mServerAddress, "{\"command\": \"ping\"}");
                try {
                    JSONObject jResponse = new JSONObject(Util.unescapeJava(response));
                    if (Util.hasError(jResponse)) {
                        Log.e("Error tag", jResponse.getString("error"));
                        setOffline();
                    } else {
                        if (jResponse.getString("response").contentEquals("pong")) {
                            setOnline();
                        } else {
                            setOffline();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setOffline();
                }
            }
        }).start();
    }

    private void store() {
        final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.writing), getString(R.string.encrypting), true);
        new Thread() {

            @Override
            public void run() {
                super.run();
                Util.uEncrypt(BackupActivity.this, mPassword);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setTitle(R.string.uploading);
                        progress.setMessage(getResources().getString(R.string.uploading_long));
                    }
                });
                SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
                String response = Util.sendCommand(mServerAddress, "{\"command\": \"store\", \"date\": \"" + Util.getCurrentDate() + "\", \"data\": \"" + prefs.getString("data", "Oh crap") + "\"}");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
                try {
                    JSONObject jResponse = new JSONObject(Util.unescapeJava(response));
                    if (Util.hasError(jResponse)) {
                        Log.e("Error tag", jResponse.getString("error"));
                    } else {
                        if (jResponse.getString("response").contentEquals("ok")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BackupActivity.this, getResources().getString(R.string.store_success), Toast.LENGTH_SHORT).show();
                                    entries();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BackupActivity.this, getResources().getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void entries() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String response = Util.sendCommand(mServerAddress, "{\"command\": \"entries\"}");
                try {
                    JSONObject jResponse = new JSONObject(Util.unescapeJava(response));
                    if (Util.hasError(jResponse)) {
                        Log.e("Error tag", jResponse.getString("error"));
                    } else {
                        JSONArray entries = jResponse.getJSONArray("response");
                        mAdapter = new SimpleAdapter(entries);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBackupList.setAdapter(mAdapter);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void setOnline() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setText(R.string.server_online);
                mStatus.setTextColor(getResources().getColor(R.color.green));
                entries();
            }
        });
    }

}
