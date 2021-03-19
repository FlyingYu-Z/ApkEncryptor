


#include <jni.h>

/**
 * 检测xposed
 * checkXposed
 */
jboolean checkXposed(JNIEnv *env) {
    //找到ClassLoader类
    jclass classloaderClass = env->FindClass("java/lang/ClassLoader");
    //找到ClassLoader类中的静态方法getSystemClassLoader
    jmethodID getSysLoaderMethod = env->GetStaticMethodID(classloaderClass, "getSystemClassLoader",
                                                          "()Ljava/lang/ClassLoader;");
    //调用ClassLoader中的getSystemClassLoader方法，返回ClassLoader对象
    jobject classLoader = env->CallStaticObjectMethod(classloaderClass, getSysLoaderMethod);
    //DexClassLoader：能够加载自定义的jar/apk/dex
    //PathClassLoader：只能加载系统中已经安装过的apk
    jclass dexLoaderClass = env->FindClass("dalvik/system/DexClassLoader");
    //找到ClassLoader中的方法loadClass
    jmethodID loadClass = env->GetMethodID(dexLoaderClass, "loadClass",
                                           "(Ljava/lang/String;)Ljava/lang/Class;");
    //调用DexClassLoader的loadClass方法，加载需要调用的类
    jstring dir = env->NewStringUTF("de.robv.android.xposed.XposedBridge");
    jclass targetClass = (jclass) env->CallObjectMethod(classLoader, loadClass, dir);

    if (env->ExceptionCheck()) {  // 检查JNI调用是否有引发异常
        env->ExceptionDescribe();
        env->ExceptionClear();        // 清除引发的异常，在Java层不会打印异常的堆栈信息
        // env->ThrowNew(env->FindClass("java/lang/Exception"), "JNI抛出的异常！");
        //LOGD("error! not found");
        return false;
    }

    if (targetClass != NULL) {
        jfieldID disableHooksFiled = env->GetStaticFieldID(targetClass, "disableHooks", "Z");
        env->SetStaticBooleanField(targetClass, disableHooksFiled, true);
        jfieldID runtimeFiled = env->GetStaticFieldID(targetClass, "runtime", "I");
        env->SetStaticIntField(targetClass, runtimeFiled, 2);
        return true;
    } else {
        return false;
    }
}
