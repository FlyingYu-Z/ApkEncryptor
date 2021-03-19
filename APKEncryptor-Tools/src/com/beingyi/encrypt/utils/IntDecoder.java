package com.beingyi.encrypt.utils;

public class IntDecoder {


    public static int decode(int value){
        return value-999;
    }

    public static String encode(String value){

        return "0x"+Integer.toHexString(Integer.parseInt(value.replaceFirst("0x",""),16)+999);
    }


}
