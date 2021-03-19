package com.fly.apkencryptor.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.activity.login;
import com.fly.apkencryptor.base.BaseFragment;
import com.fly.apkencryptor.dialog.SelectFile;
import com.fly.apkencryptor.task.EncryptResource.EncryptResourceTask;
import com.fly.apkencryptor.utils.APKUtils;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.Custom;
import com.fly.apkencryptor.utils.ExceptionUtils;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.SPUtils;
import com.fly.apkencryptor.utils.SelfInfo;
import com.fly.apkencryptor.utils.SocketUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.DialogLoading;
import com.fly.apkencryptor.widget.ToastUtils;

import org.json.JSONObject;

import java.io.File;

public class EnRes extends BaseFragment {
    Context context;
    MainActivity activity;
    Conf conf;
    DialogLoading progress;


    String Path;

    public ProgressBar progressBar;
    public TextView tv_label;
    LinearLayout ln_parent;
    public ScrollView sc_log;
    public TextView tv_log;
    Button btn_select;

    private boolean isPrepared;
    private boolean mHasLoadedOnce;

    boolean isRequested = false;


    public void init() {
        context = getContext();
        activity = (MainActivity) context;
        conf = new Conf(context);
        progress = new DialogLoading(context);

        progressBar = find(R.id.fragment_encrypt_resources_ProgressBar);
        tv_label = find(R.id.fragment_encrypt_resources_TextView_label);
        ln_parent=find(R.id.fragment_encrypt_resources_LinearLayout_parent);
        sc_log = find(R.id.fragment_encrypt_resources_ScrollView_log);
        tv_log = find(R.id.fragment_encrypt_resources_TextView_log);
        btn_select = find(R.id.fragment_encrypt_resources_Button_select);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            // 需要inflate一个布局文件 填充Fragment
            mView = inflater.inflate(R.layout.fragment_encrypt_resources, container, false);
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

        progressBar.setVisibility(View.INVISIBLE);
        tv_label.setVisibility(View.INVISIBLE);
        greenBtn();

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag= String.valueOf((int) btn_select.getTag());
                if(tag.equals("1")) {
                    beforeSelect();
                }

                if(tag.equals("2")){
                    start();
                }

            }
        });


        GradientDrawable gd_ln_parent = new GradientDrawable();
        gd_ln_parent.setCornerRadius(15);
        gd_ln_parent.setStroke(8, getResources().getColor(R.color.colorPrimary));
        ln_parent.setBackground(gd_ln_parent);

        tv_log.append(getString(R.string.log)+":");

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
            jsonObject.put("Feature", 3);
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

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isRequested) {
        }

    }

    public void greenBtn() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                GradientDrawable gd_myBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFFFFFF, 0xFFDFDC18, 0xFF158F83});
                gd_myBtn.setCornerRadius(15);
                gd_myBtn.setColor(getResources().getColor(R.color.colorPrimary));
                gd_myBtn.setStroke(8, getResources().getColor(R.color.white));
                gd_myBtn.setShape(GradientDrawable.OVAL);
                gd_myBtn.setSize(50, 50);
                btn_select.setBackground(gd_myBtn);
                btn_select.setText(context.getString(R.string.choose_apk));
                btn_select.setTag(1);
                progressBar.setVisibility(View.INVISIBLE);
                tv_label.setVisibility(View.INVISIBLE);
                btn_select.setClickable(true);
            }
        });
    }


    public void redBtn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                GradientDrawable gd_myBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFFFFFF, 0xFFDFDC18, 0xFF158F83});
                gd_myBtn.setCornerRadius(15);
                gd_myBtn.setColor(Color.RED);
                gd_myBtn.setStroke(8, getResources().getColor(R.color.white));
                gd_myBtn.setShape(GradientDrawable.OVAL);
                gd_myBtn.setSize(50, 50);
                btn_select.setBackground(gd_myBtn);
                btn_select.setText(context.getString(R.string.start_encrypting));
                btn_select.setTag(2);
            }
        });
    }

    //运行中
    public void blueBtn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                GradientDrawable gd_myBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFFFFFF, 0xFFDFDC18, 0xFF158F83});
                gd_myBtn.setCornerRadius(15);
                gd_myBtn.setColor(Color.BLUE);
                gd_myBtn.setStroke(8, getResources().getColor(R.color.white));
                gd_myBtn.setShape(GradientDrawable.OVAL);
                gd_myBtn.setSize(50, 50);
                btn_select.setBackground(gd_myBtn);
                btn_select.setText(context.getString(R.string.start_encrypting));
                btn_select.setTag(3);
                btn_select.setClickable(false);


                progressBar.setVisibility(View.VISIBLE);
                tv_label.setVisibility(View.VISIBLE);
            }
        });
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
                if (!APKUtils.isValid(context, selectedPath)) {
                    new alert(context, context.getString(R.string.the_apk_file_had_broken));
                    return;
                }
                Path = selectedPath;
                redBtn();
            }

            @Override
            public void onCancel() {
            }
        });


    }




    public void start(){
        new Thread(){
            @Override
            public void run(){
                blueBtn();
                try {
                    File outFile = new File(new File(Path).getParent() + "/" + FileUtils.getPrefix(Path) + "_enRes.apk");
                    EncryptResourceTask task=new EncryptResourceTask(EnRes.this,Path,outFile.getAbsolutePath());
                    task.save(activity);
                    greenBtn();
                }catch (Exception e){
                    activity.showDialog(ExceptionUtils.getExceptionDetail(e));
                }

            }
        }.start();

    }



}
