package com.fly.apkencryptor.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BES {

    private static final String AESTYPE ="AES";

    private static byte[] encode(byte[] data,String keyStr) {
        byte[] encrypt = null;
        try{
            Key key = generateKey(MD5.encode16(keyStr));
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypt = cipher.doFinal(data);
        }catch(Exception e){
            e.printStackTrace();
        }
        return encrypt;
    }

    private static byte[] decode(byte[] data,String keyStr) {
        byte[] decrypt = null;
        try{
            Key key = generateKey(MD5.encode16(keyStr));
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypt = cipher.doFinal(data);

        }catch(Exception e){
            e.printStackTrace();
        }
        return decrypt;
    }

    private static Key generateKey(String key)throws Exception{
        try{
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            return keySpec;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }

    }

    public static String encode(String str,String key){
        return ByteHexUtils.bytesToHex(encode(str.getBytes(),key));
    }

    public static String decode(String str,String key){
        return new String(decode(ByteHexUtils.hexToByteArray(str),key));
    }




    public static class ByteHexUtils {


        /**
         * 字节转十六进制
         * @param b 需要进行转换的byte字节
         * @return  转换后的Hex字符串
         */
        public static String byteToHex(byte b){
            String hex = Integer.toHexString(b & 0xFF);
            if(hex.length() < 2){
                hex = "0" + hex;
            }
            return hex;
        }





        /**
         * 字节数组转16进制
         * @param bytes 需要转换的byte数组
         * @return  转换后的Hex字符串
         */
        public static String bytesToHex(byte[] bytes) {
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(bytes[i] & 0xFF);
                if(hex.length() < 2){
                    sb.append(0);
                }
                sb.append(hex);
            }
            return sb.toString();
        }





        /**
         * Hex字符串转byte
         * @param inHex 待转换的Hex字符串
         * @return  转换后的byte
         */
        public static byte hexToByte(String inHex){
            return (byte)Integer.parseInt(inHex,16);
        }




        /**
         * hex字符串转byte数组
         * @param inHex 待转换的Hex字符串
         * @return  转换后的byte数组结果
         */
        public static byte[] hexToByteArray(String inHex){
            int hexlen = inHex.length();
            byte[] result;
            if (hexlen % 2 == 1){
                //奇数
                hexlen++;
                result = new byte[(hexlen/2)];
                inHex="0"+inHex;
            }else {
                //偶数
                result = new byte[(hexlen/2)];
            }
            int j=0;
            for (int i = 0; i < hexlen; i+=2){
                result[j]=hexToByte(inHex.substring(i,i+2));
                j++;
            }
            return result;
        }



    }



    public static class MD5
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



}
