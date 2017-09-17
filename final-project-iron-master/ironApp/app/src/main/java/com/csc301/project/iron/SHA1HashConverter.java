package com.csc301.project.iron;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * Created by armanghassemi on 2017-03-13.
 */

public class SHA1HashConverter {

    public static HashCode computeSHA1(String passwordText) {
        return Hashing.sha1().hashString(passwordText, Charset.defaultCharset());
    }
}
