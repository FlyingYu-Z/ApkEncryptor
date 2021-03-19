

/**
 * 获取上下文
 * @param env
 * @return
 */
static jobject getApplicationContext(JNIEnv *env) {
//    LOGI("getApplication");

    jobject application = NULL;
    jclass activity_thread_clz = env->FindClass("android/app/ActivityThread");
    if (activity_thread_clz != NULL) {
        //LOGI("activity_thread_clz != NULL");
        jmethodID currentApplication = env->GetStaticMethodID(
                activity_thread_clz, "currentApplication", "()Landroid/app/Application;");
        if (currentApplication != NULL) {
            //LOGI("currentApplication != NULL");
            application = env->CallStaticObjectMethod(activity_thread_clz, currentApplication);
        } else {
            //LOGI("Cannot find method: currentApplication() in ActivityThread.");
        }
        env->DeleteLocalRef(activity_thread_clz);
    } else {
//        LOGI("Cannot find class: android.app.ActivityThread");
    }

    return application;
}


/**
 * 获取包名
 * @param env
 * @return
 */
jstring getPackageName(JNIEnv *env) {
    jobject context = getApplicationContext(env);
    if (context == NULL) {
        //LOGE("context is null!");
        return NULL;
    }
    jclass activity = env->GetObjectClass(context);
    jmethodID methodId_pack = env->GetMethodID(activity, "getPackageName", "()Ljava/lang/String;");
    jstring name_str = static_cast<jstring >( env->CallObjectMethod(context, methodId_pack));
    return name_str;
}


/**
 * 获取安装包路径
 * @param env
 * @return
 */
jstring getSourcePath(JNIEnv *env) {
    jobject context = getApplicationContext(env);
    if (context == NULL) {
        //LOGE("context is null!");
        return NULL;
    }
    jmethodID getPackageResourcePath = (env)->GetMethodID(env->GetObjectClass(context), "getPackageResourcePath",
                                                          "()Ljava/lang/String;");
    jstring path = (jstring) (env)->CallObjectMethod(context, getPackageResourcePath);

    return path;
}


static jclass contextClass;
static jclass signatureClass;
static jclass packageNameClass;
static jclass packageInfoClass;

jstring getSign(JNIEnv *env) {

    contextClass = (jclass) env->NewGlobalRef((env)->FindClass("android/content/Context"));
    signatureClass = (jclass) env->NewGlobalRef((env)->FindClass("android/content/pm/Signature"));
    packageNameClass = (jclass) env->NewGlobalRef(
            (env)->FindClass("android/content/pm/PackageManager"));
    packageInfoClass = (jclass) env->NewGlobalRef(
            (env)->FindClass("android/content/pm/PackageInfo"));

    jobject contextObject=getApplicationContext(env);

    jmethodID getPackageManagerId = (env)->GetMethodID(contextClass, "getPackageManager","()Landroid/content/pm/PackageManager;");
    jmethodID getPackageNameId = (env)->GetMethodID(contextClass, "getPackageName","()Ljava/lang/String;");
    jmethodID signToStringId = (env)->GetMethodID(signatureClass, "toCharsString","()Ljava/lang/String;");
    jmethodID getPackageInfoId = (env)->GetMethodID(packageNameClass, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jobject packageManagerObject = (env)->CallObjectMethod(contextObject, getPackageManagerId);
    jstring packNameString = (jstring) (env)->CallObjectMethod(contextObject, getPackageNameId);
    jobject packageInfoObject = (env)->CallObjectMethod(packageManagerObject, getPackageInfoId,packNameString, 64);
    jfieldID signaturefieldID = (env)->GetFieldID(packageInfoClass, "signatures","[Landroid/content/pm/Signature;");
    jobjectArray signatureArray = (jobjectArray) (env)->GetObjectField(packageInfoObject,signaturefieldID);
    jobject signatureObject = (env)->GetObjectArrayElement(signatureArray, 0);
    const char *signStrng = (env)->GetStringUTFChars((jstring) (env)->CallObjectMethod(signatureObject, signToStringId), 0);
    return env->NewStringUTF(signStrng);
}
