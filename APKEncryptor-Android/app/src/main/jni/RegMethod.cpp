
#define JNIREG_CLASS "com/fly/apkencryptor/utils/Native"


__attribute__((section (".fly"))) JNICALL void init(JNIEnv *env, jclass thiz, jobject context,jstring opcode) {

    return;
}


__attribute__((section (".fly"))) JNICALL void start(JNIEnv *env, jclass thiz, jobject context) {
    //show(env,context,getSign(env));


    return;
}



__attribute__((section (".fly"))) JNICALL jstring getKey(JNIEnv *env, jclass thiz) {

    return getSign(env);
}


static JNINativeMethod gMethods[] = {
        { "init", "(Landroid/content/Context;Ljava/lang/String;)V", (void*)init},
        { "start", "(Landroid/content/Context;)V", (void*)start},
        { "getKey", "()Ljava/lang/String;", (void*)getKey}

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
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods,sizeof(gMethods) / sizeof(gMethods[0])))
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

