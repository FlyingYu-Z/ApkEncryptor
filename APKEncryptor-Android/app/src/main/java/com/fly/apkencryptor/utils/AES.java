package com.fly.apkencryptor.utils;


import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class AES {
    private static final String AESTYPE ="AES/ECB/PKCS5Padding"; 
    
    public static String Encrypt(String keyStr, String plainText) { 
        byte[] encrypt = null; 
        try{ 
            Key key = generateKey(keyStr); 
            Cipher cipher = Cipher.getInstance(AESTYPE); 
            cipher.init(Cipher.ENCRYPT_MODE, key); 
            encrypt = cipher.doFinal(plainText.getBytes());     
        }catch(Exception e){ 
            e.printStackTrace(); 
        }
        //return new String(Base64.encodeToString(encrypt,Base64.DEFAULT));
        return new BASE64Encoder().encode(encrypt).replaceAll("\r|\n", "");
    } 

    public static String Decrypt(String keyStr, String encryptData) {
        byte[] decrypt = null; 
        try{ 
            Key key = generateKey(keyStr); 
            Cipher cipher = Cipher.getInstance(AESTYPE); 
            cipher.init(Cipher.DECRYPT_MODE, key); 
            //decrypt = cipher.doFinal(Base64.decode(encryptData,0));
            decrypt = cipher.doFinal(new BASE64Decoder().decodeBuffer(encryptData));
            
        }catch(Exception e){ 
            e.printStackTrace(); 
        } 
        return new String(decrypt).trim();
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

    
    
    static String key=Conf.getKEY();
    //加密
    public static String encode(String str){

        return Encrypt(key,str);
    }

    //解密
    public static String decode(String str){

        return Decrypt(key,str);
    }

    //加密
    public static String encode(String str,String mKey){

        return Encrypt(mKey,str);
    }

    //加密
    public static String decode(String str,String mKey){

        return Decrypt(mKey,str);
    }



}

