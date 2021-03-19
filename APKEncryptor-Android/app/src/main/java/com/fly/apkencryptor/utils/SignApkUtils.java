package com.fly.apkencryptor.utils;

import com.android.apksig.ApkSigner;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.application.MyApp;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class SignApkUtils {

    public static void sign(MainActivity activity,String Path){

        Conf conf=new Conf(MyApp.getContext());

        try {
            File tmpFile = new File(Path + ".tmp");
            new File(Path).renameTo(tmpFile);

            if (conf.getUseKey()) {

                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(new FileInputStream(conf.getKeyStorePath()), conf.getKeyStorePw().toCharArray());

                String alias =conf.getCertAlias();

                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, conf.getCertPw().toCharArray());
                X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
                ApkSigner.Builder builder = new ApkSigner.Builder(ImmutableList.of(new ApkSigner.SignerConfig.Builder("Fly", privateKey, ImmutableList.of(x509Certificate)).build()));
                builder.setInputApk(tmpFile);
                builder.setOutputApk(new File(Path));
                builder.setCreatedBy("Fly");
                builder.setMinSdkVersion(9);
                builder.setV1SigningEnabled(true);
                builder.setV2SigningEnabled(false);
                builder.build().sign();


            } else {


                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(BYProtectUtils.getStreamFromAssets("fly.jks"), "123456".toCharArray());

                String alias ="test";

                PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, "123456".toCharArray());
                X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
                ApkSigner.Builder builder = new ApkSigner.Builder(ImmutableList.of(new ApkSigner.SignerConfig.Builder("Fly", privateKey, ImmutableList.of(x509Certificate)).build()));
                builder.setInputApk(tmpFile);
                builder.setOutputApk(new File(Path));
                builder.setCreatedBy("Fly");
                builder.setMinSdkVersion(9);
                builder.setV1SigningEnabled(true);
                builder.setV2SigningEnabled(false);
                builder.build().sign();


            }

            tmpFile.delete();

        } catch (Exception e) {
            activity.showDialog(e.toString());
        }

    }

}
