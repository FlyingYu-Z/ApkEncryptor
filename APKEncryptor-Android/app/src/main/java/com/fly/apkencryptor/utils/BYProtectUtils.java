package com.fly.apkencryptor.utils;

import com.fly.apkencryptor.application.MyApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.fly.apkencryptor.utils.FileUtils.delSingleFile;


public class BYProtectUtils {

    public static String readAssetsTxt(String fileName)
    {
        try
        {
            InputStream localInputStream = getStreamFromAssets(fileName);
            byte[] arrayOfByte = new byte[localInputStream.available()];
            localInputStream.read(arrayOfByte);
            localInputStream.close();
            String str = new String(arrayOfByte, "utf-8");
            return str;
        }
        catch (Exception localIOException)
        {
            localIOException.printStackTrace();
        }
        return "";
    }



    public static void copyAssetsFile(String fileName, String desPath) {
        delSingleFile(desPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getStreamFromAssets(fileName);
            out = new FileOutputStream(desPath);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = in.read(bytes)) != -1)
                out.write(bytes, 0 , i);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static InputStream getStreamFromAssets(String fileName)
    {
        try
        {
            InputStream localInputStream = MyApp.getContext().getAssets().open(getAssetsName(fileName));
//            byte[] data=toByteArray(localInputStream);
//            String  key=SPUtils.getString("by","key");
//            return new ByteArrayInputStream(ByteEncoder.Decrypt(data,key));
            return localInputStream;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    public static String getAssetsName(String path){
        return path;
//        if(path.contains("/")) {
//            String result=path;
//            String[] names=path.split("/");
//            for(String name:names){
//                String md5=MD5.encode(name);
//                result=result.replace(name,md5);
//            }
//            return result;
//        }else{
//            return MD5.encode(path);
//        }
    }


    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }


}
