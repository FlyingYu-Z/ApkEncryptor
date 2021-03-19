package com.fly.apkencryptor.utils;

import android.content.Context;

public class Native {

    public static native void init(Context context,String opcode);
    public static native void start(Context context);
    public static native String getKey();


}
