package cn.beingyi.sub.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.zip.ZipFile;

import cn.beingyi.sub.classloaders.MyDexClassLoader;

public class FileUtils {


    private FileUtils() {
        // This space intentionally left blank.
    }



    public static String readAssetsTxt(Context context,String paramString)
    {
        try
        {
            InputStream localInputStream = context.getAssets().open(paramString);
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




    public static String readZipTxt(Context context,String entry)
    {
        try
        {
            ZipFile zipFile=new ZipFile(context.getPackageResourcePath());
            InputStream localInputStream = zipFile.getInputStream(zipFile.getEntry(entry));
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

    public static byte[] readZipByte(Context context,String entry)
    {
        try
        {
            ZipFile zipFile=new ZipFile(context.getPackageResourcePath());
            InputStream in = zipFile.getInputStream(zipFile.getEntry(entry));
            return toByteArray(in);
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
        }
        return null;
    }



    public static ByteArrayOutputStream cloneInputStream(InputStream input)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1)
            {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }



    public static byte[] getByte(String filePath) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("file not exists");
            }
            bos = new ByteArrayOutputStream((int) file.length());
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Reads the given file, translating {@link IOException} to a
     * {@link RuntimeException} of some sort.
     *
     * @param file non-null; the file to read
     * @return non-null; contents of the file
     */
    public static byte[] readFile(File file)
            throws IOException {
        return readFile(file, 0, -1);
    }

    /**
     * Reads the specified block from the given file, translating
     * {@link IOException} to a {@link RuntimeException} of some sort.
     *
     * @param file   non-null; the file to read
     * @param offset the offset to begin reading
     * @param length the number of bytes to read, or -1 to read to the
     *               end of the file
     * @return non-null; contents of the file
     */
    private static byte[] readFile(File file, int offset, int length)
            throws IOException {
        if (!file.exists()) {
            throw new RuntimeException(file + ": file not found");
        }

        if (!file.isFile()) {
            throw new RuntimeException(file + ": not a file");
        }

        if (!file.canRead()) {
            throw new RuntimeException(file + ": file not readable");
        }

        long longLength = file.length();
        int fileLength = (int) longLength;
        if (fileLength != longLength) {
            throw new RuntimeException(file + ": file too long");
        }

        if (length == -1) {
            length = fileLength - offset;
        }

        if (offset + length > fileLength) {
            throw new RuntimeException(file + ": file too short");
        }

        FileInputStream in = new FileInputStream(file);

        int at = offset;
        while (at > 0) {
            long amt = in.skip(at);
            if (amt == -1) {
                throw new RuntimeException(file + ": unexpected EOF");
            }
            at -= amt;
        }

        byte[] result = readStream(in, length);

        in.close();

        return result;
    }

    private static byte[] readStream(InputStream in, int length)
            throws IOException {
        byte[] result = new byte[length];
        int at = 0;

        while (length > 0) {
            int amt = in.read(result, at, length);
            if (amt == -1) {
                throw new RuntimeException("unexpected EOF");
            }
            at += amt;
            length -= amt;
        }

        return result;
    }


    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }


    public static String getMd5ByFile(String path) {
        String value = null;
        File file = new File(path);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);

            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            byte[] md5_byte = md5.digest();


            BigInteger bi = new BigInteger(1, md5_byte);
            value = bi.toString(16);
            value = toHexString(md5_byte);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (Exception e) {
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


    public static String getFilePathFromUri(final Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        }

        return data;
    }


    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);

            if (!new File(newPath).getParentFile().exists()) {
                new File(newPath).getParentFile().mkdirs();
            }

            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void delSingleFile(String path) {
        try {
            File file = new File(path);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


    public static String readFile(String path) {
        String result = "";
        if (!new File(path).exists()) {
            return "";
        } else {
            try {
                FileInputStream fis = new FileInputStream(path);
                @SuppressWarnings("resource")
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder("");
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                System.out.println(e);
                return "";
            }
        }

        return result;
    }

    public static byte[] read(String fileName) throws IOException {
        InputStream is = new FileInputStream(fileName);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[is.available()];
        int n = 0;
        while ((n = is.read(buffer)) != -1)
            bos.write(buffer, 0, n);
        bos.close();
        is.close();
        return bos.toByteArray();
    }


    public static void writeFile(String path, String content) throws IOException {

        File saveFile = new File(path);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }

        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }
        final FileOutputStream outStream = new FileOutputStream(saveFile);
        try {
            outStream.write(content.getBytes());
            outStream.close();
        } catch (IOException e) {
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

    public static void copyAssetsFile(Context context, String fileName, String desPath) {
        delSingleFile(desPath);
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getApplicationContext().getAssets().open(fileName);
            out = new FileOutputStream(desPath);
            byte[] bytes = new byte[1024];
            int i;
            while ((i = in.read(bytes)) != -1)
                out.write(bytes, 0, i);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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


    public static String getModifiedTime(String path) {
        File file = new File(path);
        Calendar cal = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm");
        cal.setTimeInMillis(time);
        return formatter.format(cal.getTime()).toString();

    }


    public static String getSuffix(String path) {
        String result = "";
        if (new File(path).isDirectory()) {
            return "";
        }

        result = path.subSequence(path.lastIndexOf(".") + 1, path.length()).toString();


        return result.toLowerCase();
    }


    public static String getPrefix(String path) {
        String result = "";
        File file = new File(path);
        if (new File(path).isDirectory()) {
            return "";
        }

        result = file.getName().subSequence(0, file.getName().lastIndexOf(".")).toString();


        return result;
    }


    public static boolean rename(File file, String name) {
        return file.renameTo(new File(file.getParent(), name));
    }


    public static boolean isBinary(String path) {
        File file = new File(path);
        boolean isBinary = false;
        try {
            FileInputStream fin = new FileInputStream(file);
            long len = file.length();
            for (int j = 0; j < (int) len; j++) {
                int t = fin.read();
                if (t < 32 && t != 9 && t != 10 && t != 13) {
                    isBinary = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBinary;
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

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }


    public static void saveFile(byte[] bfile, String filePath) throws Exception {
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



    public static void saveFile(InputStream in,String path)throws IOException {

        int bytesum = 0;
        int byteread = 0;

        FileOutputStream fs = new FileOutputStream(path);
        byte[] buffer = new byte[1444];
        while ((byteread = in.read(buffer)) != -1)
        {
            bytesum += byteread;
            fs.write(buffer, 0, byteread);
        }
        in.close();
        fs.close();


    }



    public static void createFile(String path) throws IOException {

        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }

    }


    public static int chmod(File path, int mode) throws Exception {
        Class<?> fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions =
                fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
        return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
    }

    public static int getPermissions(File path) throws Exception {
        
        Class<?> fileUtils = Class.forName("android.os.FileUtils");
        int[] result = new int[1];
        Method getPermissions = fileUtils.getMethod("getPermissions", String.class, int[].class);
        getPermissions.invoke(null, path.getAbsolutePath(), result);
        return result[0];
    }



}

