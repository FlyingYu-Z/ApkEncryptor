package com.fly.apkencryptor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;

public class ErrReport extends BaseActivity {

    TextView tv_err;

    private void init(){
        tv_err=find(R.id.activity_err_report_TextView_text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_err_report);
        init();
        Intent intent=getIntent();
        String err=intent.getStringExtra("err");
        tv_err.setText(err);

    }
}
