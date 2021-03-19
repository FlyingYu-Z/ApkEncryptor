package org.flying.keycreateor.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;


public class alert {

    public alert(final Context context, final String text) {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog dialog = new AlertDialog.Builder(context)

                        .setTitle("温馨提示")
                        .setMessage(text)
                        .setCancelable(false)

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();


            }
        });


    }


}


