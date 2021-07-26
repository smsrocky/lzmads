# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mbridge.** {*; }
-keep interface com.mbridge.** {*; }
-keep class android.support.v4.** { *; }
-dontwarn com.mbridge.**
-keep class **.R$* { public static final int mbridge*; }


-dontpreverify
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames #不使用大小写的混淆，Linux系统对大小写敏感。
-optimizationpasses 5

-dontwarn android.**
-dontwarn android.support.v4.**
-dontwarn com.data.carrier.**
-dontwarn com.mbridge.**


-dontwarn com.squareup.**
-dontwarn com.fasterxml.**

-keepattributes Exceptions,InnerClasses,Signature #
-keepattributes EnclosingMethod #清楚应用集成umeng sdk 时 的warning 这个修复不需要更改javac的编译版本，可以保持1.5的版本， 虽然1.5 是官方的一个bug。 之需要在混淆配置里面添加
-keepattributes SourceFile,LineNumberTable #保留源代码的行号
-keep public class * extends android.app.Activity{
    public <fields>;
    public <methods>;
    protected <methods>;
    protected <fields>;
}

-dontnote com.mbridge.**
-dontwarn com.applovin.**
-dontwarn com.facebook.**
-dontwarn com.google.**
-dontwarn com.mopub.**
-dontwarn ru.mail.**

-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.ActivityGroup
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keeppackagenames com.mbridge.msdk

-keep public class * extends android.view.View {
    public <fields>;
    public <methods>;
}

-keep public class * extends android.view.ViewGroup {
    public <fields>;
    public <methods>;
}

-keep public class * extends android.app.Dialog {
    public <fields>;
    public <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
    public <fields>;
    public <methods>;
}

-keep class com.mbridge.msdk.R$*{
   *;
}

-keep class com.android.internal.**.R$*{
   *;
}


-keepclassmembers class **.R$* {
    public static <fields>;
}



-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }