package com.beingyi;

import com.beingyi.encrypt.DexStringEncryptor;
import com.beingyi.tools.utils.FileUtils;
import com.beingyi.tools.utils.ZipOut;

import java.io.File;
import java.util.zip.ZipFile;

public class encryptAPK {

    public static void main(String[] args) {
        File assetsDir=new File("E:\\MyData\\AllProjects\\MessyProjects\\APKEncryptor\\APKEncryptor-Android\\app\\src\\main\\assets\\");

        try {
            ZipFile zipFile = new ZipFile("");
            ZipOut zipOut = new ZipOut(assetsDir + "\\sub.apk").setInput(zipFile);

            DexStringEncryptor encryptor = new DexStringEncryptor(FileUtils.toByteArray(zipFile.getInputStream(zipFile.getEntry("classes.dex"))),null);
            zipOut.addFile("classes.dex", encryptor.getData());
            zipOut.save();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
