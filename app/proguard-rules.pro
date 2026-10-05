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

# Gson lee y escribe estos modelos por reflexión, así que R8 no puede renombrar sus campos.
# CustomCalculatorData además ya está guardado como JSON por versiones anteriores de la app:
# si cambiaran los nombres, la calculadora guardada no se podría recuperar al actualizar.
-keep class com.donyaep.calnotas.data.model.** { <fields>; <init>(...); }
-keep class com.donyaep.calnotas.data.remote.dto.** { <fields>; <init>(...); }
# Gson 2.10 no trae reglas propias: necesita los genéricos (List<CustomFieldData>) y @SerializedName.
-keepattributes Signature, *Annotation*