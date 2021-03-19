package cn.beingyi.sub.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipOut {
    ZipFile inputZipFile;
    String outPath;
    ZipOutputStream zos;
    List<String> saveList=new ArrayList<>();
    List<String> removeList=new ArrayList<>();


    List<String> entries = new ArrayList<>();

    public ZipOut(String outPath) throws Exception {
        this.outPath=outPath;
        this.zos=new ZipOutputStream(new FileOutputStream(outPath));

    }

    public ZipOut setInput(ZipFile zipFile) throws Exception {
        this.inputZipFile=zipFile;
        readZip(inputZipFile);
        return this;
    }

    public void addFile(String entry, byte[] data) throws IOException
    {
        zos.putNextEntry(new ZipEntry(entry));
        zos.write(data);
        zos.closeEntry();
        saveList.add(entry);
    }

    public void removeFile(String entry){
        removeList.add(entry);

    }

    public void save() throws Exception{
        if(inputZipFile==null){
            throw new FileNotFoundException("the input file not found!");
        }

        Iterator entry = entries.iterator();
        while (entry.hasNext()) {
            String key = (String) entry.next();

            if (removeList.contains(key) || saveList.contains(key)) {
                continue;
            }

            byte[] data = FileUtils.toByteArray(inputZipFile.getInputStream(inputZipFile.getEntry(key)));
            zos.putNextEntry(new ZipEntry(key));
            zos.write(data);
            zos.closeEntry();

        }
        zos.close();
        inputZipFile.close();
    }



    private void readZip(ZipFile zip) throws Exception {
        Enumeration enums = zip.entries();
        while (enums.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enums.nextElement();
            String entryName = entry.getName();
            entries.add(entryName);
        }

    }


}
