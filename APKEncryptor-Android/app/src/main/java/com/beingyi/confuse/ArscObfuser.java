package com.beingyi.confuse;

import com.fly.apkencryptor.utils.FileUtils;
import com.iyfeng.arsceditor.AndrolibResources;
import com.iyfeng.arsceditor.ResDecoder.ARSCCallBack;
import com.iyfeng.arsceditor.ResDecoder.data.ResTable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ArscObfuser {
    AndrolibResources mAndRes;
    List<String> configs=new ArrayList<>();
    List<String> types=new ArrayList<>();
    List<String> keys=new ArrayList<>();
    List<String> values=new ArrayList<>();

    List<String> changedValues=new ArrayList<>();
    List<String> resTypes=new ArrayList<>();
    List<String> resValues=new ArrayList<>();
    HashMap<String,String> obfusedMap=new HashMap<>();

    byte[] input;

    public ArscObfuser(InputStream in)throws Exception{

        resTypes.add("layout");
        resTypes.add("mipmap");
        resTypes.add("menu");
        resTypes.add("drawable");
        resTypes.add("anim");
        resTypes.add("animator");
        //resTypes.add("xml");
        resTypes.add("interpolator");
        //resTypes.add("color");


        this.input=toByteArray(in);

        byte[] newBytes=input.clone();
        mAndRes=new AndrolibResources();
        ResTable resTable=mAndRes.getResTable(new ByteArrayInputStream(newBytes));

        ARSCCallBack callback = new ARSCCallBack() {
            @Override
            public void back(String config, String type, String key, String value) {

                if (type != null) {
                    //System.out.println("type="+type+",key="+key+",value="+value);

                    configs.add(config);
                    types.add(type);
                    keys.add(key);
                    values.add(value);
                    changedValues.add("");

                    if(resTypes.contains(type)&& value.startsWith("res/")){
                        resValues.add(value);
                    }

                }

            }
        };

        mAndRes.decodeARSC(resTable, callback);


        for(String value:resValues){
            String suffix= getSuffix(value);
            String obfusedValue=getRandomLitter(32)+"."+suffix;
            obfusedMap.put(value,obfusedValue);
            //String key=getKey(value);
            //changeValue(key,obfusedValue);
            mAndRes.mARSCDecoder.replace(value,obfusedValue);
            System.out.println(value+"->"+obfusedValue);
        }


    }


    public static String getSuffix(String path){
        String result="";
        if(new File(path).isDirectory()){
            return "";
        }
        result=path.subSequence(path.lastIndexOf(".")+1,path.length()).toString();
        return result.toLowerCase();
    }


    private static String getRandomLitter(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }


    public HashMap<String,String> getMap(){
        return obfusedMap;
    }

    private String getKey(String value){
        int position = values.indexOf(value);
        return keys.get(position);
    }

    private void changeValue(String key,String value){

        int position = keys.indexOf(key);
        if(position == -1){
            System.err.println("not found:"+key);
            return;
        }
        //System.out.printalueln("found: " + values.get(position));
        changedValues.remove(position);
        changedValues.add(position, value);


    }

    public byte[] getData()throws Exception{
        return mAndRes.mARSCDecoder.write(new ByteArrayInputStream(input), values, changedValues);
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


    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        in.close();
        return out.toByteArray();
    }


}
