package com.fly.apkencryptor.fragment;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fly.apkencryptor.Interface.SocketCallBack;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.activity.login;
import com.fly.apkencryptor.base.BaseFragment;
import com.fly.apkencryptor.dialog.PkgSelector;
import com.fly.apkencryptor.dialog.SelectFile;
import com.fly.apkencryptor.dialog.TreePkgSelector;
import com.fly.apkencryptor.task.EncryptString.EncryptStringTask;
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
import java.util.ArrayList;
import java.util.List;

public class EnStr extends BaseFragment {
    Context context;
    MainActivity activity;
    Conf conf;
    DialogLoading progress;

    public ProgressBar progressBar;
    public TextView tv_label;
    LinearLayout ln_parent;
    public ScrollView sc_log;
    public TextView tv_log;

    CheckBox cb_moreSettings;
    LinearLayout ln_settings;
    EditText ed_threadCount;
    CheckBox cb_customEncrypt;

    Button btn_select;

    String Path;
    List<String> pkgs=new ArrayList<>();
    private boolean isPrepared;
    private boolean mHasLoadedOnce;

    boolean isRequested = false;


    public void init() {
        context = getContext();
        activity = (MainActivity) context;
        conf = new Conf(context);
        progress = new DialogLoading(context);

        progressBar = find(R.id.fragment_encrypt_string_ProgressBar);
        tv_label = find(R.id.fragment_encrypt_string_TextView_label);
        ln_parent = find(R.id.fragment_encrypt_string_LinearLayout_parent);
        sc_log = find(R.id.fragment_encrypt_string_ScrollView_log);
        tv_log = find(R.id.fragment_encrypt_string_TextView_log);
        cb_moreSettings=find(R.id.fragment_encrypt_string_CheckBox_moreSettings);
        ln_settings=find(R.id.fragment_encrypt_string_LinearLayout_settings);
        ed_threadCount=find(R.id.fragment_encrypt_string_EditText_threadCount);
        cb_customEncrypt=find(R.id.fragment_encrypt_string_CheckBox_customEncrypt);
        btn_select = find(R.id.fragment_encrypt_string_Button_select);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            // 需要inflate一个布局文件 填充Fragment
            mView = inflater.inflate(R.layout.fragment_encrypt_string, container, false);
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

        greenBtn();

        GradientDrawable gd_ln_parent = new GradientDrawable();
        gd_ln_parent.setCornerRadius(15);
        gd_ln_parent.setStroke(8, getResources().getColor(R.color.colorPrimary));
        ln_parent.setBackground(gd_ln_parent);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag= String.valueOf((int) btn_select.getTag());
                if(tag.equals("1")) {
                    beforeSelect();
                }

                if(tag.equals("2")){
                    startEncrypt();
                }

            }
        });


        tv_log.append(context.getString(R.string.welcome_to_APK_Encryptor) + SelfInfo.getVersionName(context) + "\n");
        tv_log.append(context.getString(R.string.string_encryption_features_in_this_version));
        tv_log.append(context.getString(R.string.encrypt_with_a_random_key));
        tv_log.append(context.getString(R.string.assign_a_method_to_each_character));

        ln_parent.setVisibility(View.VISIBLE);


        TextView tv_info = find(R.id.fragment_encrypt_string_TextView_info);

        new Thread() {
            @Override
            public void run() {

                while (true) {
                    try {

                        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
                        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        activityManager.getMemoryInfo(info);

                        //当前分配的总内存
                        long totalMemory = Runtime.getRuntime().totalMemory();
                        //剩余内存
                        long freeMemory = Runtime.getRuntime().freeMemory();

                        StringBuilder sb = new StringBuilder();
                        sb.append(context.getString(R.string.total_free_memory) + getSpaceSize(info.availMem) + "\n");
                        sb.append(context.getString(R.string.allocated_memory) + getSpaceSize(totalMemory) + "\n");
                        sb.append(context.getString(R.string.remaining_memory) + getSpaceSize(freeMemory) + "\n");

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_info.setText(sb.toString());
                            }
                        });

                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();

        cb_moreSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ln_settings.setVisibility(View.VISIBLE);
                }else {
                    ln_settings.setVisibility(View.GONE);
                }
            }
        });
        initEditText(ed_threadCount);

        cb_customEncrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {

                    new Thread() {
                        @Override
                        public void run() {
                            activity.showLoading();

                            try {

                                PkgSelector pkgSelector = new PkgSelector(context, Path);
                                pkgSelector.show(new PkgSelector.CallBack() {
                                    @Override
                                    public void onCancel() {
                                        cb_customEncrypt.setChecked(false);
                                    }

                                    @Override
                                    public void onOK(List<String> pkgs) {
                                        EnStr.this.pkgs.clear();
                                        EnStr.this.pkgs.addAll(pkgs);
                                    }
                                });
                                /**
                                TreePkgSelector treePkgSelector = new TreePkgSelector(context, Path);
                                treePkgSelector.show(new TreePkgSelector.CallBack() {
                                    @Override
                                    public void onCancel() {
                                        cb_customEncrypt.setChecked(false);
                                    }

                                    @Override
                                    public void onOK(List<String> pkgs) {
                                        EnStr.this.pkgs.clear();
                                        EnStr.this.pkgs.addAll(pkgs);
                                    }
                                });**/


                            } catch (Exception e) {
                                activity.showDialog(ExceptionUtils.getExceptionDetail(e));
                                greenBtn();
                            }

                            activity.disLoading();
                        }
                    }.start();

                }else {


                }


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
            jsonObject.put("Feature", 2);
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



    public void greenBtn(){
        GradientDrawable gd_myBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFFFFFF, 0xFFDFDC18, 0xFF158F83});
        gd_myBtn.setCornerRadius(15);
        gd_myBtn.setColor(getResources().getColor(R.color.colorPrimary));
        gd_myBtn.setStroke(8, getResources().getColor(R.color.white));
        gd_myBtn.setShape(GradientDrawable.OVAL);
        gd_myBtn.setSize(50, 50);
        btn_select.setBackground(gd_myBtn);
        btn_select.setText(context.getString(R.string.choose_apk));
        btn_select.setTag(1);
        cb_moreSettings.setVisibility(View.GONE);
        ln_settings.setVisibility(View.GONE);
        cb_customEncrypt.setChecked(false);
        btn_select.setClickable(true);

    }


    public void redBtn(){
        GradientDrawable gd_myBtn = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xFFFFFF, 0xFFDFDC18, 0xFF158F83});
        gd_myBtn.setCornerRadius(15);
        gd_myBtn.setColor(Color.RED);
        gd_myBtn.setStroke(8, getResources().getColor(R.color.white));
        gd_myBtn.setShape(GradientDrawable.OVAL);
        gd_myBtn.setSize(50, 50);
        btn_select.setBackground(gd_myBtn);
        btn_select.setText(context.getString(R.string.start_encrypting));
        btn_select.setTag(2);
        cb_moreSettings.setVisibility(View.VISIBLE);
        pkgs.clear();
    }



    public void initEditText(EditText edit) {
        String tag = String.valueOf(edit.getTag());
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SPUtils.putString("edit", tag, edit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String text = SPUtils.getString("edit", tag);
        if (text != null && !text.isEmpty()) {
            edit.setText(text);
        }else {
            SPUtils.putString("edit", tag, edit.getText().toString());
        }


    }



    public static String getSpaceSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
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

    public void startEncrypt(){

        String selectedPath=this.Path;

        if(cb_moreSettings.isChecked()) {
            try {
                if (Integer.parseInt(ed_threadCount.getText().toString()) > 32 || Integer.parseInt(ed_threadCount.getText().toString()) == 0) {
                    ToastUtils.show(context.getString(R.string.the_maximum_number_of_threads_cannot_exceed_32));
                    return;
                }
            } catch (Exception e) {
                ToastUtils.show(context,R.string.please_input_the_correct_number_of_threads);
                return;
            }
        }

        new Thread() {
            @Override
            public void run() {


                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        tv_label.setVisibility(View.VISIBLE);
                        btn_select.setClickable(false);

                    }
                });

                try {
                    File outFile = new File(new File(selectedPath).getParent() + "/" + FileUtils.getPrefix(selectedPath) + "_enStr.apk");
                    EncryptStringTask task = new EncryptStringTask(context, EnStr.this, selectedPath, outFile.getAbsolutePath());

                    int threadCount=6;
                    if(cb_moreSettings.isChecked()){
                        threadCount=Integer.parseInt(ed_threadCount.getText().toString());
                    }
                    task.start(threadCount,pkgs);
                } catch (Exception e) {
                    activity.showDialog(ExceptionUtils.getExceptionDetail(e));
                    greenBtn();
                }


            }
        }.start();



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
                if(!APKUtils.isValid(context,selectedPath)){
                    new alert(context,context.getString(R.string.the_apk_file_had_broken));
                    return;
                }
                Path=selectedPath;
                redBtn();

            }

            @Override
            public void onCancel() {
            }
        });


    }


}
