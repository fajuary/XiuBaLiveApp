#android 官方混淆配置
###########优化精简代码###########
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*  #指定要启用和禁用的优化
-optimizationpasses 5  #压缩比率  0---7
-allowaccessmodification  #指定在预先处理中 类和类成员的访问性可能会扩大
-dontpreverify #指定不预先验证处理类文件
#-dontoptimize
-dontusemixedcaseclassnames  #指定不产生混合的情况下的类名称而混淆
-dontskipnonpubliclibraryclasses
#-dontskipnonpubliclibraryclassmembers    #指定不去忽略包可见的库类的成员。
-verbose
-keepattributes SourceFile,LineNumberTable#保留行号

-keepattributes *Annotation*  #保持注解
-keepattributes Signature
-keepattributes EnclosingMethod
-keep class * extends java.lang.annotation.Annotation { *; }

-keepclasseswithmembernames class * {#指定不混淆所有的jni方法
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {#所有View的 子类 及其getset方法都不混淆
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {#所有的activity子类中 参数类型为view的方法
   public void *(android.view.View);
}

-keepclassmembers enum * { #不混淆枚举类型中的指定方法
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable { #不混淆Parcelable 子类及其Creator成员变量
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class **.R$* {#不混淆R类里及其所有内部static类中的所有static变量字段
    public static <fields>;
}

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity

-dontwarn android.support.** #不提示兼容库的错误警告

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends com.xiu8.base.BaseBean{*;}
-keep public class * extends android.provider.BaseColumns{*;}

-keep public class * extends com.seek.web.AppSeek{
  public *;
}

-keepattributes *JavascriptInterface*
-dontoptimize
-dontpreverify

-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

#谷歌gson
-dontwarn com.google.gson.**
-keep class com.google.gson.** {*;}
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.** {
    <fields>;
    <methods>;
}

# 腾讯相关
-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.** {*;}

#  腾讯相关
-dontwarn com.tencent.stat.**
-keep class com.tencent.stat.**{*;}

# 腾讯相关
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}

# OKIO相关
-dontwarn okio.**
-keep class okio.** {*;}

#新浪微博
-dontwarn com.sina.**
-keep class com.sina.**{*;}

#加密相关 lib
-dontwarn zhibo.common.tools.**
-keep class zhibo.common.tools.** {*;}

#AOP 相关lib
-dontwarn aj.org.**
-keep class aj.org.** {*;}
-dontwarn org.aspectj.**
-keep class org.aspectj.** {*;}
-dontwarn org.objectweb.**
-keep class org.objectweb.** {*;}

#多渠道打包相关
-dontwarn com.mcxiaoke.packer.helper.**
-keep class com.mcxiaoke.packer.helper.** {*;}

#openIsntall
#忽略警告
-dontwarn com.fm.openinstall.**
#避免混淆
-keep public class com.fm.openinstall.* {*; }

#rong - start -
-keepattributes Exceptions,InnerClasses
-keep class io.rong.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**
-ignorewarnings
#rong - end -

#声网
-keep class io.agora.rtc.** { *; }
-dontwarn io.agora.rtc.**

#
-dontwarn android.arch.**
-keep class android.arch.** {*;}

# oss
-dontwarn com.alibaba.**
-keep class com.alibaba.** {*;}

-dontwarn android.support.**
-keep class android.support.** { *; }

-dontwarn com.czt.**
-keep class com.czt.** { *; }

-dontwarn com.github.**
-keep class com.github.** { *; }

-dontwarn com.bumptech.**
-keep class com.bumptech.** { *; }

-dontwarn com.chad.**
-keep class com.chad.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *; }

-dontwarn com.jakewharton.**
-keep class com.jakewharton.** { *; }

-dontwarn com.scwang.**
-keep class com.scwang.** { *; }

-dontwarn com.seek.adaper.**
-keep class com.seek.adaper.** { *; }

-dontwarn com.http.**
-keep class com.http.** { *; }

-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }

# 听云ProGuard configurationsfor NetworkBench Lens
-keep classcom.networkbench.** { *; }
-dontwarncom.networkbench.**
-keepattributesExceptions, Signature, InnerClasses
# End NetworkBench Lens