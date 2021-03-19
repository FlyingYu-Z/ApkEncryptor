
#define JNIREG_CLASS "cn/beingyi/sub/utils/Native"

jstring StrKey;
jboolean isI=false;



//获取字符串加密用到的key
__attribute__((section (".fly"))) JNICALL jstring getStr(JNIEnv *env, jclass thiz,jobject ctx) {

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





__attribute__((section (".fly"))) JNICALL jstring getStringc(JNIEnv *env, jclass obj) {
    return (jstring)env-> NewStringUTF("I am string from jni22222");
}



static JNINativeMethod gMethods[] = {
        { "getStringKey", "(Landroid/content/Context;)Ljava/lang/String;", (void*)getStringc},
        { "getKey", "(Landroid/content/Context;)Ljava/lang/String;", (void*)getStr},

};


static int registerNativeMethods(JNIEnv* env, const char* className,
                                 JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}


static int registerNatives(JNIEnv* env)
{
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0])))
        return JNI_FALSE;

    return JNI_TRUE;
}




void clearErr(JNIEnv *env){
    if (env->ExceptionCheck()) {  // 检查JNI调用是否有引发异常
        env->ExceptionDescribe();
        env->ExceptionClear();//清除引发的异常,在Java层不会打印异常的堆栈信息
        //show(env,context,env->NewStringUTF("有异常，且已清除"));

    }else{
        //show(env,context,env->NewStringUTF("无异常"));

    }
}




extern "C"
JNIEXPORT jstring JNICALL
Java_cn_beingyi_sub_utils_Native_getHead(JNIEnv *env, jclass thiz, jobject context,jstring loader) {

    char start[1];
    env->GetStringUTFRegion(loader,0,1,start);
    jstring s=env->NewStringUTF(start);
    if(strcmp(JstringToChar(env,s),JstringToChar(env,env->NewStringUTF("l")))==0){
        isI=true;

        return NULL;
    }else{
        return env->NewStringUTF("");
    }


}
