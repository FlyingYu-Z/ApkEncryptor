package com.fly.apkencryptor.utils;

public class UserInfo {

    public static void setValue(String key,String value){
        SPUtils.putString("info",key,value);
    }

    public static String getStrValue(String key){
        return SPUtils.getString("info",key);
    }

    public static void setValue(String key,boolean value){
        SPUtils.putBoolean("info",key,value);
    }

    public static boolean getBoolValue(String key){
        return SPUtils.getBoolean("info",key);
    }


}
