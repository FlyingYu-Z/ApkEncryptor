package com.test;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class alert
{

    public alert(final Context context, final String text)
    {


        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog dialog = new AlertDialog.Builder(context)

                        .setMessage(text)
                        .setCancelable(false)

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();


            }
        });



    }

    
        
    }


