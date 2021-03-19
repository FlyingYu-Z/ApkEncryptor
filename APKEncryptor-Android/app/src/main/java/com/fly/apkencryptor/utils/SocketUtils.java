package com.fly.apkencryptor.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fly.apkencryptor.Interface.SocketCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketUtils {
    Context context;
    Socket socket;
    public DataInputStream reader;
    public DataOutputStream writer;

    String result;

    public SocketUtils(Context mContext, final JSONObject json, final SocketCallBack callBack) throws JSONException {
        this.context = mContext;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String language=preferences.getString("pre_settings_language","");
        json.put("language",language);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callBack.onStart();
            }
        });
        new Thread() {
            @Override
            public void run() {

                try {

                    String socketAddress =Conf.getDomain();
                    InetAddress netAddress = InetAddress.getByName(socketAddress);


                    socket = new Socket();
                    socket.connect(new InetSocketAddress(netAddress.getHostAddress(), Conf.getPort()), 10000);
                    //socket.setSoTimeout(3 * 1000);
                    reader = new DataInputStream(socket.getInputStream());
                    writer = new DataOutputStream(socket.getOutputStream());

                    String key=MD5.encode16(TimeUtils.getCurrentTime()).toLowerCase();
                    String pkg=APKUtils.getPkgName(context,SelfInfo.getApkPath(context));
                    String data=BES.encode(json.toString(),key);
                    writer.writeInt(1);
                    writer.writeUTF(BES.encode(data+key,MD5.encode16(SPUtils.getString("self","key"))));
                    writer.flush();

                    result = decode(reader.readUTF());
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(result);
                        }
                    });

                    writer.close();
                    reader.close();
                    socket.close();

                } catch (final Exception e) {
                    if(e.getClass().equals(SocketTimeoutException.class)){

                    }else {
                        e.printStackTrace();
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure(e.toString());
                            }
                        });
                    }
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFinished();
                    }
                });
            }
        }.start();

    }


    private static String decode(String data){
        String key=data.substring(data.length()-16);
        String src=data.substring(0,data.length()-16);
        return BES.decode(src,key);
    }


}
