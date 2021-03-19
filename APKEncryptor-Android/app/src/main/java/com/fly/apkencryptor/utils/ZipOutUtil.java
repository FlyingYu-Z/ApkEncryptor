package com.fly.apkencryptor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipOutUtil
{
    public static String AddFile(ZipOutputStream zos, String FileName, byte[] data) throws IOException
    {
        zos.putNextEntry(new ZipEntry(FileName));
        zos.write(data);
        zos.closeEntry();
        return FileName;
    }
    public static void Sava(ZipInputStream zip, ZipOutputStream zos, List<String> FileName, ZipOutUtil.ZipSavsCallback callback) throws IOException
    {
        callback.onStep(Step.START);
        callback.onStep(Step.COPY_FILE);
        ZipEntry entry;
        lable1:while ((entry= zip.getNextEntry())!=null)
        {
            for (String n:FileName)
            {
                if (n.equals(entry.getName()))
                    continue lable1;
            }
            copyZipEntry(zos, entry, zip);
        }
        callback.onStep(Step.OUTPUT);
        zos.close();
        zip.close();
        callback.onStep(Step.FINISH);
    }
    public static void Sava(ZipFile zip, ZipOutputStream zos, List<String> FileName, ZipOutUtil.ZipSavsCallback callback) throws IOException
    {
        callback.onProgress(0, zip.size());
        callback.onStep(Step.START);
        Enumeration<? extends ZipEntry>  enumeration = zip.entries();
        int i = 0;
        callback.onStep(Step.COPY_FILE);
        lable1:while (enumeration.hasMoreElements())
        {
            ZipEntry ze = enumeration.nextElement();
            for (String n:FileName)
            {
                if (n.equals(ze.getName()))
                    continue lable1;
            }
            copyZipEntry(zos, ze, zip);
            callback.onProgress(i++, 0);
        }
        callback.onStep(Step.OUTPUT);
        zos.close();
        zip.close();
        callback.onStep(Step.FINISH);
    }
    private static void copyZipEntry(ZipOutputStream zos, ZipEntry zipEntry, ZipFile zipFile) throws IOException
    {
        InputStream rawInputStream = zipFile.getInputStream(zipEntry);
        zos.putNextEntry(new ZipEntry(zipEntry.getName()));
        int len;
        byte[] b = new byte[10240];
        while ((len = rawInputStream.read(b)) > 0)
        {
            zos.write(b, 0, len);
        }
        zos.closeEntry();
    }
    private static void copyZipEntry(ZipOutputStream zos, ZipEntry zipEntry, ZipInputStream zipFile) throws IOException
    {
        zos.putNextEntry(new ZipEntry(zipEntry.getName()));
        int len;
        byte[] b = new byte[10240];
        while ((len = zipFile.read(b)) > 0)
        {
            zos.write(b, 0, len);
        }
        zos.closeEntry();
    }
    public interface ZipSavsCallback
    {
        void onStep(Step step);
        void onProgress(int progress, int total);
    }
    public enum Step
    {
        START,
        COPY_FILE,
        OUTPUT,
        FINISH
    }
}

