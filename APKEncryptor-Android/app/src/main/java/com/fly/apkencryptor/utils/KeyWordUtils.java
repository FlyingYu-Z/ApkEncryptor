package com.fly.apkencryptor.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyWordUtils {


    public static void setHighlight(TextView tv,String str ,String key){

        //tv.setText(str);
        Editable edit=tv.getEditableText();
        SpannableString s = new SpannableString(str);
        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //edit.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(s);

    }

}
