package com.beingyi.tools;

import com.beingyi.encrypt.DexStringEncryptor;
import com.beingyi.tasks.EncryptApkEncryptor;
import com.beingyi.tasks.WriteOpenSourceList;
import com.beingyi.tools.utils.ByteEncoder;
import com.beingyi.tools.utils.FileUtils;
import com.beingyi.tools.utils.MD5;
import com.beingyi.tools.utils.ZipOut;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

public class Main {


    public static void main(String[] args) {
        Main main=new Main();
        main.start();

    }


    File apkFile=new File("E:\\MyData\\AllProjects\\MessyProjects\\APKEncryptor\\SubApplication\\app\\build\\outputs\\apk\\debug\\app-debug.apk");
    File assetsDir=new File("E:\\MyData\\AllProjects\\MessyProjects\\APKEncryptor\\APKEncryptor-Android\\app\\src\\main\\assets\\");
    File assetsEnDir=new File("E:\\MyData\\AllProjects\\MessyProjects\\APKEncryptor\\APKEncryptor-Android\\app\\src\\main\\assets_en\\");

    public void start(){

        //FileUtils.copyFile(apkFile.getAbsolutePath(),assetsDir+"\\sub.apk");
        try {
            new WriteOpenSourceList(assetsDir);
            dealConf();
            ZipFile zipFile=new ZipFile(apkFile);
            ZipOut zipOut=new ZipOut(assetsDir+"\\sub.apk").setInput(zipFile);

            DexStringEncryptor encryptor = new DexStringEncryptor(FileUtils.toByteArray(zipFile.getInputStream(zipFile.getEntry("classes.dex"))),null);
            zipOut.addFile("classes.dex",encryptor.getData());
            zipOut.save();
            encryptAssets();

            new EncryptApkEncryptor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dealConf()throws Exception{
        File conf=new File(assetsDir,"conf.json");

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("SubApplication","cn.beingyi.sub.apps.SubApp.SubApplication");
        //jsonObject.put("BaseApplication","cn.beingyi.sub.apps.SubApp.SubApplication");

        FileUtils.saveFile(jsonObject.toString().getBytes(),conf.getAbsolutePath());

    }

    private void encryptAssets() throws Exception{
        String key="cn.beingyi.apkencryptor-en";
        /**
        for(File file:assetsEnDir.listFiles()){
            file.delete();
        }**/
        FileUtils.delFolder(assetsEnDir.getAbsolutePath());
        FileUtils.mkdir(assetsEnDir.getAbsolutePath());


        List<File> assetFiles=new ArrayList<>();
        listFile(assetFiles,assetsDir.getAbsolutePath());
        for(File file:assetFiles){
            if(file.isFile()) {


                String assetName = file.getAbsolutePath().replace(assetsDir.getAbsolutePath()+"\\","");
                String enName = getAssetsName(assetName);

                if(!new File(assetsEnDir.getAbsolutePath()+"\\"+enName).getParentFile().exists()){
                    FileUtils.mkdir(new File(assetsEnDir.getAbsolutePath()+"\\"+enName).getParentFile().getAbsolutePath());
                }

                System.out.println(enName);
                byte[] enBytes = ByteEncoder.Encrypt(FileUtils.read(file.getAbsolutePath()), MD5.encode(key));
                //System.err.println(MD5.encode(key));
                FileUtils.saveFile(enBytes, assetsEnDir + "\\" + enName);
            }
        }

        System.out.println("assets资源加密完成");
    }


    public static String getAssetsName(String path){

        if(path.contains("\\")) {
            String result=path;
            String[] names=path.split("\\\\");
            for(String name:names){
                String md5=MD5.encode(name);
                result=result.replace(name,md5);
            }

            return result;
        }else{
            return MD5.encode(path);
        }

    }


    private static void listFile(List<File> result,String path){

        File dir=new File(path);
        File[] files=dir.listFiles();
        for(File file:files){
            if(file.isFile()){
                result.add(file);
            }else {
                listFile(result, file.getAbsolutePath());
            }
        }
    }

}
