package cn.beingyi.sub.utils;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static cn.beingyi.sub.utils.FileUtils.delSingleFile;


public class BYProtectUtils {



    public static String getAssetsName(String path){

        if(path.contains("/")) {
            String result=path;
            String[] names=path.split("/");
            for(String name:names){
                String md5=MD5.encode(name);
                result=result.replace(name,md5);
            }

            return result;
        }else{
            return MD5.encode(path);
        }

    }
}
