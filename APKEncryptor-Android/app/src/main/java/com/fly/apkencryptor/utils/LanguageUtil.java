package com.fly.apkencryptor.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fly.apkencryptor.application.MyApp;

import java.util.Locale;

public class LanguageUtil {

    private static final String TAG="LanguageUtil";

    public static Context attachBaseContext(Context context, String language) {
        Log.d(TAG, "attachBaseContext: "+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Resources resources = context.getResources();
        Locale locale = LanguageUtil.getLocaleByLanguage(language);

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        return context.createConfigurationContext(configuration);
    }

    private static Locale getLocaleByLanguage(String language) {
        if(language.equals("zh")){
            return Locale.SIMPLIFIED_CHINESE;
        }

        if(language.equals("en")){
            return Locale.ENGLISH;
        }

        if(language.equals("ru")){
            return new Locale("ru");
        }

        if(language.equals("")){
            if(isZh(MyApp.getContext())){
                return Locale.ENGLISH;
            }else {
                return Locale.ENGLISH;
            }
        }

        return Locale.ENGLISH;
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }


    /**
     * @param context
     * @param newLanguage 想要切换的语言类型 比如 "en" ,"zh"
     */
    @SuppressWarnings("deprecation")
    public static void changeAppLanguage(Context context, String newLanguage) {
        if (TextUtils.isEmpty(newLanguage)) {
            return;
        }
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        //获取想要切换的语言类型
        Locale locale = getLocaleByLanguage(newLanguage);
        configuration.setLocale(locale);
        // updateConfiguration
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

}
