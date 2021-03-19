package com.fly.apkencryptor.utils;

import java.util.ArrayList;

public class Property {

    String Content;
    ArrayList<String> array=new ArrayList<String>();

    public Property(String mContent){
        this.Content=mContent;
        String[] lines=Content.split("\n");
        for(int i=0;i<lines.length;i++){
            array.add(lines[i].replace("nnn","\n"));

        }

    }


    public Property() {
        this.Content = "";
    }

    public String getString(String key){
        for(int i=0;i<array.size();i++){
            if(array.get(i).contains(key+"=")){
                String value=array.get(i).substring((key+"=").length(),array.get(i).length()).toString().trim();
                return value;
            }

        }

        return "";
    }

    public boolean getBoolean(String key){
        for(int i=0;i<array.size();i++){
            if(array.get(i).contains(key+"=")){
                boolean value=Boolean.parseBoolean(array.get(i).substring((key+"=").length(),array.get(i).length()).toString().trim());
                return value;
            }

        }

        return false;
    }


    public void setValue(String key, String value){

        if(array.size()==0){
            array.add((key+"="+value).trim());
            return;
        }

        for(int i=0;i<array.size();i++){
            if(array.get(i).contains(key+"=")){
                array.remove(i);
                array.add((key+"="+value).trim());
                return;
            }else {
                if(i==array.size()-1){
                    array.add((key+"="+value).trim());
                    return;
                }
            }

        }

    }


    public void setValue(String key, boolean value){

        if(array.size()==0){
            array.add((key+"="+value).trim());
            return;
        }

        for(int i=0;i<array.size();i++){
            if(array.get(i).contains(key+"=")){
                array.remove(i);
                array.add((key+"="+value).trim());
                return;
            }else {
                if(i==array.size()-1){
                    array.add((key+"="+value).trim());
                    return;
                }
            }

        }

    }


    public String toString(){
        String result="";
        for(int i=0;i<array.size();i++){
            result=result+"\n"+array.get(i);
        }
        return result;
    }



}
