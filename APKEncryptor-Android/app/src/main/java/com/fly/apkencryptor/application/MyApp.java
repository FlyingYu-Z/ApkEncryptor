package com.fly.apkencryptor.application;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;
import com.fly.apkencryptor.activity.ErrReport;
import com.fly.apkencryptor.crash.CrashHandler;
import com.fly.apkencryptor.utils.BYProtectUtils;
import com.fly.apkencryptor.utils.CustomFun;
import com.fly.apkencryptor.utils.DexLoader;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.LanguageUtil;
import com.fly.apkencryptor.utils.LifeCallBack;
import com.fly.apkencryptor.utils.MD5;
import com.fly.apkencryptor.utils.Native;
import com.fly.apkencryptor.utils.RecoveryPMS;
import com.fly.apkencryptor.utils.SPUtils;
import com.fly.apkencryptor.widget.ToastUtils;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.security.Security;

import sun1.security.provider.JavaProvider;


final public class MyApp extends Application implements IExceptionHandler,Application.ActivityLifecycleCallbacks
{


    private DbManager.DaoConfig daoConfig;
    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }

    private static Context mContext;

    @Override
    protected void attachBaseContext(Context base) {
        try {
            Class loadClass = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge");
            Field declaredField = loadClass.getDeclaredField("disableHooks");
            Field declaredField2 = loadClass.getDeclaredField("runtime");
            declaredField.setAccessible(true);
            declaredField2.setAccessible(true);
            declaredField.setBoolean(null, true);
            declaredField2.setInt(null, 2);
        } catch (Exception e) {

        }

        super.attachBaseContext(base);

        checkLoader();

        DefenseCrash.initialize();
        DefenseCrash.install(this);

        System.loadLibrary("BY");

    }



    @Override
    public void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode) {
        throwable.printStackTrace();
        showErr(throwable.toString());
    }

    @Override
    public void onEnterSafeMode() {

    }

    @Override
    public void onMayBeBlackScreen(Throwable throwable) {
        showErr(throwable.toString());
    }


    public boolean checkLoader(){
        if(getClassLoader().getClass().getName().startsWith("l.")){
            CustomFun.exit();
            mContext=null;
            return  true;
        }
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String language=preferences.getString("pre_settings_language","");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(mContext, language);
        }
        new RecoveryPMS();


        //CrashHandler crashHandler = new CrashHandler();
        //crashHandler.init(getApplicationContext());


        x.Ext.init(this);
        x.Ext.setDebug(false); // 开启debug会影响性能

        daoConfig = new DbManager.DaoConfig()
                .setDbName("AppData")//创建数据库的名称
                .setDbVersion(1)//数据库版本号
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                    }
                });

        Security.addProvider(new JavaProvider());

        LifeCallBack lifeCallBack=new LifeCallBack();
        registerActivityLifecycleCallbacks(lifeCallBack);


    }




    public void showErr(String err){

        Intent intent=new Intent(getApplicationContext(), ErrReport.class);
        intent.putExtra("err",err);
        getApplicationContext().startActivity(intent);

    }








    public static Context getContext(){
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

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }



}

