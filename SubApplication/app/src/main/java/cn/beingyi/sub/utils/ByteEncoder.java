package cn.beingyi.sub.utils;


import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class ByteEncoder {
    private static final String AESTYPE ="AES/ECB/PKCS5Padding"; 
    
    public static byte[] Encrypt(byte[] data,String keyStr) {
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

    public static byte[] Decrypt(byte[] data,String keyStr) {
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

    



}

