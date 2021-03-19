package org.flying.keycreateor.ui;

import android.content.Context;
import android.widget.Toast;

import org.flying.keycreateor.MyApp;


public class ToastUtils {

    public static void show(String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    public static void show(int msg) {
        show(MyApp.getContext().getString(msg), Toast.LENGTH_SHORT);
    }

    public static void showLong(String msg) {
        show(msg, Toast.LENGTH_LONG);
    }


    private static void show(String message, int show_length) {
        Context context = MyApp.getContext();
        Toast.makeText(context,message,show_length).show();
    }


}

