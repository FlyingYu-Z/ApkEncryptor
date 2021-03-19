package com.fly.apkencryptor.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.utils.Conf;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;

import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import androidx.appcompat.app.AlertDialog;

public class SetKeyStore {


    Context context;
    Conf conf;

    public SetKeyStore(Context mContext) {
        this.context = mContext;
        this.conf=new Conf(context);

        TextView title = new TextView(context);
        title.setPadding(0, 10, 0, 0);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTextColor(Color.BLACK);
        title.setText(context.getString(R.string.set_the_signature_private_key));

        View view = LinearLayout.inflate(context, R.layout.dialog_keyinfo, null);

        EditText ed_path=view.findViewById(R.id.dialog_keyinfo_path);
        EditText ed_keystorePw=view.findViewById(R.id.dialog_keyinfo_keystorePw);
        EditText ed_certAlias=view.findViewById(R.id.dialog_keyinfo_certAlias);
        EditText ed_certPw=view.findViewById(R.id.dialog_keyinfo_certPw);

        ed_path.setText(conf.getKeyStorePath());
        ed_keystorePw.setText(conf.getKeyStorePw());
        ed_certAlias.setText(conf.getCertAlias());
        ed_certPw.setText(conf.getCertPw());

        AlertDialog dialog = new AlertDialog.Builder(context)

                .setCustomTitle(title)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(context.getString(R.string.ok), null)
                .create();
        dialog.show();


        ed_path.setFocusable(false);
        ed_path.setFocusableInTouchMode(false);
        ed_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SelectFile(context, "jks", new SelectFile.SelectFileCallBack() {
                    @Override
                    public void onSelected(String selectedPath) {
                        ed_path.setText(selectedPath);
                    }

                    @Override
                    public void onCancel() {
                    }
                });

            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path=new File(ed_path.getText().toString());
                if(!path.exists()){
                    ToastUtils.show(context.getString(R.string.keystore_file_does_not_exist));
                }else{
                    conf.setKeyStorePath(path.getAbsolutePath());
                    conf.setKeyStorePw(ed_keystorePw.getText().toString());
                    conf.setCertAlias(ed_certAlias.getText().toString());
                    conf.setCertPw(ed_certPw.getText().toString());

                    try {

                        KeyStore keyStore = net.fornwall.apksigner.KeyStoreFileManager.loadKeyStore(conf.getKeyStorePath(),null);
                        String alias = conf.getCertAlias();
                        X509Certificate publicKey = (X509Certificate) keyStore.getCertificate(alias);
                        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, conf.getCertPw().toCharArray());

                        ToastUtils.show(context.getString(R.string.set_up_successfully));
                        dialog.dismiss();
                    } catch (Exception e) {
                        new alert(context,e.toString());
                    }


                }

            }
        });


    }


}
