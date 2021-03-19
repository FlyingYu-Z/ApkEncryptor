package com.fly.apkencryptor.activity;

import android.os.Bundle;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;

public class Example extends BaseActivity {


    private void init(){


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        init();
        this.Title=getString(R.string.app_name);


    }




}
