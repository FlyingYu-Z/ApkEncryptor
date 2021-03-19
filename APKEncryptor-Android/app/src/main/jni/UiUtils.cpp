
#include <jni.h>


jobject showToast(JNIEnv *env, jobject thiz, jobject context, jstring str) {

    jclass tclss = env->FindClass("android/widget/Toast");
    jmethodID mid = env->GetStaticMethodID(tclss,"makeText","(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jobject job = env->CallStaticObjectMethod(tclss,mid,context,str);
    jmethodID showId = env->GetMethodID(tclss,"show","()V");
    env->CallVoidMethod(job,showId,context,str);

}


void show(JNIEnv *env,jobject context,jstring str){

    jclass alert = env->FindClass("com/fly/apkencryptor/utils/JniAlert");
    jmethodID contruct = env->GetMethodID(alert, "<init>", "()V");
    jobject alertObj=env->NewObject(alert,contruct);

    jmethodID show_id = env->GetMethodID(env->GetObjectClass(alertObj), "show", "(Landroid/content/Context;Ljava/lang/String;)V");
    env->CallVoidMethod(alertObj,show_id,context,str);

}
