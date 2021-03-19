package cn.beingyi.sub.utils;

import java.io.File;
import java.io.IOException;
import android.os.Process;

public class CustomFun {

    public static void exit(){

        String exitCmd = "kill " + Process.myPid();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(exitCmd);
        } catch (IOException ignored) {
        }

    }
    public static void deleteFile(String path){
        String deleteCmd = "rm -r" + path;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(deleteCmd);
        } catch (IOException ignored) {
        }
    }

    public static void deleteFloor(String dir){
        File[] files=new File(dir).listFiles();
        for(File file:files){
            if(file.isFile()){
                deleteFile(file.getAbsolutePath());
            }else{
                deleteFloor(file.getAbsolutePath());
            }
        }
    }

}
