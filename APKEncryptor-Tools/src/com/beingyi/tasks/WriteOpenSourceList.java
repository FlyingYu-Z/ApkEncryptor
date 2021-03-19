package com.beingyi.tasks;

import com.beingyi.tools.utils.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WriteOpenSourceList {
    JSONObject jsonObject=new JSONObject();
    int i=1;
    public WriteOpenSourceList(File assets) throws Exception {
        File openSourcePath=new File(assets,"OpenSourceList.json");

        addItem("ANTLR 3 Runtime","https://mvnrepository.com/artifact/org.antlr/antlr-runtime","BSD","A framework for constructing recognizers, compilers, and translators from grammatical descriptions containing Java, C#, C++, or Python actions.");
        addItem("ApkSigner","https://android.googlesource.com/platform/tools/apksig","Apache 2.0","apksig is a project which aims to simplify APK signing and checking whether APK's signatures should verify on Android. apksig supports JAR signing (used by Android since day one) and APK Signature Scheme v2 (supported since Android Nougat, API Level 24).");
        addItem("Apache Commons IO","https://mvnrepository.com/artifact/commons-io/commons-io","Apache 2.0","The Apache Commons IO library contains utility classes, stream implementations, file filters, file comparators, endian transformation classes, and much more.");
        addItem("Apache Commons Lang","https://mvnrepository.com/artifact/org.apache.commons/commons-lang3","Apache 2.0","Apache Commons Lang, a package of Java utility classes for the classes that are in java.lang's hierarchy, or are considered to be so standard as to justify existence in java.lang.");
        addItem("Apache Commons CLI","https://mvnrepository.com/artifact/commons-cli/commons-cli","Apache 2.0","Apache Commons CLI provides a simple API for presenting, processing and validating a command line interface");
        addItem("Dexlib2","https://mvnrepository.com/artifact/org.smali/dexlib2","BSD 3-clause","dexlib2 is a library for reading/modifying/writing Android dex files");
        addItem("Guava","https://mvnrepository.com/artifact/com.google.guava/guava","\tApache 2.0","Guava is a suite of core and expanded libraries that include utility classes, google's collections, io classes, and much much more.");
        addItem("ZipSigner","https://code.google.com/archive/p/zip-signer/source/default/source","Apache 2.0","API and app for signing Zip, Apk, and/or Jar files onboard Android devices.");
        //addItem("","","","");

        FileUtils.writeFile(openSourcePath.getAbsolutePath(),jsonObject.toString());

    }


    private void addItem(String Name,String Url,String License,String Description) throws JSONException {
        JSONObject item=new JSONObject();
        item.put("Name",Name);
        item.put("Url",Url);
        item.put("License",License);
        item.put("Description",Description);

        jsonObject.put(String.valueOf(i),item.toString());
        i++;
    }


    class OSInfo{
        public String Name="";
        public String Url="";
        public String License="";
        public String Description="";

    }


}
