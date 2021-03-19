package cn.beingyi.sub.apps;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.zip.ZipFile;

import cn.beingyi.sub.ui.ToastUtils;
import cn.beingyi.sub.utils.ABIUtils;
import cn.beingyi.sub.utils.BYProtectUtils;
import cn.beingyi.sub.utils.CrashHandler;
import cn.beingyi.sub.utils.FileUtils;
import cn.beingyi.sub.utils.Forbidden;
import cn.beingyi.sub.utils.Native;
import dalvik.system.PathClassLoader;

public class BaseApplication extends Application {


    static Context mContext;


    @Override
    protected void attachBaseContext(Context base) {

        try {
            ZipFile zipFile = new ZipFile(getApkPath(base));
            if(zipFile.getEntry("src/"+ BYProtectUtils.getAssetsName("application"))==null) {
            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.attachBaseContext(base);

        mContext = base;


    }

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(getApplicationContext());


        mContext = getApplicationContext();



        try {

        } catch (Exception e) {

        }


    }


    public static Context getContext() {

        return mContext;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        System.exit(0);
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    public String getApkPath(Context context) {

        String path = context.getPackageResourcePath();
        return path;
    }

    /*
     *
     *
     *
     *
     * 以下多余
     *
     *
     *
     *
     *
     *
     * */





}
