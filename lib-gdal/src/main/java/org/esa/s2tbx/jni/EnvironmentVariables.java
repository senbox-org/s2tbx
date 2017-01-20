package org.esa.s2tbx.jni;

/**
 * @author Jean Coravu
 */
public class EnvironmentVariables {
    static {
        System.loadLibrary("environment-variables");
    }

    public static native String getEnvironmentVariable(String key);

    public static native int setEnvironmentVariable(String keyEqualValue);
}

/*

#include <jni.h>
#include <stdlib.h>
#include<string.h>
#include "org_esa_s2tbx_jni_EnvironmentVariables.h"

// 1) javac org/esa/s2tbx/jni/EnvironmentVariables.java
// 2) javah org.esa.s2tbx.jni.EnvironmentVariables
// 3) gcc -Wl,--add-stdcall-alias -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -shared -o environment-variables.dll org_esa_s2tbx_jni_EnvironmentVariables.c

JNIEXPORT jstring JNICALL Java_org_esa_s2tbx_jni_EnvironmentVariables_getEnvironmentVariable(JNIEnv *env, jclass thisObj, jstring inJNIKey) {
	// convert the JNI String (jstring) into C-String (char*)
   	const char *inCKey = (*env)->GetStringUTFChars(env, inJNIKey, NULL);
   	if (NULL == inCKey) {
    	return NULL;
	}

	char *existingValue;
    existingValue = getenv(inCKey);

   	// release resources
   	(*env)->ReleaseStringUTFChars(env, inJNIKey, inCKey);

	// convert the C-string (char*) into JNI String (jstring) and return
   	return (*env)->NewStringUTF(env, existingValue);
}


JNIEXPORT jint JNICALL Java_org_esa_s2tbx_jni_EnvironmentVariables_setEnvironmentVariable(JNIEnv * env, jclass thisObj, jstring inJNIKeyEqualValue) {
	// convert the JNI String (jstring) into C-String (char*)
   	const char *inCKeyEqualValue = (*env)->GetStringUTFChars(env, inJNIKeyEqualValue, NULL);
   	if (NULL == inCKeyEqualValue) {
    	return -1;
	}

	jint result = 0;

	if (putenv(inCKeyEqualValue) != 0) {
		// failed to set the environment variable
		result = -2;
	}

   	// release resources
   	(*env)->ReleaseStringUTFChars(env, inJNIKeyEqualValue, inCKeyEqualValue);

   	return result;
}

 */