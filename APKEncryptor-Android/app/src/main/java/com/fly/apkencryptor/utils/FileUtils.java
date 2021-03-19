package com.fly.apkencryptor.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;


import com.fly.apkencryptor.application.MyApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

public class FileUtils
{


    public static String getSDPath()
    { 
        File sdDir = null; 
        boolean sdCardExist = Environment.getExternalStorageState()   
            .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist)   
        {                               
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }   
        return sdDir.toString(); 
    }



    public static String getModifiedTime(String path) {
        File file = new File(path);
        Calendar cal = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime()).toString();

    }


    public static void mkdir(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }


    public static String getMd5ByFile(String path)
    {
        String value = null;
        File file=new File(path);
		FileInputStream in=null;
		try
        {
		    in = new FileInputStream(file);

            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
			byte[] md5_byte=md5.digest();
			
			
            BigInteger bi = new BigInteger(1, md5_byte);
            value = bi.toString(16);
			value=toHexString(md5_byte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
		
		

		
		
        return value;
    }

	private static String toHexString(byte[] bytes) {       
        Formatter formatter = new Formatter();              
        for (byte b : bytes) {          
            formatter.format("%02x", b);
        }

        String res = formatter.toString();
        formatter.close();
        return res;
	}



    public static String getMD5ByStream(InputStream is) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");

            while ((len = is.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            is.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }




    public static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String readAssetsTxt(String paramString)
    {
        try
        {
            InputStream localInputStream = MyApp.getContext().getAssets().open(paramString);
            byte[] arrayOfByte = new byte[localInputStream.available()];
            localInputStream.read(arrayOfByte);
            localInputStream.close();
            String str = new String(arrayOfByte, "utf-8");
            return str;
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
        }
        return "";
    }



    public static InputStream getStreamFromAssets(String fileName)
    {
        try
        {
            InputStream localInputStream = MyApp.getContext().getAssets().open(fileName);
            return localInputStream;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public static String getFilePathFromUri(final Uri uri)
    {
        if (null == uri)
        {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
        {
            data = uri.getPath();
        }

        return data;
    }



    public static void copyFile(String oldPath, String newPath)
    {  
        try
        {  
            int bytesum = 0;  
            int byteread = 0;  
            File oldfile = new File(oldPath);  

            if (!new File(newPath).getParentFile().exists())
            {
                new File(newPath).getParentFile().mkdirs();
            }

            if (oldfile.exists())
            { //文件存在时  
                InputStream inStream = new FileInputStream(oldPath); //读入原文件  
                FileOutputStream fs = new FileOutputStream(newPath);  
                byte[] buffer = new byte[1444];  
                while ((byteread = inStream.read(buffer)) != -1)
                {  
                    bytesum += byteread; //字节数 文件大小  
                    System.out.println(bytesum);  
                    fs.write(buffer, 0, byteread);  
                }  
                inStream.close();  
                fs.close();  
            }  
        }  
        catch (Exception e)
        {  
            System.out.println("复制单个文件操作出错");  
            e.printStackTrace();  
        }  
    }  


    public static void delSingleFile(String path)
    {
        try
        {
            File file = new File(path);
            file.delete(); //删除空文件夹
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
        }
    }


    public static void delFolder(String folderPath)
    {
        try
        {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
        }
    }

//删除指定文件夹下所有文件
//param path 文件夹完整绝对路径
    private static boolean delAllFile(String path)
    {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists())
        {
            return flag;
        }
        if (!file.isDirectory())
        {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++)
        {
            if (path.endsWith(File.separator))
            {
                temp = new File(path + tempList[i]);
            }
            else
            {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile())
            {
                temp.delete();
            }
            if (temp.isDirectory())
            {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }









    public static String readFile(String path)
    {
        String result="";
        if (!new File(path).exists())
        {
            System.out.println("文件不存在："+path);
            return "";
        }
        else
        {
            try
            {
                FileInputStream fis = new FileInputStream(path);
                @SuppressWarnings("resource")
                BufferedReader br = new BufferedReader(new InputStreamReader(
                                                               fis));
                StringBuilder sb = new StringBuilder("");
                String line = null;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line+"\r\n");
                }
                Log.i("tag", sb.toString());
                result = sb.toString();
            }
            catch (Exception e)
            {
                Log.i("tag", "读取失败！");
                System.out.println(e);
                return "";
            }
        }

        return result;
    }


    public static void writeFile(File file,String content,boolean append){

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            BufferedWriter bf = new BufferedWriter(fileWriter);
            bf.write(content);
            bf.flush();
            fileWriter.flush();
            bf.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeFile(String path, String content) throws IOException
    {

        File saveFile = new File(path);
        if (!saveFile.getParentFile().exists())
        {
            saveFile.getParentFile().mkdirs();
        }

        if (!saveFile.exists())
        {
            saveFile.createNewFile();
        }
        final FileOutputStream outStream = new FileOutputStream(saveFile);
        try
        {
            outStream.write(content.getBytes());
            outStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }



	
	
	
	public static boolean hasExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    /**
     * 获取缓存路径
     *
     * @param context
     * @return 返回缓存文件路径
     */
    public static File getCacheDir(Context context) {
        File cache;
        if (hasExternalStorage()) {
            cache = context.getExternalCacheDir();
        } else {
            cache = context.getCacheDir();
        }
        if (!cache.exists())
            cache.mkdirs();
        return cache;
    }

    public static void copyAssetsFile(Context context, String fileName,String desPath) {
		delSingleFile(desPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getApplicationContext().getAssets().open(fileName);
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



    public static String getPrefix(String path){
        String result="";
        if(new File(path).isDirectory()){
            return "";
        }
        String name=new File(path).getName();
        result=name.subSequence(0,name.lastIndexOf(".")).toString();
        return result.toLowerCase();
    }


    public static String getSuffix(String path){
        String result="";
        if(new File(path).isDirectory()){
            return "";
        }
        result=path.subSequence(path.lastIndexOf(".")+1,path.length()).toString();
        return result.toLowerCase();
    }



    public static byte[] getByte(String filePath) {

        InputStream in = null;
        byte[] data=null;
        try {
            in = new FileInputStream(filePath);

            data = toByteArray(in);
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();

    }



    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String BitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap Base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
