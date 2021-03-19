package com.fly.apkencryptor.utils;


import java.io.IOException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Base64Util {


    public static String encode(String str){

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(str.getBytes());

    }


    public static String decode(String str){

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            return  new String(decoder.decodeBuffer(str));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";

    }




}




