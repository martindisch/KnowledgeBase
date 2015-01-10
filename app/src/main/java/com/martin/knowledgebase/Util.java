package com.martin.knowledgebase;

import android.content.Context;
import android.content.SharedPreferences;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

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
            // TODO: error handling
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
            AesCbcWithIntegrity.CipherTextIvMac civ = encrypt(Entry.stringify(entries), key);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("data", civ.toString());
            editor.commit();
            // TODO: error handling
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
