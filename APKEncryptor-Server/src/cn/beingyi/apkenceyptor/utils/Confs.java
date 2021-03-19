package cn.beingyi.apkenceyptor.utils;

public class Confs {
    Tools tools;

    public Confs() {
        tools=new Tools();

    }

    public String getValue(String Name){
        return tools.find("confs","Value","Name",Name);
    }

    public void setValue(String Name,Object Value){
        tools.update("confs","Value",Value.toString(),"Name",Name);
    }


    public void close(){
        tools.close();
    }

}
