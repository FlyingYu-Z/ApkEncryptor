package com.fly.apkencryptor.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.fly.apkencryptor.application.MyApp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RecoveryPMS {

    public RecoveryPMS(){
        recoverPMS(MyApp.getContext(),getIPackageManager());
    }

    public static void recoverPMS(Context ct, Object ipm) {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod =
                    activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            //1. 获取全局的ActivityThread对象

            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);

            /*通过置空系统IPM来迫使系统产生新的IPM对象,也是一种方法,但是这种方法也可以被HOOK.
              IPackageManager$Stub.asInterface方法里存在Hook点,故不采用此方法.

             sPackageManagerField.set(currentActivityThread, null);
             //2. 将sPackageManager置空,让后面反射获取IPackageManager时系统再产生
             //新的IPackageManager

             Method getPackageManagerMethod =
             activityThreadClass.getDeclaredMethod("getPackageManager");
             getPackageManagerMethod.setAccessible(true);
             Object originalIPackageManager = getPackageManagerMethod.invoke(null);
             //3. 获取到的新的IPackageManager,此IPackageManager没有被Hook

             */

            sPackageManagerField.set(currentActivityThread, ipm);
            //4. 替换掉ActivityThread里面的被Hook过的 sPackageManager 字段

            PackageManager pm = ct.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, ipm);
            //5. 替换ApplicationPackageManager里面的被Hook过的mPM对象
        } catch (Exception e) {
            Log.d("ysh", "recovery pms error:" + Log.getStackTraceString(e));
        }
    }

    public static Object getIPackageManager() {
        try {
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod =
                    serviceManagerClass.getDeclaredMethod("getService", String.class);
            getServiceMethod.setAccessible(true);
            Object iBinder = getServiceMethod.invoke(null, new String[]{"package"});

            Class<?> iPackageManager$Stub$ProxyClass = Class.forName("android.content.pm.IPackageManager$Stub$Proxy");
            Constructor constructor = iPackageManager$Stub$ProxyClass.getDeclaredConstructor(IBinder.class);
            constructor.setAccessible(true);
            Object iPackageManager$Stub$Proxy = constructor.newInstance(new Object[]{iBinder});
            return iPackageManager$Stub$Proxy;
        } catch (Exception e) {
            Log.d("ysh", "get IPackageManager error:" + Log.getStackTraceString(e));
        }
        return null;
    }



}
