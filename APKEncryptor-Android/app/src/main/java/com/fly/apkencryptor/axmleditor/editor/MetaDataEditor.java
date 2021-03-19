package com.fly.apkencryptor.axmleditor.editor;


import com.fly.apkencryptor.axmleditor.decode.AXMLDoc;
import com.fly.apkencryptor.axmleditor.decode.BTagNode;
import com.fly.apkencryptor.axmleditor.decode.BXMLNode;
import com.fly.apkencryptor.axmleditor.decode.StringBlock;
import com.fly.apkencryptor.axmleditor.utils.TypedValue;

import java.util.List;

/**
 * 添加或修改 meta-data 信息
 *
 * MetaDataEditor metaDataEditor = new MetaDataEditor(doc);
 * metaDataEditor.setEditorInfo(new MetaDataEditor.EditorInfo("UMENG_CHANNEL", "apkeditor")); // meta-data  name 和value
 * metaDataEditor.commit();
 *
 * Created by zl on 15/9/8.
 */
public class MetaDataEditor extends BaseEditor<MetaDataEditor.EditorInfo> {

    public MetaDataEditor(AXMLDoc doc) {
        super(doc);
        setEditor(NAME,VALUE);
    }

    private int meta_data;


    @Override
    public String getEditorName() {
        return NODE_METADATA;
    }

    @Override
    protected void editor() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode meta_data = (BTagNode) findNode();

        //如果有  直接修改
        if(meta_data != null){
            meta_data.setAttrStringForKey(attr_value, editorInfo.metaValue_Index);
        }else{
            BTagNode.Attribute name_attr = new BTagNode.Attribute(namespace, attr_name, TypedValue.TYPE_STRING);
            name_attr.setString(editorInfo.metaName_Index);
            BTagNode.Attribute value_attr = new BTagNode.Attribute(namespace, attr_value, TypedValue.TYPE_STRING);
            value_attr.setString(editorInfo.metaValue_Index);

            //没有  新建节点插入
            meta_data = new BTagNode(-1, this.meta_data);
            meta_data.setAttribute(name_attr);
            meta_data.setAttribute(value_attr);
            children.add(meta_data);
        }
        doc.getStringBlock().setString(editorInfo.metaValue_Index, editorInfo.metaValue);
    }

    @Override
    protected BXMLNode findNode() {
        BXMLNode application = doc.getApplicationNode(); //manifest node
        List<BXMLNode> children = application.getChildren();

        BTagNode meta_data = null;

        end:for(BXMLNode node : children){
            BTagNode m = (BTagNode)node;
            //it's a risk that the value for "android:name" maybe not String
            if((this.meta_data == m.getName()) && (m.getAttrStringForKey(attr_name) == editorInfo.metaName_Index)){
                meta_data = m;
                break end;
            }
        }
        return meta_data;
    }


    @Override
    protected void registStringBlock(StringBlock sb) {
        namespace = sb.putString(NAME_SPACE);
        meta_data = sb.putString(NODE_METADATA);

        attr_name = sb.putString(NAME);
        attr_value = sb.putString(VALUE);

//        if(metaName_Value != null)
//        meta_name = sb.putString(metaName_Value);


        editorInfo.metaName_Index=sb.putString(editorInfo.metaName);

        editorInfo.metaValue_Index=sb.addString(editorInfo.metaValue);

//        if(metaName_Value !=null && meta_value == -1){
//            if(metaValue_Value == null){
//                metaValue_Value="";
//            }
//
//            meta_value = sb.addString(metaValue_Value);//now we have a seat in StringBlock
//        }
    }

    public static class EditorInfo{
        private String metaName;
        private String metaValue;

        private int metaName_Index;
        private int metaValue_Index;

        private boolean metaNameHasEditor;
        private boolean metaValueHasEditor;

        public EditorInfo(){}

        public EditorInfo(String metaName, String metaValue) {
            this.metaName = metaName;
            this.metaValue = metaValue;
        }
    }
}
