package com.fly.apkencryptor.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class warn {

    public warn(final Context context, final String text) {


        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle("警告")
                .setMessage(text)
                .setCancelable(false)

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                })
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        System.exit(0);
                    }
                })
                .create();
        dialog.show();

    }


}



