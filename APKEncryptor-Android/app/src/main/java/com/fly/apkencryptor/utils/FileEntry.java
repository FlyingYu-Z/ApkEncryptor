package com.fly.apkencryptor.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.KeyGenerator;

import Decoder.BASE64Encoder;

public class FileEntry
{
    public static void main(String[] args)
    {

        String key="123456";//密码
        String tmp="/storage/emulated/0/test/test.jpg.加密后";//加密后的文件
        String path="/storage/emulated/0/test/test.jpg";//要加密的文件
        String ded="/storage/emulated/0/test/test.jpg.加密后.解密后";//解密后的文件
        try
        {
            //encrypt(path,tmp,key);
        }
        catch (Exception e)
        {
        }
        
        try
        {
            //decrypt(tmp,ded, key);
        }
        catch (Exception e)
        {
        }

    }





    public static void encrypt(String fileUrl, String destpath, String key) throws Exception
    {
        File file = new File(fileUrl);
        String path = file.getPath();
        if (!file.exists())
        {
            return;
        }

        File dest = new File(destpath);
        InputStream in = new FileInputStream(fileUrl);
        OutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int len=0;;
        byte[] outputBuffer = new byte[1024];
        String regEx="[^0-9]";  
        Pattern p = Pattern.compile(regEx);  
        Matcher m = p.matcher(getMd5(key));
        String result=m.replaceAll("").trim();

        while ((len = in.read(buffer)) > 0)
        {
            for (int i=0;i < len;i++)
            {
                byte b = buffer[i];
                b =(byte)(b^ Integer.parseInt(result));
                outputBuffer[i] = b;
            }

            out.write(outputBuffer, 0, len);
            out.flush();
        }
        in.close();
        out.close();
        System.out.println("加密成功");
    }





    public static String decrypt(String fileUrl, String destpath, String key) throws Exception
    {
        File file = new File(fileUrl);
        if (!file.exists())
        {
            return null;
        }
        File dest = new File(destpath);
        if (!dest.getParentFile().exists())
        {
            dest.getParentFile().mkdirs();
        }
        InputStream is = new FileInputStream(fileUrl);
        OutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int len=0;;
        byte[] outputBuffer = new byte[1024];
        String regEx="[^0-9]";  
        Pattern p = Pattern.compile(regEx);  
        Matcher m = p.matcher(getMd5(key));
        String result=m.replaceAll("").trim();
        while ((len = is.read(buffer)) > 0)
        {
            for (int i=0;i < len;i++)
            {
                byte b = buffer[i];
                b =(byte)(b^ Integer.parseInt(result));
                outputBuffer[i] = b;
            }
            out.write(outputBuffer, 0, len);
            out.flush();
        }
        out.close();
        is.close();
        System.out.println("解密成功");
        return destpath;
    }




    public static InputStream encrypt(InputStream is, String key) throws Exception
    {

        InputStream resultStream=null;
        byte[] buffer = new byte[1024];
        int len=0;;
        byte[] outputBuffer = new byte[1024];
        ByteArrayOutputStream outputByte = new ByteArrayOutputStream();
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(getMd5(key));
        String result=m.replaceAll("").trim();

        while ((len = is.read(buffer)) > 0)
        {
            for (int i=0;i < len;i++)
            {
                byte b = buffer[i];
                b =(byte)(b^ Integer.parseInt(result));
                outputBuffer[i] = b;
            }

            outputByte.write(outputBuffer, 0, len);
            outputByte.flush();
        }
        is.close();

        resultStream=new ByteArrayInputStream(outputByte.toByteArray());

        return resultStream;
    }





    public static InputStream decrypt(InputStream is, String key) throws Exception
    {
        InputStream resultStream=null;
        byte[] buffer = new byte[1024];
        int len=0;;
        byte[] outputBuffer = new byte[1024];
        ByteArrayOutputStream outputByte = new ByteArrayOutputStream();
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(getMd5(key));
        String result=m.replaceAll("").trim();
        while ((len = is.read(buffer)) > 0)
        {
            for (int i=0;i < len;i++)
            {
                byte b = buffer[i];
                b =(byte)(b^ Integer.parseInt(result));
                outputBuffer[i] = b;
            }
            outputByte.write(outputBuffer, 0, len);
            outputByte.flush();
        }

        resultStream=new ByteArrayInputStream(outputByte.toByteArray());
        is.close();


        return resultStream;
    }





    public static String getMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }






    Key key;   
    public FileEntry(String str) {   
        getKey(str);//生成密匙   
    }   
    /**  
     * 根据参数生成KEY  
     */   
    public void getKey(String strKey) {   
        try {   
            KeyGenerator _generator = KeyGenerator.getInstance("DES");   
            _generator.init(new SecureRandom(strKey.getBytes()));   
            this.key = _generator.generateKey();   
            _generator = null;   
        } catch (Exception e) {   
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);   
        }   
    }








}

