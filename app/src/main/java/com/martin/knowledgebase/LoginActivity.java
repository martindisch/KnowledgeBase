package com.martin.knowledgebase;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.security.GeneralSecurityException;

import static com.tozny.crypto.android.AesCbcWithIntegrity.generateSalt;
import static com.tozny.crypto.android.AesCbcWithIntegrity.saltString;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.title_activity_login));
        if (savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
            if (prefs.contains("salt")) {
                getFragmentManager().beginTransaction().add(R.id.container, new FragmentCheckPassword()).commit();
            }
            else {
                getFragmentManager().beginTransaction().add(R.id.container, new FragmentSetPassword()).commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO: Remove debug code
            SharedPreferences prefs = getSharedPreferences("KB", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class FragmentSetPassword extends Fragment {

        public FragmentSetPassword() {
        }

        private EditText mFirst, mSecond;
        private Button mGenerate;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_setpassword, container, false);
            mFirst = (EditText) rootView.findViewById(R.id.etFirst);
            mSecond = (EditText) rootView.findViewById(R.id.etSecond);
            mGenerate = (Button) rootView.findViewById(R.id.bEnter);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (savedInstanceState != null) {
                mFirst.setText(savedInstanceState.getString("first"));
                mSecond.setText(savedInstanceState.getString("second"));
            }

            mGenerate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFirst.getText().toString().contentEquals(mSecond.getText().toString()) && !mFirst.getText().toString().contentEquals("")) {
                        try {
                            String salt = saltString(generateSalt());
                            SharedPreferences prefs = getActivity().getSharedPreferences("KB", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("salt", salt);
                            editor.commit();
                            // TODO: Get the password to MainActivity via an Intent
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        // TODO: Have snackbar pop up
                    }
                }
            });
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("first", mFirst.getText().toString());
            outState.putString("second", mSecond.getText().toString());
        }
    }

    public static class FragmentCheckPassword extends Fragment {

        public FragmentCheckPassword() {
        }

        private EditText mFirst;
        private Button mLogin;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_checkpassword, container, false);
            mFirst = (EditText) rootView.findViewById(R.id.etFirst);
            mLogin = (Button) rootView.findViewById(R.id.bEnter);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            if (savedInstanceState != null) {
                mFirst.setText(savedInstanceState.getString("first"));
            }

            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Get the password to MainActivity via an Intent
                }
            });
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString("first", mFirst.getText().toString());
        }
    }
}
