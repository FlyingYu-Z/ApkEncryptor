package com.fly.apkencryptor.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AndroidManifestAnalyze {

    public String appPackage;
    public List<String> permissions = new ArrayList();
    public List<String> activities = new ArrayList();
    public String LaunchActivity;
    /**
     * 解析包名
     * @param doc
     * @return
     */
    public  String findPackage(Document doc){
        Node node = doc.getFirstChild();
        NamedNodeMap attrs  =node.getAttributes();
        for(int i = 0; i < attrs.getLength(); i++){
            if(attrs.item(i).getNodeName() == "package"){
                return attrs.item(i).getNodeValue();
            }
        }
        return null;
    }

    /**
     * 解析入口activity
     * @param doc
     * @return
     */
    public  String findLaucherActivity(Document doc){
        Node activity = null;
        String sTem = "";
        NodeList categoryList = doc.getElementsByTagName("category");
        for(int i = 0; i < categoryList.getLength(); i++){
            Node category = categoryList.item(i);
            NamedNodeMap attrs  =category.getAttributes();
            for(int j = 0; j < attrs.getLength(); j++){
                if(attrs.item(j).getNodeName() == "android:name"){
                    if(attrs.item(j).getNodeValue().equals("android.intent.category.LAUNCHER")){
                        activity = category.getParentNode().getParentNode();
                        break;
                    }
                }
            }
        }
        if(activity != null){
            NamedNodeMap attrs  =activity.getAttributes();
            for(int j = 0; j < attrs.getLength(); j++){
                if(attrs.item(j).getNodeName() == "android:name"){
                    sTem = attrs.item(j).getNodeValue();
                }
            }
        }
        return sTem;
    }


    public void xmlHandle(InputStream inputStream){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // 创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();

            //加载xml文件
            Document document = db.parse(inputStream);
            NodeList permissionList = document.getElementsByTagName("uses-permission");
            NodeList activityAll = document.getElementsByTagName("activity");

            //获取权限列表
            for (int i = 0; i < permissionList.getLength(); i++) {
                Node permission = permissionList.item(i);
                permissions.add((permission.getAttributes()).item(0).getNodeValue());
            }

            //获取activity列表
            appPackage = (findPackage(document));
            for(int i = 0; i < activityAll.getLength(); i++){
                Node activity = activityAll.item(i);
                NamedNodeMap attrs  =activity.getAttributes();
                for(int j = 0; j < attrs.getLength(); j++){
                    if(attrs.item(j).getNodeName() == "android:name"){
                        String sTem = attrs.item(j).getNodeValue();
                        if(sTem.startsWith(".")){
                            sTem = appPackage+sTem;
                        }
                        activities.add(sTem);
                    }
                }
            }
            String s = findLaucherActivity(document);
            if(s.startsWith(".")){
                s = appPackage+s;
            }
            //移动入口类至首位
            this.LaunchActivity=s;
            activities.remove(s);
            activities.add(0, s);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}