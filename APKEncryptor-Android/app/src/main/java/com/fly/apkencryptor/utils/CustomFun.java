package com.fly.apkencryptor.utils;

import android.os.Process;

import java.io.IOException;

public class CustomFun {

    public static void exit(){

        String exitCmd = "kill " + Process.myPid();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(exitCmd);
        } catch (IOException ignored) {
        }

    }

}
