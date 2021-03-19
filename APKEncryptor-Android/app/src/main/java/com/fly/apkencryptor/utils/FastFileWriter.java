package com.fly.apkencryptor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FastFileWriter {

    File file;
    boolean append;

    public FastFileWriter(File file,boolean append)throws Exception{
        this.file=file;
        this.append =append;
        if(!file.exists()){
            file.createNewFile();
        }

    }

    public void write(String content) throws Exception{

        FileWriter fileWriter;
        BufferedWriter bufferedWriter;

        fileWriter=new FileWriter(file,append);
        bufferedWriter=new BufferedWriter(fileWriter);

        bufferedWriter.write(content);
        bufferedWriter.flush();
        fileWriter.flush();

        bufferedWriter.close();
        fileWriter.close();

    }


    public File getPath(){

        return file;
    }


}
