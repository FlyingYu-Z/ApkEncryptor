#include <jni.h>
#include <md5.h>
#include <string.h>
#include <stdio.h>


jboolean checkHook(JNIEnv *env) {

    jboolean isExists = false;

    jclass jclass1 = env->FindClass("bin/mt/apksignaturekillerplus/HookApplication");
    if (jclass1 == NULL) {
        isExists = false;
    } else {
        isExists = true;
    }

    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    return isExists;


}


void* lookup_symbol(char* libraryname,char* symbolname)
{
    void*imagehandle = dlopen(libraryname,RTLD_GLOBAL|RTLD_NOW);
    if(imagehandle !=NULL){
        void* sym = dlsym(imagehandle, symbolname);
        if(sym !=NULL){
            return sym;
        }
        else{
            //LOGD("(lookup_symbol) dlsym didn‘t work");
            return NULL;
        }
    }
    else{
        //LOGD("(lookup_symbol) dlerror: %s",dlerror());
        return NULL;
    }
}



jobject getCurrentPMSObject(JNIEnv *env){
    jclass activityThreadClass = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThreadMethod = env->GetStaticMethodID(activityThreadClass,"currentActivityThread","()Landroid/app/ActivityThread;");
    jobject currentActivityThread = env->CallStaticObjectMethod(activityThreadClass,currentActivityThreadMethod);
    jfieldID sPackageManagerFieldId = env->GetStaticFieldID(activityThreadClass,"sPackageManager","Landroid/content/pm/IPackageManager;");
    jobject sPackageManager = env->GetStaticObjectField(env->GetObjectClass(currentActivityThread),sPackageManagerFieldId);
    env->DeleteLocalRef(activityThreadClass);
    //env->DeleteLocalRef(currentActivityThreadMethod);
    env->DeleteLocalRef(currentActivityThread);
    return sPackageManager;
}


int isHookPMS(JNIEnv *env){
    jobject cPMSO = getCurrentPMSObject(env);
    jclass cPMSC = env->GetObjectClass(cPMSO);
    jclass cPMSFC =env->GetSuperclass(cPMSC);
    jclass proxyClass = env->FindClass("java/lang/reflect/Proxy");
    if(env->IsAssignableFrom(  cPMSFC,proxyClass)){
        //PMS被Hook
        env->DeleteLocalRef(cPMSO);
        env->DeleteLocalRef( cPMSC);
        env->DeleteLocalRef( cPMSFC);
        env->DeleteLocalRef( proxyClass);
        return 1;
    }else{
        env->DeleteLocalRef( cPMSO);
        env->DeleteLocalRef( cPMSC);
        env->DeleteLocalRef(cPMSFC);
        env->DeleteLocalRef( proxyClass);
        return 0;
    }
}

/**

string ToHexString(const string &input) {
    char *buf = new char[input.length() * 2 + 1];
    memset(buf, 0, input.length() * 2 + 1);

    for (int i = 0; i < input.size(); i++) {
        sprintf(buf + i * 2, "%02x", (uint8_t) input.at(i));
    }

    string res(buf, input.size() * 2);
    delete[] buf;
    return res;
}


string MD5(const string &src) {
    uint8_t signature[16];
    md5((const unsigned char *) src.data(), src.size(), signature);
    string result((char *) signature, 16);
    return ToHexString(result);
}

**/

