//
// Created by Administrator on 2019/11/18.
//

#include <string.h>
#include <jni.h>
#include<stdlib.h>

//char to jstring
jstring charToJstring(JNIEnv* env,const char* pat)
{
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("Ljava/lang/String;");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("GB2312");
    //将byte数组转换为java String,并输出
    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
}

//jstring to char*

char* JstringToChar(JNIEnv* env, jstring jstr)
{
    char* rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr= (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0)
    {
        rtn = (char*)malloc(alen + 1);

        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

/**
jstring appenString(JNIEnv *env, jstring s1, jstring s2)
{   const char *s1x = (env)->GetStringUTFChars(s1, NULL);
    const char *s2x = (env)->GetStringUTFChars(s2, NULL);
    char *sall = new char[strlen(s1x) + strlen(s2x) + 1];
    strcpy(sall, s1x);
    strcat(sall, s2x);
    jstring retval = (env)->NewStringUTF(sall);
    (env)->ReleaseStringUTFChars(s1, s1x);
    (env)->ReleaseStringUTFChars(s2, s2x);
    free(sall);
    return retval;
}**/
