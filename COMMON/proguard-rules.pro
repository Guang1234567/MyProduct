# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\dev_kit\android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn rx.**
-keep class rx.** { *; }

-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okio.** { *; }
-keep class okhttp3.** { *; }

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# Auto-value-Parcel
-keep class **AutoValue_*$1 { }
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
