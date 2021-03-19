package com.fly.apkencryptor.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.application.MyApp;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.CustomFun;
import com.fly.apkencryptor.utils.LanguageUtil;
import com.fly.apkencryptor.utils.SPUtils;
import com.fly.apkencryptor.utils.StatusBarUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.DialogLoading;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    public Context context;
    public Activity activity;
    public Conf conf;
    public DialogLoading progress;
    public boolean isInstallFromGP=false;
    public String Title;
    private void init(){
        context = this;
        this.activity=(Activity)context;
        conf=new Conf(context);
        progress=new DialogLoading(context,R.style.CustomDialog);



    }

    /**
     * 此方法先于 onCreate()方法执行
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        //获取我们存储的语言环境 比如 "en","zh",等等
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String language=preferences.getString("pre_settings_language","");
        //attach 对应语言环境下的context
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);


        init();

        PackageManager packageManager = getPackageManager();
        String installerName = packageManager.getInstallerPackageName(getPackageName());
        if (installerName != null) {
            if (installerName.equals("com.android.vending")) {
                this.isInstallFromGP = true;
            }
        }

        SPUtils.putBoolean("conf","isPaid",isInstallFromGP);


        int modifiers=MyApp.class.getModifiers();
        if(modifiers!=17){
            CustomFun.exit();
        }


        //获取状态栏高度
        /**
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        ViewGroup viewgroup = getWindow().getDecorView().findViewById(android.R.id.content);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewgroup.getLayoutParams();
        lp.setMargins(0, result, 0, 0);
        viewgroup.setLayoutParams(lp);
         **/
        setNavigationBarColor();

    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView tv_title=((TextView)find(R.id.BaseActivity_TextView_title));
        if(tv_title!=null && this.Title!=null){
            tv_title.setText(this.Title);
        }

    }

    public void setStatusBar(){
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtils.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtils.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtils.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtils.setStatusBarColor(this,this.getResources().getColor(R.color.colorPrimary));
        }
    }


    protected <T extends View> T find(int viewId) {
        return (T) findViewById(viewId);
    }


    @TargetApi(21)
    public void setNavigationBarColor() {

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 虚拟导航键
        window.setNavigationBarColor(getResources().getColor(R.color.colorPrimary));

    }


    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public Context getContext() {
        return context;
    }


    public void StartActivity(Class<?> cls) {
        startActivity(new Intent(getContext(), cls));
    }


    public void finish(View view) {
        finish();
    }
}
