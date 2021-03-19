package com.fly.apkencryptor.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.fly.apkencryptor.application.MyApp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;

import dalvik.system.DexClassLoader;

public class LoadNetDex {

    Context context;
    Conf conf;
    String dexPath;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;

                case 1:
                    loadDex();
                    break;

                case 2:
                    break;


                case 3:

                    break;

                default:
                    break;

            }

        }

    };




    public LoadNetDex(Context context){
        this.context=context;
        this.conf=new Conf(context);

        if(conf.isVpnUsed()||conf.isWifiProxy(MyApp.getContext())){

        }else {
            start();
        }

    }


    public void start(){
        if(conf.isVpnUsed()||conf.isWifiProxy(MyApp.getContext())){
            ;
        }else {
            downloadDex("http://"+conf.getURL()+"assets/apkencryptor.data");
        }


    }



    public void loadDex(){

        File cacheFile =new File(context.getFilesDir()+ File.separator +"dynamic");
        if(!cacheFile.exists()){
            cacheFile.mkdirs();
        }

        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, cacheFile.getAbsolutePath(), null, context.getClassLoader());
        try
        {
            Class clazz = dexClassLoader.loadClass("cn.beingyi.Main");
            Constructor c=clazz.getConstructor(Context.class);
            c.newInstance(context);
            FileUtils.delFolder(cacheFile.getAbsolutePath());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Toast.makeText(context,"初始化失败", Toast.LENGTH_LONG).show();

        }




    }

    public void downloadDex(final String path){
        new Thread(){
            @Override
            public  void run(){

                URL url;
                HttpURLConnection connection;
                try {
                    url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(4000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Charset", "utf-8");
                    connection.connect();
                    String urlFilePath = connection.getURL().getFile();
                    String fileName = urlFilePath.substring(urlFilePath.lastIndexOf(File.separatorChar) + 1);
                    File file = new File(context.getFilesDir(), fileName);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        int contentLength = connection.getContentLength();

                        BufferedInputStream bfi = new BufferedInputStream(inputStream);
                        int len;
                        int totle = 0;
                        byte[] bytes = new byte[1024];
                        while ((len = bfi.read(bytes)) != -1) {
                            totle += len;
                            outputStream.write(bytes, 0, len);
                        }

                        outputStream.close();
                        inputStream.close();
                        bfi.close();

                        dexPath=file.getAbsolutePath();
                        handler.sendEmptyMessage(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        }.start();



    }




}
