package cn.beingyi.sub.apps.SubApp;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.beingyi.sub.apps.BaseApplication;
import cn.beingyi.sub.apps.AppUtils.SubApp.utils.Utils;
import cn.beingyi.sub.utils.APKUtils;
import cn.beingyi.sub.utils.BYProtectUtils;
import cn.beingyi.sub.utils.ByteEncoder;
import cn.beingyi.sub.utils.CheckUtils;
import cn.beingyi.sub.utils.CrashHandler;
import cn.beingyi.sub.utils.CustomFun;
import cn.beingyi.sub.utils.FileUtils;
import cn.beingyi.sub.utils.ListUtil;
import cn.beingyi.sub.utils.SelfInfo;

import static cn.beingyi.sub.utils.FileUtils.copyAssetsFile;
import static cn.beingyi.sub.utils.FileUtils.toByteArray;

public class SubApplication extends BaseApplication {

    public final static String TAG = SubApplication.class.getName();
    List<File> dexFiles;
    File dexDir;
    File optDir;
    String applcationName;


    boolean checkVirtual;
    boolean checkXposed;
    boolean checkRoot;
    boolean checkVPN;


    @Override
    protected void attachBaseContext(Context base) {
        dexFiles = new ArrayList<>();
        dexDir=base.getDir("ded",MODE_PRIVATE);
        optDir=new File(base.getFilesDir(),"opt");


        CustomFun.deleteFloor(dexDir.getAbsolutePath());
        FileUtils.mkdir(dexDir.getAbsolutePath());
        FileUtils.mkdir(optDir.getAbsolutePath());

        try {
            ZipFile zipFile = new ZipFile(getApkPath(base));
            String key=APKUtils.getPkgName(base,getApkPath(base));
            String conf=new String(ByteEncoder.Decrypt(FileUtils.readZipByte(base,"src/"+ BYProtectUtils.getAssetsName("application")),key),"UTF-8");
            //String conf=new String(FileUtils.readZipByte(base,"src/"+ BYProtectUtils.getAssetsName("application")),"UTF-8");

            //if(zipFile.getEntry("classes2.dex")!=null){
            //    CustomFun.exit();
            //}

            FileUtils.mkdir(dexDir.getAbsolutePath());

            JSONObject jsonObject=new JSONObject(conf);
            applcationName=jsonObject.getString("application");
            checkVirtual=jsonObject.getBoolean("checkVirtual");
            checkXposed=jsonObject.getBoolean("checkXposed");
            checkRoot=jsonObject.getBoolean("checkRoot");
            checkVPN=jsonObject.getBoolean("checkVPN");



            List<String> dexs= ListUtil.StringToList(jsonObject.getString("dex"));
            for(int i=0;i<dexs.size();i++) {
                ZipEntry zipEntry = zipFile.getEntry("src/" + dexs.get(i));
                InputStream inputStream = zipFile.getInputStream(zipEntry);
                File file = new File(dexDir, getRandomString(32) + ".dex");
                byte[] decrypt = xorEncode(toByteArray(inputStream),key);

                FileUtils.saveFile(decrypt,file.getAbsolutePath());

                dexFiles.add(file);
            }

            try {
                //Class.forName(jsonObject.getString("sub"));
            }catch (Exception e){
                //applcationName=jsonObject.getString("sub");
            }


        } catch (Exception e) {
            e.printStackTrace();
            try {
                //FileUtils.writeFile(FileUtils.getSDPath()+"/ss.txt",e.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        super.attachBaseContext(base);



        try {
            //添加无效代码，对抗jadx & jd-gui
            boolean qwerty21345hjdnjd = false;
            while (qwerty21345hjdnjd) {
                switch (1) {
                    case 1:
                        while (qwerty21345hjdnjd) {
                            try {
                                Throwable throwable=new Throwable();
                                Throwable cause = throwable.getCause();
                            } catch (NullPointerException e) {
                            } finally {
                            }
                        }
                        break;
                }
            }


            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable = new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }

            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable = new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }

            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable = new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }

            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable = new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }

            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable = new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }

            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable = new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }

            switch (1) {
                case 1:
                    loadDexByVersion();
                    try {
                        CustomFun.deleteFloor(dexDir.getAbsolutePath());
                        for (File file : dexFiles) {
                            file.delete();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }



        } catch (Exception e) {
            e.printStackTrace();
            try {
                //FileUtils.writeFile(FileUtils.getSDPath()+"/ss.txt",e.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }


    private void loadDexByVersion() throws Exception{
        ClassLoader classLoader = getClassLoader();

        //添加无效代码，对抗jadx & jd-gui
        boolean qwerty21345hjdnjd = false;
        while (qwerty21345hjdnjd) {
            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable=new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }
        }

        //如果大于api 23，即6.0以上
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            Field pathListField = Utils.findField(classLoader, "pathList");
            Object pathList = pathListField.get(classLoader);

            Field dexElementsField = Utils.findField(pathList, "dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(pathList);

            Method makeDexElements = Utils.findMethod(pathList, "makePathElements", List.class, File.class, List.class);

            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            Object[] addElements = (Object[]) makeDexElements.invoke(pathList, dexFiles, optDir, suppressedExceptions);

            //合并数组
            Object[] newElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(), dexElements.length + addElements.length);
            System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
            System.arraycopy(addElements, 0, newElements, dexElements.length, addElements.length);

            //替换classloader中的element数组
            dexElementsField.set(pathList, newElements);
            return;
        }

        //添加无效代码，对抗jadx & jd-gui
        while (qwerty21345hjdnjd) {
            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable=new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }
        }

        //5.0 - 6.0 api 21-22
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M){

            Field pathListField = Utils.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            expandFieldArray(dexPathList, "dexElements", v19.makeDexElements(dexPathList, new ArrayList<File>(dexFiles), optDir, suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);
                }
                Field suppressedExceptionsField =
                        Utils.findField(classLoader, "dexElementsSuppressedExceptions");
                IOException[] dexElementsSuppressedExceptions =
                        (IOException[]) suppressedExceptionsField.get(classLoader);

                if (dexElementsSuppressedExceptions == null) {
                    dexElementsSuppressedExceptions =
                            suppressedExceptions.toArray(
                                    new IOException[suppressedExceptions.size()]);
                } else {
                    IOException[] combined =
                            new IOException[suppressedExceptions.size() +
                                    dexElementsSuppressedExceptions.length];
                    suppressedExceptions.toArray(combined);
                    System.arraycopy(dexElementsSuppressedExceptions, 0, combined,
                            suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                    dexElementsSuppressedExceptions = combined;
                }

                suppressedExceptionsField.set(classLoader, dexElementsSuppressedExceptions);
            }

            return;
        }

        //添加无效代码，对抗jadx & jd-gui
        while (qwerty21345hjdnjd) {
            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable=new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }
        }

        //4.4 api 19
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){

            Field pathListField = Utils.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            expandFieldArray(dexPathList, "dexElements", v19.makeDexElements(dexPathList,
                    new ArrayList<File>(dexFiles), optDir,
                    suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);
                }
                Field suppressedExceptionsField =
                        Utils.findField(classLoader, "dexElementsSuppressedExceptions");
                IOException[] dexElementsSuppressedExceptions =
                        (IOException[]) suppressedExceptionsField.get(classLoader);

                if (dexElementsSuppressedExceptions == null) {
                    dexElementsSuppressedExceptions =
                            suppressedExceptions.toArray(
                                    new IOException[suppressedExceptions.size()]);
                } else {
                    IOException[] combined =
                            new IOException[suppressedExceptions.size() +
                                    dexElementsSuppressedExceptions.length];
                    suppressedExceptions.toArray(combined);
                    System.arraycopy(dexElementsSuppressedExceptions, 0, combined,
                            suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                    dexElementsSuppressedExceptions = combined;
                }

                suppressedExceptionsField.set(classLoader, dexElementsSuppressedExceptions);
            }

            return;
        }

        //添加无效代码，对抗jadx & jd-gui
        while (qwerty21345hjdnjd) {
            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable=new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }
        }

        //如果api大于等于14，小于等于18，即4.0-4.3
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){

            Field pathListField = Utils.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            expandFieldArray(dexPathList, "dexElements", v14_18.makeDexElements(dexPathList, new ArrayList<File>(dexFiles), optDir));

            return;
        }

        //添加无效代码，对抗jadx & jd-gui
        while (qwerty21345hjdnjd) {
            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable=new Throwable();
                            Throwable cause = throwable.getCause();
                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }
        }


    }


    //异或加密
    public static byte[] xorEncode(byte[] data,String key){
        byte[] keyBytes=key.getBytes();

        byte[] encryptBytes=new byte[data.length];
        for(int i=0; i<data.length; i++){
            encryptBytes[i]=(byte) (data[i]^keyBytes[i%keyBytes.length]);
        }
        return encryptBytes;
    }


    private static class v19{

        private static Object[] makeDexElements(Object dexPathList, ArrayList<File> files, File optimizedDirectory, ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements = Utils.findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class, ArrayList.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions);
        }

    }

    private static class v14_18 {

        private static Object[] makeDexElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements =
                    Utils.findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files, optimizedDirectory);
        }


    }

    private static void expandFieldArray(Object instance, String fieldName, Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field jlrField = Utils.findField(instance, fieldName);
        Object[] original = (Object[]) jlrField.get(instance);
        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + extraElements.length);
        System.arraycopy(original, 0, combined, 0, original.length);
        System.arraycopy(extraElements, 0, combined, original.length, extraElements.length);
        jlrField.set(instance, combined);
    }


    @Override
    public void onCreate() {
        super.onCreate();


        try {
            bindRealApplicatin();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    /**
     * 让代码走入if中的第三段中
     *
     * @return
     */
    @Override
    public String getPackageName() {
        if (!TextUtils.isEmpty(applcationName)) {
            return "";//#
        }
        return super.getPackageName();
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (TextUtils.isEmpty(applcationName)) {
            return super.createPackageContext(packageName, flags);
        }
        try {
            bindRealApplicatin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delegate;

    }

    boolean isBindReal;
    Application delegate;

    private void bindRealApplicatin() throws Exception {
        if (isBindReal) {
            return;
        }
        if (TextUtils.isEmpty(applcationName)) {
            return;
        }

        //添加无效代码，对抗jadx & jd-gui
        boolean qwerty21345hjdnjd = false;
        while (qwerty21345hjdnjd) {
            switch (1) {
                case 1:
                    while (qwerty21345hjdnjd) {
                        try {
                            Throwable throwable=new Throwable();
                            Throwable cause = throwable.getCause();

                            while (qwerty21345hjdnjd){
                                switch (2){
                                    case 2:
                                        while (qwerty21345hjdnjd){
                                            while (qwerty21345hjdnjd){
                                                while (qwerty21345hjdnjd){
                                                    try{
                                                        Throwable throwable2=new Throwable();
                                                        Throwable cause2 = throwable.getCause();
                                                        switch (2){

                                                        }
                                                    }catch (Exception e){
                                                        try{
                                                            switch (2){

                                                            }
                                                        }catch (Exception e2){

                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        break;
                                }
                            }

                        } catch (NullPointerException e) {
                        } finally {
                        }
                    }
                    break;
            }
        }

        //得到attachBaseContext(context) 传入的上下文 ContextImpl
        Context baseContext = getBaseContext();
        //创建用户真实的application (MyApplication)
        Class<?> delegateClass = Class.forName(applcationName);
        delegate = (Application) delegateClass.newInstance();
        //得到attach()方法
        Method attach = Application.class.getDeclaredMethod("attach", Context.class);//#
        attach.setAccessible(true);
        attach.invoke(delegate, baseContext);


        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");//#
        //获取mOuterContext属性
        Field mOuterContextField = contextImplClass.getDeclaredField("mOuterContext");//#
        mOuterContextField.setAccessible(true);
        mOuterContextField.set(baseContext, delegate);
        Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");//#
        mMainThreadField.setAccessible(true);
        Object mMainThread = mMainThreadField.get(baseContext);

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");//#
        Field mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication");//#
        mInitialApplicationField.setAccessible(true);
        mInitialApplicationField.set(mMainThread, delegate);
        Field mAllApplicationsField = activityThreadClass.getDeclaredField("mAllApplications");//#
        mAllApplicationsField.setAccessible(true);
        ArrayList<Application> mAllApplications = (ArrayList<Application>) mAllApplicationsField.get(mMainThread);
        mAllApplications.remove(this);
        mAllApplications.add(delegate);
        Field mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo");//#
        mPackageInfoField.setAccessible(true);
        Object mPackageInfo = mPackageInfoField.get(baseContext);

        Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");//#
        Field mApplicationField = loadedApkClass.getDeclaredField("mApplication");//#
        mApplicationField.setAccessible(true);
        mApplicationField.set(mPackageInfo, delegate);
        Field mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo");//#
        mApplicationInfoField.setAccessible(true);
        ApplicationInfo mApplicationInfo = (ApplicationInfo) mApplicationInfoField.get(mPackageInfo);
        mApplicationInfo.className = applcationName;

        switch (1) {
            case 1:
                while (qwerty21345hjdnjd) {
                    try {
                        Throwable throwable=new Throwable();
                        Throwable cause = throwable.getCause();
                    } catch (NullPointerException e) {
                    } finally {
                    }
                }
                break;
        }

        delegate.onCreate();
        isBindReal = true;

        new Thread(){
            @Override
            public void run(){

                while (true){
                    try{
                        Thread.sleep(1000);


                        if(checkVirtual){
                            try {
                                CheckUtils.Virtual.main(delegate.getApplicationContext());
                            }catch (Exception e){

                            }
                        }

                        if(checkXposed){
                            try {
                                CheckUtils.Xposed.main(delegate.getApplicationContext());
                            }catch (Exception e){

                            }
                        }

                        if(checkRoot){
                            try{
                                CheckUtils.Root.main(delegate.getApplicationContext());
                            }catch (Exception e){

                            }
                        }

                        if(checkVPN){
                            try {
                                CheckUtils.VPN.main(delegate.getApplicationContext());
                            }catch (Exception e){

                            }
                        }

                    }catch (Exception e){

                    }

                }

            }
        }.start();

        create();
    }



    public void create(){

        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(getApplicationContext());


    }




    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";//#
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


}
