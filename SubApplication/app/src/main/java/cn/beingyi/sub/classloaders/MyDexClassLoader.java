package cn.beingyi.sub.classloaders;

import java.lang.reflect.Method;
import dalvik.system.DexClassLoader;

public class MyDexClassLoader extends DexClassLoader {

    public MyDexClassLoader(byte bytes[],
                            String dexPath,
                            String optimizedDirectory,
                            String librarySearchPath,
                            ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);

        createDexClassLoader(bytes,parent);

    }
    private ClassLoader mClassLoader;
    private int mCookie;
    private void createDexClassLoader(byte[] bytes, ClassLoader parent) {
        // android 4.1 DexFile.openDexFile(byte[])
        mClassLoader = parent;
        try {
            // 1. 获取  DexFile 类类型
            Class clz = Class.forName("dalvik.system.DexFile");
            // 2. 获取 openDexFile 方法对象
            Method method = clz.getDeclaredMethod("openDexFile",byte[].class);
            // 3. 调用方法，返回 cookie
            method.setAccessible(true);
            mCookie = (int) method.invoke(null,new Object[]{bytes});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        // android 4.1 DexFile.defineClass(String name, ClassLoader loader, int cookie)
        Class c = null;
        try {
            // 获取加载的类信息
            Class dexFile = Class.forName("dalvik.system.DexFile");
            // 获取静态方法
            Method method = dexFile.getDeclaredMethod("defineClass", String.class, ClassLoader.class, int.class);
            method.setAccessible(true);
            // 调用
            c = (Class)method.invoke(null,name, mClassLoader, mCookie);
            return c;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return super.loadClass(name);
    }
}
