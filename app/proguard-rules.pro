# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android_SDK/tools/proguard/proguard-android.txt
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

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-dontwarn android.support.v4.**

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn android.support.v7.**

#for date time slider
-keep class com.github.jjobes.** { *; }
-keep interface com.github.jjobes.** { *; }
-dontwarn com.github.jjobes.**

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#app42
-keep class com.shephertz.app42.** { *; }
-keep interface com.shephertz.app42.** { *; }
-dontwarn com.shephertz.app42.**

#Permission Dispatcher
-dontwarn com.github.hotchemi.**
-keep class com.github.hotchemi.** { *; }
-keep interface com.github.hotchemi.** { *; }

#AES
-dontwarn com.scottyab.**
-keep class com.scottyab.** { *; }
-keep interface com.scottyab.** { *; }

#yydcdut
-dontwarn com.yydcdut.sdlv.**
-keep class com.yydcdut.sdlv.** { *; }
-keep interface com.yydcdut.sdlv.** { *; }

#commons
-dontwarn org.apache.commons.codec.**
-keep class org.apache.commons.codec.** { *; }
-keep interface org.apache.commons.codec.** { *; }

#-keepattributes *Annotation*

#google play services

-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-keepclassmembers class com.google.android.gms.** {
    *;
 }
-dontwarn com.google.android.gms.*

 -keep class * extends java.util.ListResourceBundle {
     protected Object[][] getContents();
 }

 -keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
     public static final *** NULL;
 }

 -keepnames @com.google.android.gms.common.annotation.KeepName class *
 -keepclassmembernames class * {
     @com.google.android.gms.common.annotation.KeepName *;
 }

 -keepnames class * implements android.os.Parcelable {
     public static final ** CREATOR;
 }

 #sql cipher
 #-dontwarn net.sqlcipher.**
 #-keep class net.sqlcipher.** { *; }
 #-keep interface net.sqlcipher.** { *; }

 #Glide
 -dontwarn com.bumptech.glide.**
 -keep public class * implements com.bumptech.glide.module.GlideModule
 -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
   **[] $VALUES;
   public *;
 }

  #glide circle
  -dontwarn jp.wasabeef.**
  -keep class com.jp.wasabeef.** { *; }
  -keep interface jp.wasabeef.** { *; }

  #permission helper
  -dontwarn com.ayz4sci.androidfactory.**
  -keep class com.ayz4sci.androidfactory.** { *; }
  -keep interface com.ayz4sci.androidfactory.** { *; }