package cn.beingyi.apkenceyptor.strings;

import cn.beingyi.apkenceyptor.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Strings {

    public static String getString(String language,String id){
        InputStream in=Strings.class.getResourceAsStream("string-"+language+".txt");

        byte[] data= new byte[0];
        try {
            data = FileUtils.toByteArray(in);
            String content=new String(data,"UTF-8");
            Property property=new Property(content);

            return property.getString(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    static class Property {

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


        public void setValue(String key,String value){
            for(int i=0;i<array.size();i++){
                if(array.get(i).contains(key+"=")){
                    array.remove(i);
                    array.add((key+"="+value).trim());
                    return;
                }else {
                    if(i==array.size()){
                        array.add((key+"="+value).trim());
                    }
                }

            }

        }


        public void setValue(String key,boolean value){
            for(int i=0;i<array.size();i++){
                if(array.get(i).contains(key+"=")){
                    array.remove(i);
                    array.add((key+"="+value).trim());
                    return;
                }else {
                    if(i==array.size()){
                        array.add((key+"="+value).trim());
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


}
