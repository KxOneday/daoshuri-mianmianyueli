# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.timecalendar.app.data.local.entity.** { *; }

# Compose
-dontwarn androidx.compose.**
