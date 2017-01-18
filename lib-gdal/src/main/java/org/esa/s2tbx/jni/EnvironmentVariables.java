package org.esa.s2tbx.jni;

/**
 * @author Jean Coravu
 */
public class EnvironmentVariables {
    static {
        System.loadLibrary("environment-variables");
    }

    public static native void setEnvironmentVariable(String key, String value, String pathSeparator);
}

/*
#include <jni.h>
#include <stdlib.h>
#include<string.h>
#include "org_esa_s2tbx_jni_EnvironmentVariables.h"

JNIEXPORT void JNICALL Java_org_esa_s2tbx_jni_EnvironmentVariables_setEnvironmentVariable(JNIEnv *env, jobject thisObj, jstring inJNIKey, jstring inJNIValue, jstring inJNIPathSeparator) {
	// convert the JNI String (jstring) into C-String (char*)
   	const char *inCKey = (*env)->GetStringUTFChars(env, inJNIKey, NULL);
   	if (NULL == inCKey) {
    	return;
	}
   	const char *inCValue = (*env)->GetStringUTFChars(env, inJNIValue, NULL);
   	if (NULL == inCValue) {
    	return;
	}
   	const char *inCPathSeparator = (*env)->GetStringUTFChars(env, inJNIPathSeparator, NULL);
   	if (NULL == inCPathSeparator) {
    	return;
	}

   	char *existingValue;
    existingValue = getenv(inCKey);

    char equalSign[] = "=";

	int existingValueSize = 0;
	int pathSeparatorSize = 0;
	if (existingValue) {
		existingValueSize = strlen(existingValue);
		pathSeparatorSize = strlen(inCPathSeparator);
	}

	char *string;
    string = malloc(strlen(inCKey) + strlen(equalSign) + strlen(inCValue) + pathSeparatorSize + existingValueSize);
    if (string) {
	    strcpy(string, inCKey);
	    strcat(string, equalSign);
	    strcat(string, inCValue);
	    if (existingValue) {
	    	strcat(string, inCPathSeparator);
	    	strcat(string, existingValue);
	    }

	    if (putenv(string) != 0) {
	    	// failed to set the environment variable
	    }
    }

   	// release resources
   	(*env)->ReleaseStringUTFChars(env, inJNIKey, inCKey);
   	(*env)->ReleaseStringUTFChars(env, inJNIValue, inCValue);
   	(*env)->ReleaseStringUTFChars(env, inJNIPathSeparator, inCPathSeparator);
}
 */