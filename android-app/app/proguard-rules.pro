# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson / modelos de dados
-keep class com.atmosfera.wallpaper.weather.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Room / WorkManager — NÃO REMOVER.
#
# O Room instancia a implementação gerada (`WorkDatabase_Impl`) por REFLEXÃO,
# procurando pelo nome da classe + "_Impl" e chamando o construtor sem
# argumentos. Nada disso é visível pra análise estática do R8.
#
# A regra que o room-runtime 2.5.0 traz sozinho (puxado transitivamente pelo
# work-runtime) é `-keep class * extends androidx.room.RoomDatabase` — SEM
# `{ <init>(); }`. Ela preserva a classe, mas não garante o construtor. O R8
# 8.13.x preservava por conta própria; o R8 9.x (AGP 9.x) NÃO — e o app passa a
# crashar na abertura com:
#
#   RuntimeException: Failed to create an instance of class
#   androidx.work.impl.WorkDatabase
#   (MainActivity.onCreate → WeatherWorker.schedule → WorkManager)
#
# Confirmado em device na tentativa de migração pro AGP 9.4 em 2026-09-18 —
# gate 100% verde e o app não abria. Ver docs/dev/DECISAO-AGP-9-MIGRACAO.md.
#
# Aqui isto é defensivo: no AGP 8.13 atual o app funciona mesmo sem a regra.
# Mas "funciona porque o otimizador foi tolerante" não é garantia nenhuma, e
# quando formos pro AGP 10 (obrigatório algum dia) isto deixa de ser opcional.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
