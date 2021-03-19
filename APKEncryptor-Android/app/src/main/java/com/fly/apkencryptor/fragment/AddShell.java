package com.fly.apkencryptor.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.activity.login;
import com.fly.apkencryptor.base.BaseFragment;
import com.fly.apkencryptor.dialog.SelectFile;
import com.fly.apkencryptor.task.ShellSub.ShellSubTask;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.Custom;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.SPUtils;
import com.fly.apkencryptor.utils.SelfInfo;
import com.fly.apkencryptor.utils.SocketUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.DialogLoading;
import com.fly.apkencryptor.widget.StepLoading;
import com.fly.apkencryptor.widget.ToastUtils;

import org.json.JSONObject;

import java.io.File;

public class AddShell extends BaseFragment {

    Context context;
    MainActivity activity;
    Conf conf;
    DialogLoading progress;

    LinearLayout ln_parent;
    Button myBtn;

    TextView tv_info;
    StepLoading stepLoading;

    public CheckBox cb_checkVirtual;
    public CheckBox cb_checkXposed;
    public CheckBox cb_checkRoot;
    public CheckBox cb_checkVPN;


    private boolean isPrepared;
    private boolean mHasLoadedOnce;

    boolean isRequested = false;


    public void init(){
        context=getContext();
        activity=(MainActivity)context;
        conf=new Conf(context);
        progress=new DialogLoading(context);

        ln_parent = find(R.id.fragment_add_shell_LinearLayout_parent);
        myBtn = find(R.id.fragment_add_shell_Button_select);
        tv_info = find(R.id.fragment_add_shell_TextView_info);
        stepLoading = find(R.id.fragment_add_shell_StepLoading);

        cb_checkVirtual=find(R.id.CheckBox_checkVirtual);
        cb_checkXposed=find(R.id.CheckBox_checkXposed);
        cb_checkRoot=find(R.id.CheckBox_checkRoot);
        cb_checkVPN=find(R.id.CheckBox_checkVPN);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            // 需要inflate一个布局文件 填充Fragment
            mView = inflater.inflate(R.layout.fragment_add_shell, container, false);
            init();
            isPrepared = true;
            //实现懒加载
            lazyLoad();
        }
        //缓存的mView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个mView已经有parent的错误。
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }





        GradientDrawable gd_myBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFFFFFF, 0xFFDFDC18, 0xFF158F83});
        gd_myBtn.setCornerRadius(15);
        gd_myBtn.setColor(getResources().getColor(R.color.colorPrimary));
        gd_myBtn.setStroke(8, getResources().getColor(R.color.white));
        gd_myBtn.setShape(GradientDrawable.OVAL);
        gd_myBtn.setSize(50, 50);

        myBtn.setBackground(gd_myBtn);
        ln_parent.setVisibility(View.VISIBLE);


        GradientDrawable gd_ln_parent = new GradientDrawable();
        gd_ln_parent.setCornerRadius(15);
        gd_ln_parent.setStroke(8, getResources().getColor(R.color.colorPrimary));
        ln_parent.setBackground(gd_ln_parent);


        tv_info.setText(context.getString(R.string.no_apk_file_selected));


        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeSelect();
            }
        });

        return mView;
    }

    private void beforeSelect(){
        if(!conf.getIsLogined()){
            StartActivity(login.class);
            return;
        }

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Type", Custom.TYPE_CHECK_VIP);
            jsonObject.put("Token", conf.getToken());
            jsonObject.put("Feature", 1);
            jsonObject.put("Version", SelfInfo.getVersionName(context));

            new SocketUtils(context, jsonObject, new SocketCallBack() {
                @Override
                public void onStart() {
                    progress.show();
                }

                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject backJson = new JSONObject(result);

                        if (backJson.getBoolean("result")) {
                            selectFile();
                            SPUtils.putString("by","key",backJson.getString("key"));
                            SPUtils.putString("by","pass",backJson.getString("pass"));

                        } else {
                            ToastUtils.show(backJson.getString("msg"));
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
                    progress.dismiss();
                }
            });


        }catch (Exception e){
            new alert(context,e.toString());
        }
    }


    @Override
    public void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        //填充各控件的数据
        mHasLoadedOnce = true;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.xx");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {



            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);

    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isRequested) {
        }

    }


    public void selectFile() {

        if (conf.getUseKey()) {

            if (!new File(conf.getKeyStorePath()).exists()) {
                activity.showDialog(context.getString(R.string.keystore_file_does_not_exist));
                return;
            }

        }


        new SelectFile(context, "apk", new SelectFile.SelectFileCallBack() {
            @Override
            public void onSelected(String selectedPath) {
                tv_info.setText(selectedPath);

                try {
                    start(selectedPath);
                } catch (Exception e) {
                    activity.showDialog(e.toString());
                }

            }
            @Override
            public void onCancel() {
            }
        });


    }




    public void start(String path) throws Exception {
        tv_info.setVisibility(View.GONE);

        final String items[] = {context.getString(R.string.single_dex_encryption), context.getString(R.string.multi_dex_encryption)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(context.getString(R.string.please_choose_an_encryption_method));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                switch (which) {
                    case 0:
                        ShellSubTask.singleDEX = true;
                        break;

                    case 1:
                        ShellSubTask.singleDEX = false;
                        break;
                }



                new Thread(){
                    @Override
                    public void run(){

                        File outFile=new File(new File(path).getParent()+"/"+ FileUtils.getPrefix(path)+"_encrypted.apk");
                        try {
                            new ShellSubTask(context,AddShell.this,path,outFile.getAbsolutePath(),stepLoading).start();
                        } catch (Exception e) {
                            activity.showDialog(e.toString());
                        }

                    }
                }.start();






            }
        });
        builder.create().show();


    }




}
