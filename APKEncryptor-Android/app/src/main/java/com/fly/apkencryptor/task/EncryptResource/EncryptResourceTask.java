package com.fly.apkencryptor.task.EncryptResource;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.beingyi.confuse.ArscObfuser;
import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.fragment.EnRes;
import com.fly.apkencryptor.task.BaseTask;
import com.fly.apkencryptor.utils.APKUtils;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.SPUtils;
import com.fly.apkencryptor.utils.SignApkUtils;
import com.fly.apkencryptor.utils.ZipOut;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.zip.ZipFile;

public class EncryptResourceTask extends BaseTask {
    EnRes fragment;
    MainActivity activity;

    public EncryptResourceTask(EnRes fragment,String inputPath, String outputPath) throws Exception {
        super(inputPath, outputPath);
        this.fragment=fragment;
        this.activity= (MainActivity) fragment.getActivity();
        println(activity.getString(R.string.parsing_apk));

        ZipFile zipFile=new ZipFile(inputPath);
        ZipOut zipOut=new ZipOut(outputPath).setInput(zipFile);
        zipOut.removeFile("resources.arsc");
        ArscObfuser arscObfuser=new ArscObfuser(getZipInputStream("resources.arsc"));
        zipOut.addFile("resources.arsc",arscObfuser.getData());

        HashMap<String,String> map=arscObfuser.getMap();
        int i=0;

        for(String key:map.keySet()){
            zipOut.removeFile(key);
            zipOut.addFile(map.get(key), FileUtils.toByteArray(getZipInputStream(key)));
            //println(key+"->"+map.get(key));
            println(key);
            i++;
            int ii=i;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.progressBar.setMax(map.size());
                    fragment.progressBar.setProgress(ii);
                    fragment.tv_label.setText(key);
                }
            });


        }
        println(activity.getString(R.string.saving_file));

        zipOut.save();
        println(activity.getString(R.string.file_out_puting)+outputPath);

    }

    public void save(MainActivity activity){

        SignApkUtils.sign(activity,outputPath);
        showFinish(activity,new File(outputPath));
    }


    private void println(String str){
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment.tv_log.append(str+"\n");
                fragment.sc_log.scrollTo(0, fragment.tv_log.getMeasuredHeight()+100);
            }
        });

    }


    public static void showFinish(Context context, final File signedFile) {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SPUtils.putString("by","key","test");

                AlertDialog dialog = new AlertDialog.Builder(context)

                        .setTitle(context.getString(R.string.encrypted_successfully))
                        .setMessage(context.getString(R.string.the_output_file_is_saved_to_the_following_path) + signedFile.getAbsolutePath())
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.install), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                APKUtils.installAPK(context, signedFile.getAbsolutePath());

                            }
                        })
                        .create();
                dialog.show();


            }
        });


    }


}
