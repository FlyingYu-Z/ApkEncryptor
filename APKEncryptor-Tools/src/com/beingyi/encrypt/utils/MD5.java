package com.beingyi.encrypt.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5
{
    

    public static String encode(String string) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
	
    public static String encode16(String encryptStr) {
		return encode(encryptStr).substring(8, 24);
	}
	
}
