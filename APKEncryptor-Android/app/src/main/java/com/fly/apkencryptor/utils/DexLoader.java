package com.fly.apkencryptor.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class DexLoader {


    Context context;

    public DexLoader(Context mContext){
        this.context=mContext;

    }


    public void load(InputStream is,String name,String method) {
        try {

            File dynamic=new File(context.getFilesDir() + File.separator + "dynamic" +name+ File.separator + "dynamic.dex");
            if(!dynamic.getParentFile().exists()){
                dynamic.getParentFile().mkdirs();
            }


            byte[] buffer = new byte[is.available()];
            is.read(buffer);

            OutputStream outStream = new FileOutputStream(dynamic);
            outStream.write(buffer);

            DexClassLoader dexClassLoader = new DexClassLoader(dynamic.getAbsolutePath(), dynamic.getParentFile().getAbsolutePath(), null, context.getClassLoader());
            Class clazz = dexClassLoader.loadClass(name);
            Method dexRes = clazz.getDeclaredMethod(method);
            Constructor c = clazz.getConstructor(Context.class);
            dexRes.invoke(c.newInstance(context));
            FileUtils.delFolder(dynamic.getParentFile().getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();

        }

    }







}
