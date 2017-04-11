package org.esa.s2tbx.jni;

/**
 * @author Jean Coravu
 */
public class EnvironmentVariables {
    public static void changeCurrentDirectory(String dir) {
        int result = EnvironmentVariablesNative.chdir(dir);
        if (result != 0) {
            throw new RuntimeException("Unable to set change the current directory: " + String.valueOf(result));
        }
    }

    public static String getEnvironmentVariable(String key) {
        return EnvironmentVariablesNative.getenv(key);
    }

    public static void setEnvironmentVariable(String keyEqualValue) {
        int result = EnvironmentVariablesNative.putenv(keyEqualValue);
        if (result != 0) {
            throw new RuntimeException("Unable to set environment variable: " + String.valueOf(result));
        }
    }

}
