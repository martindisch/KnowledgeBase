package com.martin.knowledgebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import static com.tozny.crypto.android.AesCbcWithIntegrity.decryptString;
import static com.tozny.crypto.android.AesCbcWithIntegrity.encrypt;
import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;


public class MainActivity extends Activity {

    private String mPassword;
    private LinearLayout container;
    private TextView mEntries;
    private ImageButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        mPassword = i.getStringExtra("password");
        container = (LinearLayout) findViewById(R.id.container);
        mEntries = (TextView) findViewById(R.id.tvEntries);
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
            final String salt = prefs.getString("salt", "Oh crap");
            final ProgressDialog progress = ProgressDialog.show(this, "Reading", "Reading data", true);
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    try {
                        String data = prefs.getString("data", "Oh crap");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setMessage("Decrypting");
                            }
                        });
                        AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(mPassword, salt);
                        String plainText = decryptString(new AesCbcWithIntegrity.CipherTextIvMac(data), key);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setMessage("Crunching data");
                            }
                        });
                        PlainStorage.getInstance().setmEntries(Entry.listify(plainText));
                        displayData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });
                        // TODO: Error handling
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

            }.start();
        }
        else {
            displayData();
        }

    }

    private void displayData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
                String all = "";
                for (Entry e : entries) {
                    all += e.getTitle() + " - " + e.getDate() + "\n";
                    all += e.getText() + "\n\n";
                }
                final String text = all;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mEntries.setText(text);
                    }
                });
            }
        }.run();
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
            final ProgressDialog progress = ProgressDialog.show(this, "Writing", "Crunching data", true);
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    try {
                        ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
                        String serData = Entry.stringify(entries);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setMessage("Encrypting");
                            }
                        });
                        AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(mPassword, salt);
                        AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(serData, key);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setMessage("Storing");
                            }
                        });
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("data", civ.toString());
                        editor.commit();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

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
