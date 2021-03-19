package com.fly.apkencryptor.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;


public class SelfInfo
{
    
    

    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }



    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name ="";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
    
    
    public static String getApkPath(Context context){
        
        String path=context.getPackageResourcePath();
        return path;
    }
    
    
    
    public static int getSign(Context context){

        PackageManager pm = context.getPackageManager();  
        PackageInfo pi = null;  
        int sig = 0;  
        try {  
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);  
            Signature[] s = pi.signatures;  
            sig = s[0].hashCode();    
        } catch (Exception e1) {  
            sig = 0;  
            e1.printStackTrace();  
        }  
        return sig;
        
    }
    
    public static String getSignature(Context context)
    {
        try {
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;
            /******* 循环遍历签名数组拼接应用签名 *******/
            return signatures[0].toCharsString();
            /************** 得到应用签名 **************/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
}
