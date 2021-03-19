package cn.beingyi.apkenceyptor.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {


    public static String ListToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list)
        {
            sb.append(",");
            sb.append(s);
        }
        return new String(sb).replaceFirst(",", "");
    }


    public static List<String> StringToList(String str) {
    	List<String> list=new ArrayList<>();
    	String[] array=str.split(",");
    	
    	for(int i=0;i<array.length;i++) {
    		list.add(array[i]);
    	}
    	
        return list;
    }


}
