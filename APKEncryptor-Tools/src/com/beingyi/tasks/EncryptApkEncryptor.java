package com.beingyi.tasks;

import com.beingyi.encrypt.DexStringEncryptor;
import com.beingyi.tools.utils.*;
import com.bigzhao.xml2axml.tools.AxmlDecoder;
import com.bigzhao.xml2axml.tools.AxmlEncoder;
import com.google.common.collect.Lists;
import net.fornwall.apksigner.KeyStoreFileManager;
import net.fornwall.apksigner.ZipSigner;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.smali.Smali;
import org.jf.smali.SmaliOptions;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EncryptApkEncryptor {


    public List<String> dexEntries = new ArrayList<>();
    ZipFile zipFile;
    ZipOut zipOut;
    List<String> onlyEncryptClasses =new ArrayList<>();

    //混淆类
    HashMap<String, String> typeEnties = new HashMap<String, String>();

    //IntDecoderSmail intDecoderSmail=new IntDecoderSmail();

    public EncryptApkEncryptor()throws Exception{
        String path="E:\\MyData\\AllProjects\\MessyProjects\\APKEncryptor\\APKEncryptor-Android\\app\\build\\outputs\\apk\\release\\app-release.apk";
        String outPath="E:\\MyData\\AllProjects\\MessyProjects\\APKEncryptor\\APKEncryptor-Android\\app\\build\\outputs\\apk\\release\\APK Encryptor_encrypted.apk";

        FileUtils.delSingleFile(outPath);

        zipFile=new ZipFile(path);
        zipOut=new ZipOut(outPath).setInput(zipFile);

        readZip(zipFile);

        //this.onlyEncryptClasses.add("com.fly.apkencryptor");


        List<String> activities= ManifestParse.parseManifestActivity(getZipInputStream("AndroidManifest.xml"));
        for(String dexName:dexEntries){
            DexBackedDexFile dex = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), getZipInputStream(dexName));
            List<DexBackedClassDef> classDefs = Lists.newArrayList(dex.getClasses());

            for (DexBackedClassDef classDef : classDefs) {
                String type = classDef.getType();
                String pkg = DexUtils.getPkgNameByType(type);


                    if (activities.contains(pkg)) {
                        String newType = getObfusedTypeName(type);
                        typeEnties.put(type, newType);
                        System.out.println(type + "->" + newType);
                    }

            }
        }


        for(String dexName:dexEntries){
            //DexStringEncryptor encryptor = new DexStringEncryptor(FileUtils.toByteArray(zipFile.getInputStream(zipFile.getEntry(dexName))),onlyEncryptClasses);

            //zipOut.addFile("classes.dex",encryptor.getData());
            zipOut.addFile("classes.dex",confuseDex(FileUtils.toByteArray(getZipInputStream(dexName))));

        }



        String axml = AxmlDecoder.decode(FileUtils.toByteArray(getZipInputStream("AndroidManifest.xml")));
        for (String key : typeEnties.keySet()) {
            String pkg = DexUtils.getPkgNameByType(key);
            String newPkg = DexUtils.getPkgNameByType(typeEnties.get(key));
            axml = axml.replace(pkg, newPkg);
        }
        //System.out.println(axml);
        //zipOut.addFile("AndroidManifest.xml", AxmlEncoder.encode(axml));



        zipOut.save();


        System.out.println("正在签名");
        signApk(outPath);

        System.out.println("APK Encryptor加密完成");

    }


    private byte[] confuseDex(byte[] data)throws Exception{
        DexBuilder dexBuilder=new DexBuilder(Opcodes.getDefault());
        dexBuilder.setIgnoreMethodAndFieldError(true);

        DexBackedDexFile dex = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), new ByteArrayInputStream(data));
        List<DexBackedClassDef> classDefs = Lists.newArrayList(dex.getClasses());
        List<DexBackedClassDef> newClassDefs = new ArrayList<>();

        for (DexBackedClassDef classDef : classDefs) {
            System.out.println(classDef.getType());
            String smali = DexUtils.getSmali(classDef);
            String orSmali = new String(smali);

            //if(DexUtils.getSuffix(classDef.getType()).startsWith("R$")){
            //    dexBuilder.internClassDef(new ResIDEncryptor(classDef,intDecoderSmail.IntDecoder_ClassType).getClassDef());
            //    continue;
            //}

            for (String key : typeEnties.keySet()) {
                smali = smali.replace(key.substring(0, key.length() - 1), typeEnties.get(key).substring(0, typeEnties.get(key).length() - 1));
            }
            if (!smali.equals(orSmali)) {
                Smali.assembleSmaliFile(smali, dexBuilder, new SmaliOptions());
            } else {
                dexBuilder.internClassDef(classDef);
            }

        }

        //dexBuilder.internClassDef(intDecoderSmail.getClassDef());
        return DexUtils.getDexBuilderData(dexBuilder);

    }


    public void signApk(String input) throws Exception {
        String keystorePath="E:\\MyData\\KeyStore\\beingyi.jks";

        char[] storePassword = "zy3b9ac9ff".toCharArray();
        char[] keyPassword = "zy3b9ac9ff".toCharArray();

        KeyStore keyStore = KeyStoreFileManager.loadKeyStore(keystorePath, null);
        String alias = keyStore.aliases().nextElement();
        X509Certificate publicKey = (X509Certificate) keyStore.getCertificate(alias);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword);
        File tmp=new File(input+".tmp");
        tmp.delete();
        ZipSigner.signZip(publicKey, privateKey, "SHA1withRSA", input, tmp.getAbsolutePath());
        new File(input).delete();
        tmp.renameTo(new File(input));

    }



    public InputStream getZipInputStream(String entry) throws IOException {

        return new ByteArrayInputStream(FileUtils.toByteArray(zipFile.getInputStream(zipFile.getEntry(entry))));
    }


    private void readZip(ZipFile zip) throws Exception {
        Enumeration enums = zip.entries();
        while (enums.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enums.nextElement();
            String entryName = entry.getName();

            if (entryName.startsWith("classes") && entryName.endsWith(".dex")) {
                dexEntries.add(entryName);
            }

        }

    }




    private static String keys;

    static {
        try {
            keys = com.beingyi.encrypt.utils.FileUtils.readFile(new File("resource/keys.txt").getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String getObfusedTypeName(String type) {
        int length = 0;
        String[] types = type.split("/");
        if (types != null) {
            length = types.length;
        } else {
            length = 1;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("L");
        for (int i = 0; i < length; i++) {
            sb.append(getRandomLitter(3).toLowerCase() + "/");
        }
        sb.append(";");

        String result = sb.toString();


        return result.replace("/;", ";");
    }



    private String getRandomStr(int length) {
        String[] keyArray = keys.split("\n");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < length; i++) {
            int num = r.nextInt(keyArray.length);
            sb.append(keyArray[num]);
        }
        return sb.toString().replace(".", "");
    }


    private static String getRandomLitter(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }


}
