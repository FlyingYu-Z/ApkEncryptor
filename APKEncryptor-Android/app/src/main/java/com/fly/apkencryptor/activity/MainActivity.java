package com.fly.apkencryptor.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.adapter.SectionsPagerAdapter;
import com.fly.apkencryptor.base.BaseActivity;
import com.fly.apkencryptor.dialog.ActivateKey;
import com.fly.apkencryptor.dialog.UserInfoDialog;
import com.fly.apkencryptor.fragment.AddShell;
import com.fly.apkencryptor.fragment.EnRes;
import com.fly.apkencryptor.fragment.EnStr;
import com.fly.apkencryptor.update.CheckUpdate;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.Custom;
import com.fly.apkencryptor.utils.Native;
import com.fly.apkencryptor.utils.RecoveryPMS;
import com.fly.apkencryptor.utils.SelfInfo;
import com.fly.apkencryptor.utils.SocketUtils;
import com.fly.apkencryptor.utils.UserInfo;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.DialogLoading;
import com.fly.apkencryptor.widget.ToastUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity  implements ViewPager.OnPageChangeListener,View.OnClickListener {


    DrawerLayout drawerLayout;
    SwipeRefreshLayout swipeRefreshLayout_nav;
    Toolbar toolbar;
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;

    LinearLayout ln_logined;
    LinearLayout ln_not_login;
    NavigationView navigationView;


    public static final int MSG_REFRESH = 1001;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    progress.show();
                    break;

                case 2:
                    progress.dismiss();
                    break;

                case 3:
                    ToastUtils.show(msg.obj.toString());
                    break;

                case 4:
                    new alert(context, msg.obj.toString());
                    break;
                case MSG_REFRESH:

                    Intent intent = new Intent("android.intent.action.UpdateUserInfo");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    if(conf.getIsLogined()){
                        ln_logined.setVisibility(View.VISIBLE);
                        ln_not_login.setVisibility(View.GONE);
                        refreshInfo();
                    }else {
                        ln_logined.setVisibility(View.GONE);
                        ln_not_login.setVisibility(View.VISIBLE);
                    }

                    break;

            }
            super.handleMessage(msg);
        }
    };


    public void init() {
        this.context = this;
        this.conf = new Conf(context);
        progress = new DialogLoading(context, R.style.CustomDialog);

        drawerLayout=find(R.id.drawer_layout);
        swipeRefreshLayout_nav=find(R.id.nav_header_main_SwipeRefreshLayout);
        toolbar = find(R.id.toolbar);

        ln_logined=find(R.id.nav_header_main_LinearLayout_logined);
        ln_not_login=find(R.id.nav_header_main_LinearLayout_not_login);

        navigationView=find(R.id.nav_header_main_NavigationView);

        find(R.id.nav_header_main_Button_more).setOnClickListener(this);
        find(R.id.nav_header_main_Button_open_vip).setOnClickListener(this);
        find(R.id.nav_header_main_Button_logout).setOnClickListener(this);

        viewPager = find(R.id.activity_main_ViewPager);
        bottomNavigationView = find(R.id.activity_main_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setItemTextAppearanceActive(R.style.bottom_selected_text);
        bottomNavigationView.setItemTextAppearanceInactive(R.style.bottom_normal_text);
        initViewPager();

    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        AddShell addShell=new AddShell();
        EnStr enStr=new EnStr();
        EnRes enRes=new EnRes();
        fragments.add(addShell);
        fragments.add(enStr);
        fragments.add(enRes);

        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(5);

        SectionsPagerAdapter mainAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mainAdapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_add_shell:
                    viewPager.setCurrentItem(0);
                    return true;

                case R.id.navigation_encrypt_string:
                    viewPager.setCurrentItem(1);
                    return true;

                case R.id.navigation_encrypt_resources:
                    viewPager.setCurrentItem(2);
                    return true;

            }
            return false;
        }
    };


    private void refreshInfo(){
        String hiddenEmail = conf.getAccount().replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
        ((TextView)find(R.id.nav_header_main_TextView_Email)).setText(hiddenEmail);

        String VIP="";

        if(UserInfo.getBoolValue("isVIP")){
            VIP=UserInfo.getStrValue("VIP");
        }else{
            VIP="Non VIP";
        }

        ((TextView)find(R.id.nav_header_main_TextView_VIP)).setText("VIP:"+VIP);

        Button btn_openVIP=find(R.id.nav_header_main_Button_open_vip);
        if(conf.getIsLogined() &&UserInfo.getBoolValue("isVIP")){
            btn_openVIP.setText(getString(R.string.renew_vip));
        }else{
            btn_openVIP.setText(getString(R.string.open_vip));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(context, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }


        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_menu);
        Drawable drawable=new BitmapDrawable(bitmap);
        toolbar.setNavigationIcon(drawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        new CheckUpdate(context, false);

        new Thread() {
            @Override
            public void run() {

                while (true) {
                    handler.sendEmptyMessage(MSG_REFRESH);
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();


        if(conf.getIsLogined()){
            login();
        }


        swipeRefreshLayout_nav.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light, android.R.color.holo_orange_light);
        swipeRefreshLayout_nav.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                getUserInfo();
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id){

                    case R.id.tools_create_keystore:
                        StartActivity(CreateKeystore.class);
                        break;

                    case R.id.menu_check_for_updates:
                        ToastUtils.show(context,R.string.checking_for_updates);
                        new CheckUpdate(context, true);
                        break;

                    case R.id.menu_about:
                        showAbout();
                        break;

                    case R.id.menu_settings:
                        StartActivity(Settings.class);
                        break;

                }
                return false;
            }
        });


        Native.start(context);


    }


    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setIntent(intent);

        boolean update = intent.getBooleanExtra("update", false);

        if(update){
            getUserInfo();
        }

        new RecoveryPMS();

    }

    public void getUserInfo() {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_GET_USERINFO);
            jsonObject.put("Token", conf.getToken());

            new SocketUtils(context, jsonObject, new SocketCallBack() {
                @Override
                public void onStart() {
                    //progress.show();
                    swipeRefreshLayout_nav.setRefreshing(true);
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject backJson = new JSONObject(result);

                        if (backJson.getBoolean("result")) {

                            UserInfo.setValue("VIP",backJson.getString("VIP"));
                            UserInfo.setValue("isLifeTimeVip",backJson.getBoolean("isLifeTimeVip"));
                            UserInfo.setValue("isForbidden",backJson.getBoolean("isForbidden"));
                            UserInfo.setValue("LoginTime",backJson.getString("LoginTime"));
                            UserInfo.setValue("SignupTime",backJson.getString("SignupTime"));
                            UserInfo.setValue("isVIP",backJson.getBoolean("isVIP"));


                        } else {
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(getString(R.string.the_network_is_busy));
                    }
                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show(getString(R.string.the_network_is_busy));
                }

                @Override
                public void onFinished() {
                    //progress.dismiss();
                    swipeRefreshLayout_nav.setRefreshing(false);
                }
            });


        }catch (Exception e){
            new alert(context,e.toString());
        }

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        bottomNavigationView.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    private void showAbout(){
        View view = View.inflate(context,R.layout.view_about,null);

        TextView tv_version=view.findViewById(R.id.view_about_version);
        TextView tv_Telegram_Group=view.findViewById(R.id.view_about_TelegramGroup);
        TextView tv_QQ_Group=view.findViewById(R.id.view_about_QQGroup);
        TextView tv_OpenSourceList=view.findViewById(R.id.view_about_OpenSourceList);


        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(context.getString(R.string.about))
                .setView(view)
                .setCancelable(true)
                .create();
        dialog.show();


        tv_version.setText(context.getString(R.string.app_name)+"_"+ SelfInfo.getVersionName(context));

        tv_Telegram_Group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl("https://t.me/apk_encryptor");
            }
        });

        tv_QQ_Group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQQGroup("ZrI2C6wb6x8HamtDnYm-5csxTPAmFV0P");
            }
        });

        tv_OpenSourceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(OpenSourceList.class);
            }
        });

    }

    public void showToast(String text) {
        Message msg = new Message();
        msg.what = 3;
        msg.obj = text;
        handler.sendMessage(msg);

    }


    public void showDialog(String text) {
        Message msg = new Message();
        msg.what = 4;
        msg.obj = text;
        handler.sendMessage(msg);

    }


    public void showLoading() {
        Message msg = new Message();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    public void disLoading() {
        Message msg = new Message();
        msg.what = 2;
        handler.sendMessage(msg);
    }


    public void joinQQGroup(String key) {

        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.QQ_not_installed), Toast.LENGTH_SHORT).show();
        }


    }



    public void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


    //开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions((Activity) context, permissions, 321);
    }

    String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.warn))
                .setMessage(getString(R.string.request_permission_tip))
                .setPositiveButton(getString(R.string.grant_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton(getString(R.string.deny_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).setCancelable(false).show();
    }


    public void onclick_enter_login(View view) {
        StartActivity(login.class);
    }




    public void logoutTip() {

        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(getString(R.string.warn))
                .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conf.setIsLogined(false);
                        conf.setToken("");
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();


    }



    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.nav_header_main_Button_more:
                new UserInfoDialog(context).show();
                break;

            case R.id.nav_header_main_Button_open_vip:
                new ActivateKey(context);
                break;

            case R.id.nav_header_main_Button_logout:
                logoutTip();
                break;
        }
    }



    public void login() {

        String Account=conf.getAccount();
        String Password=conf.getPassword();


        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_LOGIN);
            jsonObject.put("Account", Account);
            jsonObject.put("Password", Password);
            jsonObject.put("LoginKey", conf.getLoginKey());


            new SocketUtils(context, jsonObject, new SocketCallBack() {
                @Override
                public void onStart() {
                    //progress.show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject backJson = new JSONObject(result);

                        if (backJson.getBoolean("result")) {

                        } else {
                            conf.setIsLogined(false);
                            ToastUtils.show(backJson.getString("msg"));
                            StartActivity(login.class);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.show(getString(R.string.the_network_is_busy));

                    }
                }

                @Override
                public void onFailure(String error) {
                    ToastUtils.show(getString(R.string.the_network_is_busy));
                }

                @Override
                public void onFinished() {
                    //progress.dismiss();
                }
            });


        }catch (Exception e){
            new alert(context,e.toString());
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(drawerLayout.getVisibility()==View.VISIBLE){
                drawerLayout.closeDrawer(Gravity.LEFT);
            }else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
