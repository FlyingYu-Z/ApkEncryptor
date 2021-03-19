package com.fly.apkencryptor.utils;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;

import android.os.Message;
import android.widget.ScrollView;

public class LogUtils extends OutputStream implements Runnable {
    Activity activity;
    TextView tv_log;
    ScrollView scrollView;
    Handler handler;

    public LogUtils(Activity activity,TextView textView, ScrollView scroll) {
        this.activity=activity;
        this.tv_log = textView;
        this.scrollView = scroll;
        handler = new Handler();
    }

    @Override
    public void write(int p1) throws IOException {
        handler.sendEmptyMessage(p1);
        handler.post(this);
    }

    @Override
    public void run() {
        //scrollView.scrollTo(0, tv_log.getHeight()+30);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String s = new String(b, off, len);

        Message msg = Message.obtain();
        msg.obj = s;
        handler.sendMessage(msg);
        handler.post(this);
    }


    class Handler extends android.os.Handler {

        @Override
        public void handleMessage(Message msg) {

            String str=msg.obj.toString();

            /**
            if(str.startsWith("String->")) {

                SpannableStringBuilder style = new SpannableStringBuilder(str);
                style.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                tv_log.append(style);
            }else {
                tv_log.append(str);
            }**/

            tv_log.append(str);
            scrollView.scrollTo(0, tv_log.getMeasuredHeight());
        }

    }
}

