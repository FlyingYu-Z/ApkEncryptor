package com.fly.apkencryptor.utils;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.widget.ToastUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class Conf {

    Context context;

    public Conf(Context context){
        this.context=context;
        //checkPermission();



        int sig=SelfInfo.getSign(context);
        if(sig!=-821346971){
            warn();
        }

        //if(!BYProtectUtils.readAssetsTxt("self.key").equals(getApkSign.getSignatures(SelfInfo.getApkPath(context)))){
        //    warn();
        //}



    }


    public boolean getIsLogined(){
        return SPUtils.getBoolean("conf","islogined");
    }

    public void setIsLogined(boolean value){
        SPUtils.putBoolean("conf","islogined",value);
    }

    public String getToken(){
        return SPUtils.getString("conf","Token");
    }

    public void setToken(String value){
        SPUtils.putString("conf","Token",value);
    }


    public String getUserID(){
        return SPUtils.getString("conf","UserID");
    }

    public void setUserID(String value){
        SPUtils.putString("conf","UserID",value);
    }

    public String getAccount(){
        return SPUtils.getString("conf","Account");
    }

    public void setAccount(String value){
        SPUtils.putString("conf","Account",value);
    }


    public String getPassword(){
        return SPUtils.getString("conf","Password");
    }

    public void setPassword(String value){
        SPUtils.putString("conf","Password",value);
    }


    public String getLoginKey(){
        return SPUtils.getString("conf","LoginKey");
    }

    public void setLoginKey(String value){
        SPUtils.putString("conf","LoginKey",value);
    }


    public static String getDomain() {
        return "apkencryptor.beingyi.cn";
    }

    public static int getPort() {
        return 6666;
    }

    public void warn(){


        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(context.getString(R.string.warn))
                .setMessage("Error signature："+SelfInfo.getSign(context))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        System.exit(0);
                    }
                })
                .create();
        dialog.show();


    }


    public static File getAppPath(){
        File file=new File(FileUtils.getSDPath()+"/APK Encryptor/");

        if(!file.exists()){
            file.mkdirs();
        }

        if(!new File(file+"/file").exists()){
            new File(file+"/file").mkdirs();
        }
        if(!new File(file+"/tmp").exists()){
            new File(file+"/tmp").mkdirs();
        }



        return file;
    }



    public void setCuPath(String path) {
        SPUtils.putString( "conf", "path", DesPath(path));
    }


    public String getCuPath() {
        String path = SPUtils.getString( "conf", "path");

        if (path.equals("") || !new File(path).exists() || !new File(path).canWrite()) {
            return DesPath(FileUtils.getSDPath());
        }

        return DesPath(path);
    }


    //修饰路径，如果没有以/结尾，则自动补充
    public String DesPath(String path) {
        if (path.endsWith("/")) {
            return path;
        } else {
            return path + "/";
        }

    }



    public static String getKEY(){
        return "zy4f5da22ad5f4yz";//#
    }



    public boolean getUseKey(){
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(context);
        return shp.getBoolean("useKey", false);
    }

    public void setKeyStorePath(String value){
        SPUtils.putString("conf","keystore_path",value);
    }
    public String getKeyStorePath(){
        return SPUtils.getString("conf","keystore_path");
    }


    public void setKeyStorePw(String value){
        SPUtils.putString("conf","keystore_keystorePw",value);
    }
    public String getKeyStorePw(){
        return SPUtils.getString("conf","keystore_keystorePw");
    }


    public void setCertAlias(String value){
        SPUtils.putString("conf","keystore_certAlias",value);
    }
    public String getCertAlias(){
        return SPUtils.getString("conf","keystore_certAlias");
    }


    public void setCertPw(String value){
        SPUtils.putString("conf","keystore_certPw",value);
    }
    public String getCertPw(){
        return SPUtils.getString("conf","keystore_certPw");
    }





    public void setClipBoardText(String str)
    {

        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText(null, str));

        }
    }

    public String getClipBoardText()
    {
        String result="";
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            if (clipboard.hasPrimaryClip()) {
                result=clipboard.getPrimaryClip().getItemAt(0).getText().toString();
            }
        }

        return result;
    }


    public void checkPermission(){


        String[] Permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
        };


        AndPermission.with(context)
                .runtime()
                .permission(Permissions)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {

                    }
                }
                )
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if(permissions.contains(Permissions[0])){
                            ToastUtils.show("权限被拒绝");
                            System.exit(0);
                        }

                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {

                        }
                    }
                }
                )
                .start();


    }





    public void checkProxy(){
        if(isVpnUsed()||isWifiProxy(context)){
            System.exit(0);
            return;
        }

    }



    /**
     * 是否使用代理(WiFi状态下的,避免被抓包)
     */
    public boolean isWifiProxy(Context context){
        final boolean is_ics_or_later = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (is_ics_or_later) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portstr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portstr != null ? portstr : "-1"));
            System.out.println(proxyAddress + "~");
            System.out.println("port = " + proxyPort);
        }else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
            Log.e("address = ", proxyAddress + "~");
            Log.e("port = ", proxyPort + "~");
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }
    /**
     * 是否正在使用VPN
     */
    public boolean isVpnUsed() {
        try {
            Enumeration niList = NetworkInterface.getNetworkInterfaces();
            if(niList != null) {
                for (Object obj : Collections.list(niList)) {
                    NetworkInterface intf=(NetworkInterface)obj;
                    if(!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    Log.d("-----", "isVpnUsed() NetworkInterface Name: " + intf.getName());
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())){
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getURL(){
        return "http://apkencryptor.beingyi.cn/";
    }


}
