package com.martin.knowledgebase;

import android.test.AndroidTestCase;

public class EncryptionTest extends AndroidTestCase {

    public void testStringEncryptDecrypt() throws Exception {
        Util.stringEncrypt(getContext(), "asd", "pwcheck", "This should equal itself.");
        assertEquals("Does not equal itself (-_-)", "This should equal itself.", Util.stringDecrypt(getContext(), "asd", "pwcheck"));
    }
}
