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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackupActivity extends Activity implements SimpleAdapter.OnClickListener {

    private RecyclerView mBackupList;
    private SimpleAdapter mAdapter;
    private Button mRestore;
    private TextView mStatus;
    private String mServerAddress, mPassword;
    private int mSelected = -1;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mBackupList = (RecyclerView) findViewById(R.id.rvBackups);
        mBackupList.setHasFixedSize(true);
        mBackupList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mBackupList.setLayoutManager(mLayoutManager);
        mStatus = (TextView) findViewById(R.id.tvStatus);
        Button mBackup = (Button) findViewById(R.id.bBackup);
        mRestore = (Button) findViewById(R.id.bRestore);
        Button mSetAddress = (Button) findViewById(R.id.bSetAddress);

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
        mRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });

        if (getServerAddress()) {
            ping();
        }

        Intent i = getIntent();
        mPassword = i.getStringExtra("password");

        mSnackbar = new Snackbar((RelativeLayout) findViewById(R.id.snackbar), this);

        if (!(PlainStorage.getInstance().getmEntries().size() > 0)) {
            mBackup.setEnabled(false);
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
                        progress.setMessage(getString(R.string.uploading_long));
                    }
                });
                SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
                String response = Util.sendCommand(mServerAddress, "{\"command\": \"store\", \"date\": \"" + Util.getCurrentDate() + "\", \"data\": \"" + prefs.getString("salt", "Oh crap") + "/salt" + prefs.getString("data", "Oh crap") + "\"}");
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
                                    mSnackbar.setText(getString(R.string.store_success));
                                    mSnackbar.show();
                                    entries();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSnackbar.setText(getString(R.string.no_store));
                                    mSnackbar.show();
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
        mRestore.setEnabled(false);
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
                        mAdapter = new SimpleAdapter(entries, BackupActivity.this);
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

    private void restore() {
        final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.downloading), getString(R.string.download_from_server), true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String selectedDate = mAdapter.getEntries().getString(mSelected);
                    String response = Util.sendCommand(mServerAddress, "{\"command\": \"get\", \"date\": \"" + selectedDate + "\"}");
                    JSONObject jResponse = new JSONObject(Util.unescapeJava(response));
                    if (Util.hasError(jResponse)) {
                        Log.e("Error tag", jResponse.getString("error"));
                    } else {
                        SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        String[] datasalt = jResponse.getString("response").split("/salt");
                        editor.putString("salt", datasalt[0]);
                        editor.putString("data", datasalt[1]);
                        editor.commit();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setMessage(getString(R.string.decrypting));
                                progress.setTitle(getString(R.string.reading));
                            }
                        });
                        String plainText = Util.uDecrypt(BackupActivity.this, mPassword);
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
                                progress.dismiss();
                                mSnackbar.setText(getString(R.string.restored));
                                mSnackbar.show();
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

    @Override
    public void onClick(View v, int position) {
        mSelected = position;
        for (int i = 0; i < mBackupList.getChildCount(); i++) {
            if (i != position) {
                mBackupList.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.background));
            }
        }
        mBackupList.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.grey));
        mRestore.setEnabled(true);
    }

    @Override
    public void onLongClick(View v, int position) {
        mSelected = position;
        for (int i = 0; i < mBackupList.getChildCount(); i++) {
            if (i != position) {
                mBackupList.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.background));
            }
        }
        mBackupList.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.grey));

        AlertDialog.Builder dg = new AlertDialog.Builder(this);
        dg.setTitle(R.string.delete);
        dg.setMessage(R.string.confirm_delete);
        dg.setNegativeButton(R.string.no, null);
        dg.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progress = ProgressDialog.show(BackupActivity.this, getString(R.string.contacting), getString(R.string.asking_remove), true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            String selectedDate = mAdapter.getEntries().getString(mSelected);
                            String response = Util.sendCommand(mServerAddress, "{\"command\": \"delete\", \"date\": \"" + selectedDate + "\"}");
                            JSONObject jResponse = new JSONObject(Util.unescapeJava(response));
                            if (Util.hasError(jResponse)) {
                                Log.e("Error tag", jResponse.getString("error"));
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        entries();
                                        progress.dismiss();
                                        mSnackbar.setText(getString(R.string.deleted));
                                        mSnackbar.show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        dg.show();
    }
}
