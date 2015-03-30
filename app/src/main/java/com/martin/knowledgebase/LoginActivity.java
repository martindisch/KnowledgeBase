package com.martin.knowledgebase;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
            } else {
                getFragmentManager().beginTransaction().add(R.id.container, new FragmentSetPassword()).commit();
            }
        }
    }

    public static class FragmentSetPassword extends Fragment {

        private EditText mFirst, mSecond;
        private Button mGenerate;
        private Snackbar mSnackbar;

        public FragmentSetPassword() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_setpassword, container, false);
            mFirst = (EditText) rootView.findViewById(R.id.etFirst);
            mSecond = (EditText) rootView.findViewById(R.id.etSecond);
            mGenerate = (Button) rootView.findViewById(R.id.bEnter);
            mSnackbar = new Snackbar((RelativeLayout) rootView.findViewById(R.id.snackbar), "Passwords not the same", getActivity());
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
                        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Saving", "Saving password", true);

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                try {
                                    String salt = saltString(generateSalt());
                                    SharedPreferences prefs = getActivity().getSharedPreferences("KB", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("salt", salt);
                                    editor.putString("pwhash", PasswordHash.createHash(mFirst.getText().toString()));
                                    editor.commit();

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                            Intent i = new Intent(getActivity(), MainActivity.class);
                                            i.putExtra("password", mFirst.getText().toString());
                                            startActivity(i);
                                            getActivity().finish();
                                        }
                                    });
                                } catch (GeneralSecurityException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } else {
                        mSnackbar.show();
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

        private EditText mFirst;
        private Button mLogin;
        private Snackbar mSnackbar;

        public FragmentCheckPassword() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_checkpassword, container, false);
            mFirst = (EditText) rootView.findViewById(R.id.etFirst);
            mLogin = (Button) rootView.findViewById(R.id.bEnter);
            mSnackbar = new Snackbar((RelativeLayout) rootView.findViewById(R.id.snackbar), "Enter the password", getActivity());
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
                    if (!mFirst.getText().toString().contentEquals("")) {
                        final ProgressDialog progress = ProgressDialog.show(getActivity(), "Checking", "Checking password", true);
                        new Thread() {

                            @Override
                            public void run() {
                                super.run();
                                SharedPreferences prefs = getActivity().getSharedPreferences("KB", MODE_PRIVATE);
                                try {

                                    if (PasswordHash.validatePassword(mFirst.getText().toString(), prefs.getString("pwhash", "Oh crap"))) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.dismiss();
                                                Intent i = new Intent(getActivity(), MainActivity.class);
                                                i.putExtra("password", mFirst.getText().toString());
                                                startActivity(i);
                                                getActivity().finish();
                                            }
                                        });
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.dismiss();
                                                mSnackbar.setText("Wrong password");
                                                mSnackbar.show();
                                            }
                                        });
                                    }

                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeySpecException e) {
                                    e.printStackTrace();
                                }
                            }

                        }.start();
                    } else {
                        mSnackbar.setText("Enter the password");
                        mSnackbar.show();
                    }
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
