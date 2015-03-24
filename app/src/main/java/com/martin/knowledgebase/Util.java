package com.martin.knowledgebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
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
        SharedPreferences prefs = context.getSharedPreferences("KB", Context.MODE_PRIVATE);

        ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();

        try {
            AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(password, prefs.getString("salt", "Oh crap"));
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
            data += entries.get(i).getDate() + "-OUTER-";
        }
        data += entries.get(size - 1).getTitle() + "-INNER-";
        data += entries.get(size - 1).getText() + "-INNER-";
        data += entries.get(size - 1).getDate();
        return data;
    }

    public static ArrayList<Entry> listify(String data) {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        String[] sEntries = data.split("-OUTER-");
        String[] sEntry;
        for (int i = 0; i < sEntries.length; i++) {
            sEntry = sEntries[i].split("-INNER-");
            entries.add(new Entry(sEntry[0], sEntry[1], sEntry[2]));
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
}
