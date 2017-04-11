package org.esa.s2tbx.jni;

/**
 * @author Jean Coravu
 */
public class EnvironmentVariablesNative {
    static {
        System.loadLibrary("environment-variables");
    }

    public static native int chdir(String dir);

    public static native String getcwd();

    public static native String getenv(String key);

    public static native int putenv(String keyEqualValue);
}

/*
 1) javac org/esa/s2tbx/jni/EnvironmentVariablesNative.java

 2) javah org.esa.s2tbx.jni.EnvironmentVariablesNative

 3a) gcc -Wl,--add-stdcall-alias -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -shared -o environment-variables.dll org_esa_s2tbx_jni_EnvironmentVariablesNative.c

 3b) gcc -fPIC -I{$JAVA_HOME}/include -I{$JAVA_HOME}/include/linux -shared -o environment-variables.so org_esa_s2tbx_jni_EnvironmentVariablesNative.c
*/
