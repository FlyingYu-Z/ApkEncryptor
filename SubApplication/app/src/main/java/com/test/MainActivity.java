package com.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import org.json.JSONObject;

import java.lang.reflect.Method;


import cn.beingyi.sub.R;
import cn.beingyi.sub.apps.BaseApplication;
import cn.beingyi.sub.apps.SubApp.SubApplication;
import cn.beingyi.sub.ui.alert;
import cn.beingyi.sub.utils.Native;

public class MainActivity extends Activity {

    {
        System.loadLibrary("FlySub");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("BaseApplication",BaseApplication.class.getName());
            jsonObject.put("SubApplication",SubApplication.class.getName());


            Class clazz = Class.forName(BaseApplication.class.getName());
            String method = "";
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                method += m.getName();
            }

            jsonObject.put("StringDecryptMethod",method);


            new alert(this,jsonObject.toString());
        }catch (Exception e){
            new alert(this,e.toString());
        }


        new alert(this, Native.getStringKey(this));
    }



    public static native void test(Context context,String str);


}
