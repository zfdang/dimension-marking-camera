# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Room entities
-keep class com.dimensioncam.data.model.** { *; }

# Keep data classes
-keepclassmembers class com.dimensioncam.** {
    <init>(...);
}
