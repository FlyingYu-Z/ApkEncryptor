//
// Created by Administrator on 2019/11/19.
//


#include <jni.h>





void showToast(JNIEnv *env,jobject context,jstring str){
    jclass jc_Toast= env->FindClass("android/widget/Toast");
    jmethodID jm_makeText=env->GetStaticMethodID(jc_Toast,"makeText","(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    jobject jo_Toast=env->CallStaticObjectMethod(jc_Toast,jm_makeText,context,str,0);
    jmethodID jm_Show=env->GetMethodID(jc_Toast,"show","()V");
    env->CallVoidMethod(jo_Toast,jm_Show);

}



void show(JNIEnv *env,jobject context,jstring str){

    jclass alert = env->FindClass("cn/beingyi/sub/ui/JniAlert");
    jmethodID contruct = env->GetMethodID(alert, "<init>", "()V");
    jobject alertObj=env->NewObject(alert,contruct);

    jmethodID show_id = env->GetMethodID(env->GetObjectClass(alertObj), "show", "(Landroid/content/Context;Ljava/lang/String;)V");
    env->CallVoidMethod(alertObj,show_id,context,str);

}
