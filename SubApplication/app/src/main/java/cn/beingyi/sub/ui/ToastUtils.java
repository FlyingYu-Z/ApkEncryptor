package cn.beingyi.sub.ui;

import android.content.Context;
import android.widget.Toast;



public class ToastUtils {

    public static void show(Context context,String msg) {
        show(context,msg, Toast.LENGTH_LONG);
    }


    private static void show(Context context,String message, int show_length) {
        Toast.makeText(context,message,show_length).show();
    }


}

