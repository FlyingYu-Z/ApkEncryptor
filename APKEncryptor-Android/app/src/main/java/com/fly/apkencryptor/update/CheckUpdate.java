package com.fly.apkencryptor.update;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;


import com.fly.apkencryptor.R;
import com.fly.apkencryptor.utils.APKUtils;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.CustomFun;
import com.fly.apkencryptor.utils.SelfInfo;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class CheckUpdate {
    Context context;
    Conf conf;
    boolean isTip;
    String server_url;
    private final String TAG = this.getClass().getName();
    private final int UPDATA_NONEED = 0;
    private final int UPDATA_CLIENT = 1;
    private final int GET_UNDATAINFO_ERROR = 2;
    private final int SDCARD_NOMOUNTED = 3;
    private final int DOWN_ERROR = 4;
    private UpdateInfo info;
    private String localVersion;


    public CheckUpdate(Context context, boolean isTip) {

        this.context = context;
        this.isTip = isTip;
        conf = new Conf(context);
        server_url = conf.getURL() + "apk_encryptor_play.xml";

        try {
            localVersion = SelfInfo.getVersionName(context);
            CheckVersionTask cv = new CheckVersionTask();
            new Thread(cv).start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public class CheckVersionTask implements Runnable {
        InputStream is;

        public void run() {
            try {
                URL url = new URL(server_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    is = conn.getInputStream();
                }
                info = UpdateInfoParser.getUpdateInfo(is);

                String local = localVersion;
                String webversion = info.getVersion();

                if (Double.parseDouble(webversion) <= Double.parseDouble(local)) {
                    Message msg = new Message();
                    msg.what = UPDATA_NONEED;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = UPDATA_CLIENT;
                    handler.sendMessage(msg);


                }
            } catch (Exception e) {


                Message msg = new Message();
                msg.what = GET_UNDATAINFO_ERROR;
                handler.sendMessage(msg);
                e.printStackTrace();


            }
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
// TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {


                case UPDATA_NONEED:
                    if (isTip) {
                        ToastUtils.show(context, R.string.already_the_latest_version);
                    }
                    break;

                case UPDATA_CLIENT:
                    showUpdataDialog();
                    break;

                case GET_UNDATAINFO_ERROR:
                    if (isTip) {
                        ToastUtils.show(context, R.string.network_connection_failed);
                    }
                    break;

                case DOWN_ERROR:
                    ToastUtils.show(context, R.string.download_failed);

                    break;
            }
        }
    };


    protected void showUpdataDialog() {

        AlertDialog dialog = new AlertDialog.Builder(context)

                .setTitle(context.getString(R.string.version_update_prompt))
                .setMessage(info.getDescription())
                .setCancelable(!info.getForce())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(context.getString(R.string.update), null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getUrl().endsWith(".apk")) {
                    downLoadApk();
                } else {
                    Uri uri = Uri.parse(info.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
                if (!info.getForce()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info.getForce()) {
                    CustomFun.exit();
                } else {
                    dialog.dismiss();
                }
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (info.getForce()) {
                    CustomFun.exit();
                }
            }
        });

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.REQUEST_INSTALL_PACKAGES
        };
        ActivityCompat.requestPermissions((Activity) context, permissions, 1);


    }


    protected void downLoadApk() {
        final ProgressDialog pd;
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(context.getString(R.string.downloading_latest_version));

        pd.show();
        downAPK(info.getUrl(), pd);
    }


    public void downAPK(String url, final ProgressDialog pd) {

        final File file = new File(conf.getAppPath() + "/file/update.apk");
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(file.getAbsolutePath());
        requestParams.setAutoRename(true);
        requestParams.setProxy(Proxy.NO_PROXY);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                pd.dismiss();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }


                APKUtils.installAPK(context, file.getAbsolutePath());


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                new alert(context, context.getString(R.string.download_failed) + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                new alert(context, context.getString(R.string.download_failed));
            }

            @Override
            public void onFinished() {
                pd.dismiss();
            }

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                // 当前的下载进度和文件总大小
                //Log.i(tag, "正在下载中......");
                pd.setMax((int) total);
                pd.setProgress((int) current);
            }
        });

    }


}
