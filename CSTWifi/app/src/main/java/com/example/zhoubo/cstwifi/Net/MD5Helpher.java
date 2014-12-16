package com.example.zhoubo.cstwifi.Net;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhoubo on 14/12/6.
 */
public class MD5Helpher {
    private static MessageDigest md5 = null;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String MD5(String input) {
        if (input == null || md5 == null) {
            return null;
        }
        md5.update(input.getBytes());
        return new BigInteger(1, md5.digest()).toString(16);

    }

    public static String passEncrypt(String input)
    {

        return MD5(input).substring(8,8+16);
    }
}
