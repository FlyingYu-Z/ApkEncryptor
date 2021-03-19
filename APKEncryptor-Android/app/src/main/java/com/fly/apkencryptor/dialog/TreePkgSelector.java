package com.fly.apkencryptor.dialog;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.adapter.MyAdapter;
import com.fly.apkencryptor.utils.DexUtils;
import com.fly.apkencryptor.utils.ExceptionUtils;
import com.fly.apkencryptor.utils.FileUtils;
import com.fly.apkencryptor.utils.alert;
import com.fly.apkencryptor.widget.ToastUtils;
import com.google.common.collect.Lists;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class TreePkgSelector {

    Context context;
    Activity activity;
    String apkPath;
    ZipFile zipFile;
    List<String> dexEntries = new ArrayList<>();
    HashMap<String, ClassDef> classMap = new HashMap<>();
    Tree tree;
    FrameLayout framelayout;
    List<String> clicked=new ArrayList<>();

    public TreePkgSelector(Context context, String apkPath) throws Exception {
        this.context = context;
        this.activity = (Activity) context;
        this.apkPath = apkPath;
        this.zipFile = new ZipFile(apkPath);
        start();
    }

    private void start() throws Exception {
        readZip(zipFile);

        for (String dexName : dexEntries) {
            DexBackedDexFile dex = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), getZipInputStream(dexName));
            List<ClassDef> classDefs = Lists.newArrayList(dex.getClasses());

            for (ClassDef classDef : classDefs) {
                String type = classDef.getType();
                //String pkg= DexUtils.getPkgNameByType(type);
                classMap.put(type, classDef);
            }
        }
        tree = new Tree(classMap);


    }


    private void list(TreeNode root, List<String> pkgs) {
        for (String pkg : pkgs) {
            IconTreeItem nodeItem = new IconTreeItem(R.mipmap.ic_folder, pkg);
            TreeNode child = new TreeNode(nodeItem).setViewHolder(new MyHolder(context));
            root.addChild(child);
        }

    }

    private View createTree() {
        TreeNode root = TreeNode.root();
        List<String> list = tree.list("/");
        list(root, list);
        AndroidTreeView androidTreeView = new AndroidTreeView(context, root);
        return androidTreeView.getView();
    }


    class MyHolder extends TreeNode.BaseNodeViewHolder<IconTreeItem> {
        public MyHolder(Context context) {
            super(context);
        }

        ImageView img_arrow;

        @Override
        public View createNodeView(TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.item_tree, null, false);
            img_arrow = view.findViewById(R.id.item_tree_ImageView_arrow);
            ImageView img_icon = view.findViewById(R.id.item_tree_ImageView_icon);
            TextView tv_name = view.findViewById(R.id.item_tree_TextView_name);

            img_icon.setImageResource(value.icon);

            if (!value.text.endsWith("/")) {
                img_arrow.setVisibility(View.INVISIBLE);
                img_icon.setImageResource(R.mipmap.ic_clazz);
            }

            node.setClickListener(new TreeNode.TreeNodeClickListener() {
                @Override
                public void onClick(TreeNode node, Object value) {
                    int level = node.getLevel();
                    TreeNode parent = node;
                    String Path = ((IconTreeItem) parent.getValue()).text;
                    for (int i = level; i > 1; i--) {
                        parent = parent.getParent();
                        Path = ((IconTreeItem) parent.getValue()).text+Path;
                    }

                    if(!clicked.contains(Path)) {
                        List<String> list = tree.list(Path);
                        for (String pkg : list) {
                            IconTreeItem nodeItem = new IconTreeItem(R.mipmap.ic_folder, pkg);
                            TreeNode child = new TreeNode(nodeItem).setViewHolder(new MyHolder(context));
                            node.addChild(child);
                        }
                    }

                    clicked.add(Path);


                    ToastUtils.show(Path + level);
                }
            });


            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) img_arrow.getLayoutParams();
            lp.setMargins((node.getLevel() - 1) * 30, 0, 0, 0);
            img_arrow.setLayoutParams(lp);

            if (node.isExpanded()) {

            } else {

            }


            tv_name.setText(value.text);
            return view;
        }


        @Override
        public void toggle(boolean active) {
            if (active) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(img_arrow, "rotation", 0, 90).setDuration(200);
                anim.start();
            } else {
                ObjectAnimator anim = ObjectAnimator.ofFloat(img_arrow, "rotation", 90, 0).setDuration(200);
                anim.start();
            }
        }

    }


    public void show(CallBack callBack) {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = View.inflate(context, R.layout.view_tree_pkg_selector, null);
                framelayout = view.findViewById(R.id.view_tree_pkg_selector_FrameLayout);

                framelayout.addView(createTree());

                Button btn_cancel = view.findViewById(R.id.view_tree_pkg_selector_Button_cancel);
                Button btn_ok = view.findViewById(R.id.view_tree_pkg_selector_Button_ok);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(view)
                        .setCancelable(false)
                        .create();
                dialog.show();


                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        callBack.onCancel();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            List<String> result = new ArrayList<>();

                            dialog.dismiss();
                            callBack.onOK(result);
                        } catch (Exception e) {
                            new alert(context, ExceptionUtils.getExceptionDetail(e));
                        }
                    }
                });


            }
        });

    }

    private void selectAll(List<PkgInfo> processedPkgs, MyAdapter<PkgInfo> adapter) {
        int count = processedPkgs.size();
        int i = 0;
        while (i < count) {
            //framelayout.setItemChecked(i,true);
            i++;
        }
        adapter.notifyDataSetChanged();

    }


    public InputStream getZipInputStream(String entry) throws IOException {

        return new ByteArrayInputStream(FileUtils.toByteArray(zipFile.getInputStream(zipFile.getEntry(entry))));
    }

    private void readZip(java.util.zip.ZipFile zip) throws Exception {
        Enumeration enums = zip.entries();
        while (enums.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) enums.nextElement();
            String entryName = entry.getName();

            if (entryName.startsWith("classes") && entryName.endsWith(".dex")) {
                dexEntries.add(entryName);
            }
        }

    }


    class PkgInfo {
        String PkgName;

    }

    class IconTreeItem {
        public int icon;
        public String text;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }

    }


    public static interface CallBack {
        void onCancel();

        void onOK(List<String> pkgs);
    }


    public class Tree {
        HashMap<String, ClassDef> classMap;
        List<String> classes = new ArrayList<>();

        public Tree(HashMap<String, ClassDef> classMap) {
            this.classMap = classMap;
            for (String key : classMap.keySet()) {
                classes.add(key.substring(1, key.length() - 1));
            }
        }

        public List<String> list(String dir) {
            if (dir.equals("/")) {
                dir = "";
            }
            if (!dir.isEmpty() && !dir.endsWith("/")) {
                dir = dir + "/";
            }
            List<String> result = new ArrayList<>();
            for (String pkg : classes) {
                if (pkg.startsWith(dir)) {
                    String lastAll=pkg.replace(dir, "");
                    String first=lastAll.split("/")[0];
                    if(lastAll.equals(first)) {
                        result.add(first);
                    }else {
                        result.add(first+"/");
                    }
                }

            }
            removeDuplicate(result);
            Collections.sort(result, sortByType);
            return result;
        }

        private void removeDuplicate(List<String> list) {
            HashSet<String> set = new HashSet<String>(list.size());
            List<String> result = new ArrayList<String>(list.size());
            for (String str : list) {
                if (set.add(str)) {
                    result.add(str);
                }
            }
            list.clear();
            list.addAll(result);
        }

        private Comparator<String> sortByType = new Comparator<String>() {
            public int compare(String a, String b) {
                if (isDirectory(a) && !isDirectory(b)) {
                    return -1;
                }
                if (!isDirectory(a) && isDirectory(b)) {
                    return 1;
                }
                return a.toLowerCase().compareTo(b.toLowerCase());
            }
        };

        public boolean isDirectory(String name) {
            return name.endsWith("/");
        }

    }


    public class Tree2 {
        Stack<String> path;
        int dep;
        private List<Map<String, String>> node;
        private Comparator<String> sortByType = new Comparator<String>() {
            public int compare(String a, String b) {
                if (isDirectory(a) && !isDirectory(b)) {
                    return -1;
                }
                if (!isDirectory(a) && isDirectory(b)) {
                    return 1;
                }
                return a.toLowerCase().compareTo(b.toLowerCase());
            }
        };

        public Tree2(HashMap<String, ClassDef> classMap) {
            if (path == null) {
                path = new Stack<String>();
                dep = 0;
            }
            Set<String> names = classMap.keySet();

            node = new ArrayList<Map<String, String>>();
            for (String name : names) {
                String[] token = name.split("/");
                String tmp = "";
                for (int i = 0, len = token.length; i < len; i++) {
                    String value = token[i];
                    if (i >= node.size()) {
                        Map<String, String> map = new HashMap<String, String>();
                        if (classMap.containsKey(tmp + value) && i + 1 == len) {
                            map.put(tmp + value, tmp);
                        } else {
                            map.put(tmp + value + "/", tmp);
                        }
                        node.add(map);
                        tmp += value + "/";
                    } else {
                        Map<String, String> map = node.get(i);
                        if (classMap.containsKey(tmp + value)
                                && i + 1 == len) {
                            map.put(tmp + value, tmp);
                        } else {
                            map.put(tmp + value + "/", tmp);
                        }
                        tmp += value + "/";
                    }
                }
            }
        }

        public List<String> list(String parent) {
            Map<String, String> map = null;
            List<String> str = new ArrayList<String>();
            while (dep >= 0 && node.size() > 0) {
                map = node.get(dep);
                if (map != null) {
                    break;
                }
                pop();
            }
            if (map == null) {
                return str;
            }
            for (String key : map.keySet()) {
                if (parent.equals(map.get(key))) {
                    int index;
                    if (key.endsWith("/")) {
                        index = key.lastIndexOf("/", key.length() - 2);
                    } else {
                        index = key.lastIndexOf("/");
                    }
                    if (index != -1)
                        key = key.substring(index + 1);
                    str.add(key);
                }
            }
            Collections.sort(str, sortByType);

            return str;
        }

        public List<String> list() {
            return list(getCurPath());
        }

        private void push(String name) {
            dep++;
            path.push(name);
        }

        private void set(String name) {
            dep = 0;
            path.empty();
            path.push(name);
        }


        private String pop() {
            if (dep > 0) {
                dep--;
                return path.pop();
            }
            return null;
        }

        public String getCurPath() {
            return join(path, "/");
        }

        public boolean isDirectory(String name) {
            return name.endsWith("/");
        }

        private String join(Stack<String> stack, String d) {
            StringBuilder sb = new StringBuilder("");
            for (String s : stack) {
                sb.append(s);
            }
            return sb.toString();
        }


    }


}
