package com.fly.apkencryptor.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;
import com.fly.apkencryptor.fragment.settings;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.widget.DialogLoading;

public class Settings extends BaseActivity{


    Context context;
    Conf conf;
    DialogLoading progress;


    public void init() {
        this.context = this;
        this.conf = new Conf(context);
        progress = new DialogLoading(context, R.style.CustomDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        this.Title=getString(R.string.settings);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        settings fragment = new settings();
        transaction.add(R.id.activity_settings_FrameLayout, fragment);
        transaction.commit();


    }




}
