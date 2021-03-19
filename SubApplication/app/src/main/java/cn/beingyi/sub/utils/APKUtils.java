package cn.beingyi.sub.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import java.io.File;


public class APKUtils
{
    

    
    
    public static String getLabel(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadLabel(pm).toString();
            } catch (OutOfMemoryError e) {
            }
        }
        return null;
        
    }
    
    public static String getPkgName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.packageName.toString();
            } catch (OutOfMemoryError e) {
            }
        }
        return null;

    }



    public static boolean isValid(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                appInfo.packageName.toString();
                return true;
            } catch (OutOfMemoryError e) {
            }
        }
        return false;

    }


    
    public static int getVersionCode(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return info.versionCode==0?0:info.versionCode;
            } catch (OutOfMemoryError e) {
            }
        }
        return 0;

    }

    

    
    public static String getVersionName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return info.versionName==null?"0":info.versionName;
            } catch (OutOfMemoryError e) {
            }
        }
        return null;

    }
    
    
    public static boolean isApkInstalled(Context context,String packagename)
    {
        PackageManager localPackageManager = ((Activity)context).getPackageManager();
        try
        {
            PackageInfo localPackageInfo = localPackageManager.getPackageInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
            return false;
        }

    }
    

    
    public void launchAPP(Context context,String packagename)
    {
        PackageManager packageManager = context.getPackageManager(); 
        Intent intent=new Intent(); 
        intent =packageManager.getLaunchIntentForPackage(packagename); 
        context.startActivity(intent);
    }
    
    
    
    
}
