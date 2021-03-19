//
// Created by Administrator on 2019/11/18.
//

#include <jni.h>
#include "AppUtils.h"



jobject getGlobalContext(JNIEnv *env)
{
    //获取Activity Thread的实例对象
    jclass activityThread = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    //获取Application，也就是全局的Context
    jmethodID getApplication = env->GetMethodID(activityThread, "getApplication", "()Landroid/app/Application;");
    jobject context = env->CallObjectMethod(at, getApplication);
    return context;
}


jstring getPackname(JNIEnv *env, jobject context)
{
    jclass native_class = env->GetObjectClass(context);
    jmethodID mId = env->GetMethodID(native_class, "getPackageName", "()Ljava/lang/String;");
    jstring packName = static_cast<jstring>(env->CallObjectMethod(context, mId));
    return packName;
}



jstring getSignature(JNIEnv *env, jobject obj)
{
    jclass native_class = env->GetObjectClass(obj);
    jmethodID pm_id = env->GetMethodID(native_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm_obj = env->CallObjectMethod(obj, pm_id);
    jclass pm_clazz = env->GetObjectClass(pm_obj);
// 得到 getPackageInfo 方法的 ID
    jmethodID package_info_id = env->GetMethodID(pm_clazz, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jstring pkg_str = getPackname(env, obj);
// 获得应用包的信息
    jobject pi_obj = env->CallObjectMethod(pm_obj, package_info_id, pkg_str, 64);
// 获得 PackageInfo 类
    jclass pi_clazz = env->GetObjectClass(pi_obj);
// 获得签名数组属性的 ID
    jfieldID signatures_fieldId = env->GetFieldID(pi_clazz, "signatures", "[Landroid/content/pm/Signature;");
    jobject signatures_obj = env->GetObjectField(pi_obj, signatures_fieldId);
    jobjectArray signaturesArray = (jobjectArray)signatures_obj;
    jsize size = env->GetArrayLength(signaturesArray);
    jobject signature_obj = env->GetObjectArrayElement(signaturesArray, 0);
    jclass signature_clazz = env->GetObjectClass(signature_obj);
    jmethodID string_id = env->GetMethodID(signature_clazz, "toCharsString", "()Ljava/lang/String;");
    jstring str = static_cast<jstring>(env->CallObjectMethod(signature_obj, string_id));
    char *c_msg = (char*)env->GetStringUTFChars(str,0);
    //LOGI("signsture: %s", c_msg);
    return str;
}


jstring getLoader(JNIEnv *env,jobject context){

    jclass native_class = env->GetObjectClass(context);
    jmethodID mId = env->GetMethodID(native_class, "getClassLoader", "()Ljava/lang/ClassLoader;");
    jclass loader=env->GetObjectClass(env->CallObjectMethod(context,mId));
    jmethodID mclass = env->GetMethodID(loader, "getClass", "()Ljava/lang/Class;");
    jclass cla=env->GetObjectClass(env->CallObjectMethod(loader,mclass));
    jmethodID mName = env->GetMethodID(cla, "getName", "()Ljava/lang/String;");
    jstring loaderName = static_cast<jstring>(env->CallObjectMethod(cla, mName));

    return loaderName;
}