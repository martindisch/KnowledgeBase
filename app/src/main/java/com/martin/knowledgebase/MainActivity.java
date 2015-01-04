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
        mPassword = i.getStringExtra("Password");
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
        if (store.isNew()) {
            // load
        }
        ArrayList<Entry> entries = store.getmEntries();
        String all = "";
        for (Entry e : entries) {
            all += e.getTitle() + " - " + e.getDate() + "\n";
            all += e.getText() + "\n\n";
        }
        mEntries.setText(all);
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
            final ProgressDialog progress = ProgressDialog.show(this, "Encrypting", "Crunching data", true);
            new Thread() {

                @Override
                public void run() {
                    super.run();
                    try {
                        ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
                        String serData = Entry.stringify(entries);

                        progress.setMessage("Encrypting");
                        AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(mPassword, salt);
                        AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(serData, key);

                        progress.setMessage("Storing");
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
