package com.fly.apkencryptor.utils;


import com.fly.apkencryptor.bean.FileInfo;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileInfoSort {


    public static void sort(List<FileInfo> fileList) {

        sortByName(fileList, false);
    }


    public static List<FileInfo> sortByName(List<FileInfo> fileList, boolean isDesc) {
        List<FileInfo> result = fileList;

        Collections.sort(fileList, new Comparator<FileInfo>() {

            @Override
            public int compare(FileInfo p1, FileInfo p2) {

                File file1 = new File(p1.Path);
                File file2 = new File(p2.Path);


                if (file1.isDirectory() && file2.isFile()) {
                    return -1;
                }
                if (file1.isFile() && file2.isDirectory()) {
                    return 1;
                }


                return file1.getName().compareTo(file2.getName());


            }
        });


        return result;
    }


    public static List<FileInfo> sortByTime(List<FileInfo> fileList, boolean isDesc) {
        List<FileInfo> result = fileList;

        Collections.sort(fileList, new Comparator<FileInfo>() {

            @Override
            public int compare(FileInfo p1, FileInfo p2) {

                File file1 = new File(p1.Path);
                File file2 = new File(p2.Path);


                if (file1.lastModified() >= file2.lastModified()) {
                    return -1;
                }
                if (file1.lastModified() <= file2.lastModified()) {
                    return 1;
                }


                return 1;


            }
        });


        return result;
    }


}
