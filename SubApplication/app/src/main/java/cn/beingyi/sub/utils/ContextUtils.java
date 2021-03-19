package cn.beingyi.sub.utils;

import android.app.Application;

import java.lang.reflect.Method;

public class ContextUtils {

    public static Application getApplication() {
        Application application = null;
        Class<?> activityThreadClass;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            final Method method2 = activityThreadClass.getMethod("currentActivityThread", new Class[0]);
            // 得到当前的ActivityThread对象
            Object localObject = method2.invoke(null, (Object[]) null);
            final Method method = activityThreadClass.getMethod("getApplication");
            application = (Application) method.invoke(localObject, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return application;
    }

}
