
#--- 基础混淆配置 ---
-optimizationpasses 5  #指定代码的压缩级别
-allowaccessmodification  #优化时允许访问并修改有修饰符的类和类的成员

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses  #指定不去忽略非公共库的类



# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

-verbose    #混淆时是否记录日志

-ignorewarnings  #忽略警告，避免打包时某些警告出现，没有这个的话，构建报错

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*  #混淆时所采用的算法

-keepattributes *Annotation* #不混淆注解相关

-keepclasseswithmembernames class * {  #保持 native 方法不被混淆
    native <methods>;
}

-keepclassmembers enum * {  #保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#不混淆Parcelable
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

#不混淆Serializable
-keep class * implements java.io.Serializable {*;}
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {*;}



-keepclassmembers class **.R$* { #不混淆R文件
    public static <fields>;
}

#不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify


-keepattributes Signature  #过滤泛型  出现类型转换错误时，启用这个




#--- 不能被混淆的基类 ---
#-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class org.xmlpull.v1.** { *; }



#--- 不混淆android-support-v4包 ---
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class * extends android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v4.widget
-keep class * extends android.support.v4.app.** {*;}
-keep class * extends android.support.v4.view.** {*;}
-keep public class * extends android.support.v4.app.Fragment


#不混淆继承的support类
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**



-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keep resource xml elements manifest/application/meta-data@value=GlideModule

#不混淆log
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}


#保持Activity中参数类型为View的所有方法
-keepclassmembers class * extends android.app.Activity {
          public void *(android.view.View);
    }

-keepclassmembers class * extends android.support.v7.app.AppCompatActivity {
          public void *(android.view.View);
    }


#Glide图片库
 -keep class com.bumptech.glide.**{*;}
-keep class * extends java.lang.annotation.Annotation { *; }

 #有用到WEBView的JS调用接口不被混淆
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
        public *;
   }

#对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
       void *(**On*Event);
       void *(**On*Listener);
   }



#抛出异常时保留代码行号 方便测试
#-keepattributes SourceFile,LineNumberTable





-dontwarn org.xutils.**
-dontwarn org.**
#-dontwarn java.**
-keep public class android.**
-keep public class com.android.**



-keep class com.readystatesoftware.** {*;}
-keep class android.** {*;}
-keep class sun.** {*;}
-keep class sun1.** {*;}

#-keep public class cn.beingyi.subapplication.apps.StringApp.utils.StringDecryptor { *; }

-keep public class cn.beingyi.sub.apps.SubApp.SubApplication
-keep public class io.beingyi.Flying
-keep public class io.beingyi.BaseActivity { *; }
-keep public class io.beingyi.Test { *; }

-keep public class cn.beingyi.sub.ui.JniAlert { *; }
-dontshrink

-classobfuscationdictionary ./proguard-keys.txt
-packageobfuscationdictionary ./proguard-keys.txt
-obfuscationdictionary ./proguard-keys.txt