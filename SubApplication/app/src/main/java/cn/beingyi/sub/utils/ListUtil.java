package cn.beingyi.sub.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtil {


    public static String ListToString(ArrayList<String> list) {
        if(list.size()!=0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(",");
                sb.append(s);
            }
            return new String(sb).replaceFirst(",", "");
        }else{
            return "";
        }
    }

    public static String ListToString(List<String> list) {
        if(list.size()!=0) {
            StringBuilder sb = new StringBuilder();
            for (String s : list) {
                sb.append(",");
                sb.append(s);
            }
            return new String(sb).replaceFirst(",", "");
        }else {
            return "";
        }
    }

    public static ArrayList<String> StringToList(String str) {
        if(str.contains(",")){

            return new ArrayList(Arrays.asList(str.split(",")));
        }else{
            if(str.isEmpty()){
                ArrayList<String> result = new ArrayList<>();
                result.add("");
                return result;
            }else {
                ArrayList<String> result = new ArrayList<>();
                result.add(str);
                return result;
            }
        }

    }


}
