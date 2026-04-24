# ───── 基礎 Android & Kotlin 規則 ─────
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable

# ───── DataBinding ─────
-keep class androidx.databinding.** { *; }
-keep class com.xinooo.qrcode.databinding.** { *; }
-keepclassmembers class * extends androidx.databinding.ViewDataBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# ───── 專案模型類 (避免 DataBinding 或 Gson 找不到欄位) ─────
-keep class com.xinooo.qrcode.data.** { *; }
-keep class com.xinooo.qrcode.core.navigation.NavConfig { *; }

# ───── Room ─────
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# ───── Gson ─────
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ───── ML Kit & CameraX ─────
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ───── ZXing ─────
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ───── AdMob ─────
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# ───── Guava ─────
-dontwarn com.google.common.util.concurrent.ListenableFuture
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.j2objc.annotations.**
