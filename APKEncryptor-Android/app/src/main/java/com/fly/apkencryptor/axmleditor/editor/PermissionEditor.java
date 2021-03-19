package com.fly.apkencryptor.axmleditor.editor;


import com.fly.apkencryptor.axmleditor.decode.AXMLDoc;
import com.fly.apkencryptor.axmleditor.decode.BTagNode;
import com.fly.apkencryptor.axmleditor.decode.BXMLNode;
import com.fly.apkencryptor.axmleditor.decode.StringBlock;
import com.fly.apkencryptor.axmleditor.utils.TypedValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 添加或删除apk权限
 *
 * 用法
 *  PermissionEditor permissionEditor=new PermissionEditor(doc);
 *  PermissionEditor.EditorInfo editorInfo=new PermissionEditor.EditorInfo();
 *  editorInfo.with(new PermissionEditor.PermissionOpera("android.permission.INTERNET").remove());  //删除权限
 *  editorInfo.with(new PermissionEditor.PermissionOpera("android.permission.WRITE_SETTINGS").add()); //添加权限
 *  //and more ...
 *  permissionEditor.setEditorInfo(editorInfo);
 *
 *
 * Created by zl on 15/9/9.
 */
public class PermissionEditor extends BaseEditor<PermissionEditor.EditorInfo> {
    public PermissionEditor(AXMLDoc doc) {
        super(doc);
    }

    private int user_permission;

    @Override
    public String getEditorName() {
        return NODE_USER_PREMISSION;
    }

    @Override
    protected void editor() {
        List<BXMLNode> children = findNode().getChildren();
        for (PermissionOpera opera:editorInfo.editors){
            if(opera.isRemove()){
                final Iterator<BXMLNode> iterator = children.iterator();
                while (iterator.hasNext()){
                    BTagNode n= (BTagNode) iterator.next();
                    if((user_permission == n.getName()) && (n.getAttrStringForKey(attr_name) == opera.permissionValue_Index)){
                        System.out.println("删除  -->>> " + opera.permission);
                        iterator.remove();
                        //doc.getStringBlock().removeString(opera.permissionValue_Index);
                        break;
                    }
                }
            }else if(opera.isAdd()){
                BTagNode.Attribute permission_attr = new BTagNode.Attribute(namespace, attr_name, TypedValue.TYPE_STRING);
                permission_attr.setString(opera.permissionValue_Index);
                BTagNode permission_node = new BTagNode(-1, user_permission);
                permission_node.setAttribute(permission_attr);
                children.add(permission_node);
                System.out.println("添加 -->>  "+opera.permission);
                doc.getStringBlock().setString(opera.permissionValue_Index, opera.permission);
            }
        }
    }


    @Override
    protected BXMLNode findNode() {
        return doc.getManifestNode();
    }

    @Override
    protected void registStringBlock(StringBlock sb) {
        namespace = sb.putString(NAME_SPACE);
        user_permission = sb.putString(NODE_USER_PREMISSION);

        attr_name = sb.putString(NAME);

        final Iterator<PermissionOpera> iterator = editorInfo.editors.iterator();
        while (iterator.hasNext()){
            final PermissionOpera opera = iterator.next();
            if(opera.isAdd()){
                if(sb.containsString(opera.permission)){
                    iterator.remove(); //添加，已经有了不处理
                }else {
                    opera.permissionValue_Index=sb.addString(opera.permission);
                }
            }else if(opera.isRemove()){
                if(!sb.containsString(opera.permission)){
                    iterator.remove(); //删除，没有不处理
                }else {
                   opera.permissionValue_Index= sb.getStringMapping(opera.permission);
                }
            }
        }
    }

    public static class EditorInfo {
        private List<PermissionOpera> editors=new ArrayList<PermissionOpera>();

        public final EditorInfo with(PermissionOpera opera){
            editors.add(opera);
            return this;
        }
    }

    public static class PermissionOpera{
        private static final int ADD=0x00000001;
        private static final int REMOVE=0x00000002;

        private int opera=0x00000000;

        private String permission;

        private int permissionValue_Index;

        public PermissionOpera(String permission){
            this.permission=permission;
        }

        public final PermissionOpera add(){
            opera = opera & ~ REMOVE; //移除remove 标记
            opera = opera | ADD;   //添加add 标记
            return this;
        }

        public final PermissionOpera remove(){
            opera = opera & ~ ADD;
            opera = opera | REMOVE;
            return this;
        }

        final boolean isAdd(){
            return (opera & ADD) == ADD;
        }

        final boolean isRemove(){
            return (opera & REMOVE) == REMOVE;
        }
    }
}
