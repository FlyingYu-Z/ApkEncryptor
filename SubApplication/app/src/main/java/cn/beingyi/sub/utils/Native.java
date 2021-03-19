package cn.beingyi.sub.utils;

import android.content.Context;

public class Native {


    static {
        System.loadLibrary("FlySub");
    }

    public static native String getHead(Context context,String classLoader);

    public static native String getStringKey(Context context);

    public static native String getKey(Context context);

}
