package com.beingyi.confuse;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        Main main=new Main();
        main.start();
    }

    public void start(){
        File workDir=new File("resource");
        File arscFile=new File(workDir,"resources.arsc");
        File outArscFile=new File(workDir,"resources_out.arsc");

        try {
            ArscObfuser arscObfuser=new ArscObfuser(new FileInputStream(arscFile));
            saveFile(outArscFile.getAbsolutePath(),arscObfuser.getData());
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void saveFile(String filePath,byte[] bfile) throws Exception {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;

        File dir = new File(filePath);
        if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
            dir.mkdirs();
        }
        file = new File(filePath);
        fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);
        bos.write(bfile);

        if (bos != null) {

            bos.close();

        }
        if (fos != null) {

            fos.close();

        }


    }

}
