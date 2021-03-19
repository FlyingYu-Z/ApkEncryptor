package cn.beingyi.sub.ui;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;

public class alert
{

    public alert(final Context context, final String text)
    {


                AlertDialog dialog = new AlertDialog.Builder(context)

                        .setMessage(text)
                        .setCancelable(false)

                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                                if (clipboard != null) {
                                    clipboard.setPrimaryClip(ClipData.newPlainText(null, text));

                                }
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();


    }

    
        
    }


