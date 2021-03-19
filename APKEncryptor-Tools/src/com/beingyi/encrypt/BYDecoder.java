package com.beingyi.encrypt;

import com.beingyi.encrypt.utils.BES;
import com.beingyi.encrypt.utils.MD5;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class BYDecoder {

    public static String s1="fff";
    public static String s2="fsghff";


    public static String decode(){
        String result="";
        String pass="#pass";
        //String text="#text";
        String text=StringPool.s1;

        result=decode(text,pass);
        return result;
    }

    public static String decode(String text,String pass){

        int hexlen = text.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            text="0"+text;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=(byte)Integer.parseInt(text.substring(i,i+2),16);
            j++;
        }


        byte[] decrypt = null;
        try{
            Key key = new SecretKeySpec(pass.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypt = cipher.doFinal(result);

        }catch(Exception e){
            e.printStackTrace();
        }

        return new String(decrypt);
    }

}
