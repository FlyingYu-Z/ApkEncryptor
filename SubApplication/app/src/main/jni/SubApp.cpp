
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <AppUtils.cpp>
#include <UIUtils.cpp>
#include <XposedCheck.cpp>
#include <StringUtil.cpp>
#include <MD5.h>
#include <RegMethod.cpp>




jint JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    if (!registerNatives(env)) {
        return -1;
    }

    result = JNI_VERSION_1_4;


    return result;
}



extern "C"
JNIEXPORT void JNICALL
Java_com_test_MainActivity_test(JNIEnv *env, jclass type, jobject context,jstring str) {

    //jstring pkg=getPackname(env,context);


    jclass alert = env->FindClass("cn/beingyi/sub/ui/JniAler");
    clearErr(env);


    jstring pkg=getPackname(env,context);

    jclass loader=env->GetObjectClass(context);
    jmethodID mName = env->GetMethodID(loader, "getName", "()Ljava/lang/String;");
    jstring loaderName = static_cast<jstring>(env->CallObjectMethod(loader, mName));


    showToast(env,context,loaderName);

}




jstring getKey(JNIEnv *env){



}



jstring getKey2(JNIEnv *env){



}






/**

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_beingyi_sub_utils_Native_getKey2(JNIEnv *env, jclass thiz, jobject ctx) {
    jobject context=ctx;
    jstring pkg=getPackname(env,context);

    const char *originStr = JstringToChar(env,pkg);
    MD5 md5 = MD5(originStr);
    std::string md5Result = md5.hexdigest();
    jstring result=env->NewStringUTF(md5Result.c_str());


    if(checkXposed(env)){
        return result;
    }


    char key[16];
    env->GetStringUTFRegion(result,8,16,key);

    StrKey=env->NewStringUTF(key);




    jstring loader=getLoader(env,context);
    char start[1];
    env->GetStringUTFRegion(loader,0,1,start);
    jstring s=env->NewStringUTF(start);
    if(s==env->NewStringUTF("l")){
        StrKey=env->NewStringUTF("");
    }

    if(isI){
        StrKey=env->NewStringUTF("");
    }


    return StrKey;
}

**/








