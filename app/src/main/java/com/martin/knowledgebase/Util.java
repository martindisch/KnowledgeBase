package com.martin.knowledgebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.tozny.crypto.android.AesCbcWithIntegrity.decryptString;
import static com.tozny.crypto.android.AesCbcWithIntegrity.encrypt;
import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;

public class Util {

    public static String uDecrypt(Context context, String password) {
        SharedPreferences prefs = context.getSharedPreferences("KB", Context.MODE_PRIVATE);
        String plainText = "Oh crap";
        try {
            AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(password, prefs.getString("salt", "Oh crap"));
            plainText = decryptString(new AesCbcWithIntegrity.CipherTextIvMac(prefs.getString("data", "Oh crap")), key);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return plainText;
    }

    public static void uEncrypt(Context context, String password) {
        final SharedPreferences prefs = context.getSharedPreferences("KB", Context.MODE_PRIVATE);

        final ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();

        try {
            // TODO: Test this method to ensure password is kept in memory, even after many activiy changes
            if (password == null) {
                AlertDialog.Builder dg = new AlertDialog.Builder(context);
                final EditText et = new EditText(context);
                dg.setView(et);
                dg.setMessage(R.string.reenter_password);
                dg.setNegativeButton(R.string.cancel, null);
                dg.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (PasswordHash.validatePassword(et.getText().toString(), prefs.getString("pwhash", "Oh crap"))) {
                                try {
                                    AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(et.getText().toString(), prefs.getString("salt", "Oh crap"));
                                    AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(stringify(entries), key);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("data", civ.toString());
                                    editor.commit();
                                } catch (GeneralSecurityException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dg.show();
            }
            else {
                AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(password, prefs.getString("salt", "Oh crap"));
                AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(stringify(entries), key);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("data", civ.toString());
                editor.commit();
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String stringDecrypt(Context context, String password, String name) {
        SharedPreferences prefs = context.getSharedPreferences("KB", Context.MODE_PRIVATE);
        String plainText = "Oh crap";
        try {
            AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(password, prefs.getString("salt", "Oh crap"));
            plainText = decryptString(new AesCbcWithIntegrity.CipherTextIvMac(prefs.getString(name, "Oh crap")), key);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return plainText;
    }

    public static void stringEncrypt(Context context, String password, String name, String content) {
        SharedPreferences prefs = context.getSharedPreferences("KB", Context.MODE_PRIVATE);

        try {
            AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(password, prefs.getString("salt", "Oh crap"));
            AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(content, key);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(name, civ.toString());
            editor.commit();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    public static String stringify(ArrayList<Entry> entries) {
        String data = "";
        int size = entries.size();
        for (int i = 0; i < (size - 1); i++) {
            data += entries.get(i).getTitle() + "-INNER-";
            data += entries.get(i).getText() + "-INNER-";
            data += entries.get(i).getDate() + "-INNER-";
            data += entries.get(i).getUid() + "-OUTER-";
        }
        data += entries.get(size - 1).getTitle() + "-INNER-";
        data += entries.get(size - 1).getText() + "-INNER-";
        data += entries.get(size - 1).getDate() + "-INNER-";
        data += entries.get(size - 1).getUid();
        return data;
    }

    public static ArrayList<Entry> listify(String data) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        String[] sEntries = data.split("-OUTER-");
        String[] sEntry;
        for (int i = 0; i < sEntries.length; i++) {
            sEntry = sEntries[i].split("-INNER-");
            entries.add(new Entry(sEntry[0], sEntry[1], sEntry[2], Integer.parseInt(sEntry[3])));
        }
        return entries;
    }

    public static ArrayList<Entry> searchList(ArrayList<Entry> list, String query) {
        ArrayList<Entry> results = new ArrayList<Entry>();
        Entry entry;
        for (int i = 0; i < list.size(); i++) {
            entry = list.get(i);
            if (entry.getTitle().toLowerCase().contains(query.toLowerCase()) || entry.getText().toLowerCase().contains(query.toLowerCase())) {
                results.add(entry);
            }
        }
        return results;
    }

    public static int getUid(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("KB", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int uid = prefs.getInt("next_uid", 0);
        editor.putInt("next_uid", uid + 1);
        editor.commit();
        return uid;
    }

    public static Entry getWithUid(ArrayList<Entry> entries, int uid) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getUid() == uid) {
                return entries.get(i);
            }
        }
        return new Entry("No entry with this uid", "Ditto", "2000-01-01", -1);
    }

    public static String unescapeJava(String escaped) {
        if(escaped.indexOf("\\u")==-1)
            return escaped;

        String processed="";

        int position=escaped.indexOf("\\u");
        while(position!=-1) {
            if(position!=0)
                processed+=escaped.substring(0,position);
            String token=escaped.substring(position+2,position+6);
            escaped=escaped.substring(position+6);
            processed+=(char)Integer.parseInt(token,16);
            position=escaped.indexOf("\\u");
        }
        processed+=escaped;

        return processed;
    }
}
