package com.fly.apkencryptor.utils;

import android.content.Context;
import android.content.DialogInterface;

import com.fly.apkencryptor.BuildConfig;

import androidx.appcompat.app.AlertDialog;

public class Ealert
{

    public Ealert(final Context context, final String text)
    {
		if(!BuildConfig.DEBUG)return;
		AlertDialog dialog = new AlertDialog.Builder(context)

			.setTitle("温馨提示")
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





}


