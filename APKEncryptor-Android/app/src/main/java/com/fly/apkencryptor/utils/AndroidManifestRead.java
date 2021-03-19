package com.fly.apkencryptor.utils;

/**
 * Created by Administrator on 2017/11/13.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AndroidManifestRead
{
    public static String MAIN = "android.intent.action.MAIN", NAME = "android:name";

    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static DocumentBuilder builder;
    static Document doc;
    public Node manifest;
    public Node applicaton;
    File path;
    public File getPath()
	{
        return path;
    }
    public AndroidManifestRead(File xmlpath) throws ParserConfigurationException, IOException, SAXException
	{
        path = xmlpath;
        loadData();
    }
    void loadData() throws ParserConfigurationException, SAXException, IOException
	{
        builder = factory.newDocumentBuilder();
        doc = builder.parse(path);
        NodeList temp = doc.getElementsByTagName("manifest");
        manifest = temp.item(0);
        temp = doc.getElementsByTagName("application");
        applicaton = temp.item(0);
    }
    public String getPackage()
	{
        return manifest.getAttributes().getNamedItem("package").getNodeValue();
    }
    public String getAppName() throws NullPointerException
	{
        return getNodeValue(applicaton, "android:label");
    }
    public String getApplicationName()
	{
        return getNodeValue(applicaton, "android:name");
    }
    public String getAppIcon()
	{
        return getNodeValue(applicaton, "android:icon");
    }
    public String getFirstActivityName()
	{

        Node n = getFirstActivity();
        if (n == null)
            return null;
        return getNodeValue(n, NAME);
    }
    public void setAppName(String name)
	{

        setNodeValue(applicaton, "android:label", name);
    }

	public void setApplicationName(String name)
	{

        setNodeValue(applicaton, "android:name", name);
    }

    public void exportActivity()
	{
        NodeList per = doc.getElementsByTagName("activity");
        for (int i = 0; i < per.getLength(); i += 1)
		{
            ((Element) per.item(i)).setAttribute("android:exported", "true");
        }
    }
    public List<String> getPermission()
	{
        if (doc == null)
            return null;
        NodeList per = doc.getElementsByTagName("uses-permission");
        if (per == null)
            return null;
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < per.getLength(); i += 1)
		{
            NamedNodeMap map = per.item(i).getAttributes();
            if (map == null)
                return null;
            Node item = map.getNamedItem("android:name");
            if (item == null)
                return null;
            result.add(item.getNodeValue());
        }
        return result;
    }
	/*
	 <activity
	 android:name="com.alipay.sdk.app.H5PayActivity"             
	 android:configChanges="orientation|keyboardHidden|navigation"             
	 android:exported="false"             
	 android:screenOrientation="behind">
	 </activity> 

	 <activity             
	 android:name="com.alipay.sdk.auth.AuthActivity"
	 android:configChanges="orientation|keyboardHidden|navigation"             
	 android:exported="false"
	 android:screenOrientation="behind" >
	 </activity>



	 android:name="com.alipay.sdk.app.H5PayActivity"             
	 android:configChanges="orientation|keyboardHidden|navigation"
	 android:exported="false"
	 android:screenOrientation="behind" >

	 */
	//支付宝
	public void add1()
	{
        Element item = doc.createElement("activity");
        item.setAttribute("android:name", "com.alipay.sdk.app.H5PayActivity");
		//item.setAttribute("android:configChanges","orientation|keyboardHidden|navigation");
		item.setAttribute("android:exported", "false");
		//item.setAttribute("android:screenOrientation","behind");	
        applicaton.appendChild(item);
    }
	public void add2()
	{
        Element item = doc.createElement("activity");
        item.setAttribute("android:name", "com.alipay.sdk.auth.AuthActivity");
		//item.setAttribute("android:configChanges","orientation|keyboardHidden|navigation");
		item.setAttribute("android:exported", "false");
		//item.setAttribute("android:screenOrientation","behind");	
		applicaton.appendChild(item);
    }
	//支付宝



    public void addPermission(String name)
	{
        Element item = doc.createElement("uses-permission");
        item.setAttribute("android:name", name);
        manifest.appendChild(item);
    }
    public List<String> getActivityName()
	{
        List<FActivityInfo> as = getActivity();
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < as.size(); i += 1)
            names.add(as.get(i).name);
        return names;
    }
    public List<FActivityInfo> getActivity()
	{
        NodeList per = doc.getElementsByTagName("activity");
        List<FActivityInfo> result = new ArrayList<FActivityInfo>();
        for (int i = 0; i < per.getLength(); i += 1)
		{
            FActivityInfo activityitem = new FActivityInfo();
            activityitem.name = getNodeValue(per.item(i), "android:name");
            activityitem.lable = getNodeValue(per.item(i), "android:name");
            //查找Main类
            NodeList childs = per.item(i).getChildNodes();
            for (int j = 0; j < childs.getLength(); j += 1)
			{
                Node an = childs.item(j);
                if (an == null)
                    continue;
                String localname = an.getNodeName();
                if (localname == null) continue;
                if (localname.equals("intent-filter"))
				{
                    NodeList childs2 = an.getChildNodes();
                    if (childs2 == null) continue;

                    for (int o = 0; o < childs.getLength(); o += 1)
					{
                        if (childs2.item(o) == null) continue;
                        if (childs2.item(o).getNodeName().equals("action"))
						{
                            String value = getNodeValue(childs2.item(o), "android:name");
                            if (value.equals(MAIN))
							{
                                activityitem.isMain = true;
                            }
                        }
                    }
                }
            }
            result.add(activityitem);
        }
        return result;
    }


    public Node getFirstActivity()
	{
        NodeList per = doc.getElementsByTagName("activity");
        List<FActivityInfo> info = getActivity();
        for (int i = 0; i < info.size(); i += 1)
		{
            if (info.get(i).isMain)
			{
                return per.item(i);
            }
        }

        return null;
    }

    public boolean setFirstActivity(int id)
	{
        Node FistActivity = null;
        Node node = getFirstActivity();
        if (node == null)
            return false;
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i += 1)
		{
            if (list.item(i).getNodeName().equals("intent-filter"))
			{
                if (list.item(i) == null) continue;
                NodeList action = list.item(i).getChildNodes();
                if (action != null)
				{
                    for (int p = 0; p < action.getLength(); p += 1)
					{
                        String name = getNodeValue(action.item(p), NAME);
                        if (name.equals("android.intent.action.MAIN"))
						{
                            FistActivity = list.item(i);
                            node.removeChild(list.item(i));
                            break;
                        }
                    }
                }

            }
        }
        if (FistActivity == null)
		{
            return false;
        }
        NodeList per = doc.getElementsByTagName("activity");
        per.item(id).appendChild(FistActivity);
        return true;
    }
    public void addActivity(FActivityInfo activity)
	{
        Element item = doc.createElement("activity");
        item.setAttribute("android:name", activity.name);
        if (activity.lable != null)
            item.setAttribute("android:label", activity.lable);
        if (activity.theme != null)
            item.setAttribute("android:theme", activity.theme);
        applicaton.appendChild(item);
    }
    public void RemovePermission(String name)
	{
        List<String> ps = getPermission();
        for (int i = 0; i < ps.size(); i += 1)
		{
            if (name.equals(ps.get(i)))
                RemovePermission(i);
        }
    }
    public boolean RemovePermission(int id)
	{
        NodeList per = doc.getElementsByTagName("uses-permission");
        manifest.removeChild(per.item(id));
        return false;
    }
    Node miniSdk;
    public int getMiniSdk()
	{
        int sdk = 0;
        NodeList list = manifest.getChildNodes();
        for (int i = 0; i < list.getLength(); i += 1)
		{
            if (list.item(i).getNodeName().equals("uses-sdk"))
			{
                String value = AndroidManifestRead.getNodeValue(list.item(i), "android:minSdkVersion");

                if (value != null)
				{
                    sdk = Integer.parseInt(value);

                }

            }
        }
        return sdk;
    }

    public void setMiniSdk(int sdk)
	{
        NodeList list = manifest.getChildNodes();
        for (int i = 0; i < list.getLength(); i += 1)
		{
            if (list.item(i).getNodeName().equals("uses-sdk"))
			{
                setNodeValue(list.item(i), "android:minSdkVersion", "" + sdk);
            }
        }

    }

    public void Save() throws FileNotFoundException
	{
        FileOutputStream fos = new FileOutputStream(path);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        callDomWriter(doc, osw, "UTF-8");
        try
		{
            this.loadData();
        }
		catch (SAXException e)
		{
        }
		catch (ParserConfigurationException e)
		{
        }
		catch (IOException e)
		{
        }
    }


    public static Node getNode(Node n, String attr)
	{
        NamedNodeMap map = n.getAttributes();
        if (map == null)
            return null;
        Node nn = map.getNamedItem(attr);
        if (nn != null)
            return nn;
        return null;
    }


    public static String getNodeValue(Node n, String attr)
	{
        if (n == null) return "";
        NamedNodeMap map = n.getAttributes();
        if (map == null)
            return "";
        Node nn = map.getNamedItem(attr);
        if (nn != null)
            return nn.getNodeValue();
        return "";
    }

    public static void setNodeValue(Node n, String attr, String newname)
	{
        NamedNodeMap map = n.getAttributes();

        Node nn = map.getNamedItem(attr);
        if (nn != null)
            nn.setNodeValue(newname);
    }
    public static void callDomWriter(Document dom, Writer writer, String encoding)
	{
        try
		{
            Source source = new DOMSource(dom);
            Result res = new StreamResult(writer);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, res);
        }
		catch (TransformerConfigurationException e)
		{
            e.printStackTrace();
        }
		catch (TransformerException e)
		{
            e.printStackTrace();
        }
    }
}

