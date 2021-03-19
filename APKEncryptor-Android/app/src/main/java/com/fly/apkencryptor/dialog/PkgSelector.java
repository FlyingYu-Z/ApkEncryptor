package com.fly.apkencryptor.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.adapter.MyAdapter;
import com.fly.apkencryptor.utils.DexUtils;
import com.fly.apkencryptor.utils.ExceptionUtils;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;
import com.google.common.collect.Lists;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class PkgSelector {

    Context context;
    Activity activity;
    String apkPath;
    ZipFile zipFile;
    List<String> dexEntries = new ArrayList<>();
    List<String> pkgs = new ArrayList<>();

    ListView listView;

    public PkgSelector(Context context,String apkPath) throws Exception {
        this.context=context;
        this.activity=(Activity)context;
        this.apkPath=apkPath;
        this.zipFile=new ZipFile(apkPath);
        start();
    }

    private void start() throws Exception {
        readZip(zipFile);

        for (String dexName:dexEntries){
            DexBackedDexFile dex=DexBackedDexFile.fromInputStream(Opcodes.getDefault(),getZipInputStream(dexName));
            List<ClassDef> classDefs= Lists.newArrayList(dex.getClasses());

            for(ClassDef classDef:classDefs){
                String pkg= DexUtils.getPkgNameByType(classDef.getType());
                pkgs.add(pkg);

            }


        }

    }

    public List<String> getPkgs(){

        List<String> result=new ArrayList<>();

        HashMap<String,String> map=new HashMap();
        for(String pkg: pkgs){
            String[] parts=pkg.split("\\.");
            if(parts.length>1){
                map.put(parts[0],null);
            }else {
                map.put(pkg,null);
            }

        }

        for(String key:map.keySet()){
            result.add(key);
        }

        return result;
    }



    public void show(CallBack callBack){

        List<PkgInfo> processedPkgs = new ArrayList<>();
        for(String name:getPkgs()){
            PkgInfo info=new PkgInfo();
            info.PkgName=name;
            processedPkgs.add(info);
        }


        MyAdapter<PkgInfo> adapter = new MyAdapter<PkgInfo>(processedPkgs, R.layout.item_pkg) {
            @Override
            public void bindView(MyAdapter.ViewHolder holder, PkgInfo obj,int position) {

                holder.setText(R.id.item_pkg_TextView_pkg,obj.PkgName);

                CheckBox cb=holder.getView(R.id.item_pkg_CheckBox);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listView.setItemChecked(position,cb.isChecked());
                    }
                });
                cb.setChecked(listView.isItemChecked(position));

                /**
                if (framelayout.isItemChecked(position)) {
                    holder.getItemView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                } else {
                    holder.getItemView().setBackgroundColor(Color.TRANSPARENT);
                }**/


            }
        };

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view=View.inflate(context,R.layout.view_pkg_selector,null);
                listView=view.findViewById(R.id.view_pkg_selector_ListView);
                listView.setAdapter(adapter);

                Button btn_cancel=view.findViewById(R.id.view_pkg_selector_Button_cancel);
                Button btn_ok=view.findViewById(R.id.view_pkg_selector_Button_ok);


                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(view)
                        .setCancelable(false)
                        .create();
                dialog.show();


                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callBack.onCancel();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            List<String> result = new ArrayList<>();
                            int count = processedPkgs.size();
                            int i = 0;
                            while (i < count) {
                                PkgInfo info=processedPkgs.get(i);
                                if (listView.isItemChecked(i)) {
                                    result.add("L" + info.PkgName);
                                }

                                i++;
                            }

                            dialog.dismiss();
                            callBack.onOK(result);
                        }catch (Exception e){
                            new alert(context, ExceptionUtils.getExceptionDetail(e));
                        }
                    }
                });

                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if(!listView.isItemChecked(position)){
                                listView.setItemChecked(position,false);
                            }else {
                                listView.setItemChecked(position,true);
                            }
                            adapter.notifyDataSetChanged();
                            ToastUtils.show("刷新");
                        }catch (Exception e){
                            new alert(context, ExceptionUtils.getExceptionDetail(e));
                        }

                    }
                });

                selectAll(processedPkgs,adapter);

            }
        });

    }

    private void selectAll(List<PkgInfo> processedPkgs,MyAdapter<PkgInfo> adapter){
        int count = processedPkgs.size();
        int i = 0;
        while (i < count) {
            listView.setItemChecked(i,true);
            i++;
        }
        adapter.notifyDataSetChanged();

    }


    public InputStream getZipInputStream(String entry) throws IOException {

        return new ByteArrayInputStream(FileUtils.toByteArray(zipFile.getInputStream(zipFile.getEntry(entry))));
    }

    private void readZip(java.util.zip.ZipFile zip) throws Exception {
        Enumeration enums = zip.entries();
        while (enums.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enums.nextElement();
            String entryName = entry.getName();

            if (entryName.startsWith("classes") && entryName.endsWith(".dex")) {
                dexEntries.add(entryName);
            }
        }

    }


    class PkgInfo{
        String PkgName;

    }



    public static interface CallBack{
        void onCancel();
        void onOK(List<String> pkgs);
    }

}
