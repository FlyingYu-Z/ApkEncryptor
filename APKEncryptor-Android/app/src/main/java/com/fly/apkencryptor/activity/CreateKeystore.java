package com.fly.apkencryptor.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.base.BaseActivity;
import com.fly.apkencryptor.dialog.SelectFile;
import com.fly.apkencryptor.utils.alert;

import net.fornwall.apksigner.CertCreator;
import net.fornwall.apksigner.KeySet;
import net.fornwall.apksigner.KeyStoreFileManager;

import org.spongycastle.jce.X509Principal;
import org.spongycastle.x509.X509V3CertificateGenerator;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CreateKeystore extends BaseActivity {

    AppCompatEditText ed_path;
    AppCompatEditText ed_storePass;
    AppCompatEditText ed_alias;
    AppCompatEditText ed_keyPass;
    AppCompatEditText ed_certValidityYears;
    AppCompatEditText ed_certSignatureAlgorithm;
    AppCompatEditText ed_FullName;
    AppCompatEditText ed_Organization;
    AppCompatEditText ed_OrganizationalUnit;
    AppCompatEditText ed_country;

    private void init(){
        ed_path=find(R.id.activity_create_keystore_AppCompatEditText_filePath);
        ed_storePass=find(R.id.activity_create_keystore_AppCompatEditText_storePass);
        ed_alias=find(R.id.activity_create_keystore_AppCompatEditText_alias);
        ed_keyPass=find(R.id.activity_create_keystore_AppCompatEditText_keyPass);
        ed_certValidityYears=find(R.id.activity_create_keystore_AppCompatEditText_certValidityYears);
        ed_certSignatureAlgorithm=find(R.id.activity_create_keystore_AppCompatEditText_certSignatureAlgorithm);
        ed_FullName=find(R.id.activity_create_keystore_AppCompatEditText_FullName);
        ed_Organization=find(R.id.activity_create_keystore_AppCompatEditText_Organization);
        ed_OrganizationalUnit=find(R.id.activity_create_keystore_AppCompatEditText_OrganizationalUnit);
        ed_country=find(R.id.activity_create_keystore_AppCompatEditText_country);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_keystore);
        init();
        this.Title=getString(R.string.create_keystore);



        ed_path.setFocusable(false);
        ed_path.setFocusableInTouchMode(false);
        ed_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SelectFile(context, true,"jks", new SelectFile.SelectFileCallBack() {
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

    }


    private void cresteJKS(){

        String storePath=ed_path.getText().toString();
        char[] storePass = ed_storePass.getText().toString().toCharArray();
        String alias=ed_alias.getText().toString();
        char[] keyPass = ed_keyPass.getText().toString().toCharArray();
        int certValidityYears=Integer.parseInt(ed_certValidityYears.getText().toString());
        String certSignatureAlgorithm=ed_certSignatureAlgorithm.getText().toString();
        String FullName=ed_FullName.getText().toString();
        String Organization=ed_Organization.getText().toString();
        String OrganizationalUnit=ed_OrganizationalUnit.getText().toString();
        String Country=ed_country.getText().toString();


        try {

            CertCreator.DistinguishedNameValues distinguishedNameValues = new CertCreator.DistinguishedNameValues();
            distinguishedNameValues.setCommonName(FullName);
            distinguishedNameValues.setOrganization(Organization);
            distinguishedNameValues.setOrganizationalUnit(OrganizationalUnit);
            distinguishedNameValues.setCountry(Country);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair KPair = keyPairGenerator.generateKeyPair();

            X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
            X509Principal principal = distinguishedNameValues.getPrincipal();

            // generate a positive serial number
            BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().nextInt());
            while (serialNumber.compareTo(BigInteger.ZERO) < 0)
                serialNumber = BigInteger.valueOf(new SecureRandom().nextInt());
            v3CertGen.setSerialNumber(serialNumber);
            v3CertGen.setIssuerDN(principal);
            v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 30L));
            v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 366L * certValidityYears)));
            v3CertGen.setSubjectDN(principal);
            v3CertGen.setPublicKey(KPair.getPublic());
            v3CertGen.setSignatureAlgorithm(certSignatureAlgorithm);

            X509Certificate PKCertificate = v3CertGen.generate(KPair.getPrivate(), KeyStoreFileManager.SECURITY_PROVIDER.getName());
            KeySet keySet= new KeySet(PKCertificate, KPair.getPrivate(), null);


            KeyStore privateKS = KeyStoreFileManager.createKeyStore(storePass);
            privateKS.setKeyEntry(alias, keySet.privateKey, keyPass, new java.security.cert.Certificate[] { keySet.publicKey });

            File sfile = new File(storePath);
            if (sfile.exists()) {
                throw new IOException("File already exists: " + storePath);
            }
            KeyStoreFileManager.writeKeyStore(privateKS, storePath, storePass);

            new alert(context,getString(R.string.create_successfully));
        } catch (Exception e) {
            new alert(context,e.toString());
        }

    }

    public void onclick_create(View view) {
        cresteJKS();
    }
}
