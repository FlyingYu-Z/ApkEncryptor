package com.fly.apkencryptor.utils;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.fly.apkencryptor.R;

public class alert {

    public alert(final Context context, final String text) {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog dialog = new AlertDialog.Builder(context)

                        .setTitle(context.getString(R.string.tips))
                        .setMessage(text)
                        .setCancelable(false)

                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
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


