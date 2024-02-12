#include "h2co3Launcher_internal.h"
#include <android/native_window_jni.h>
#include <jni.h>
#include <stdlib.h>

void deleteGlobalRef(JNIEnv *env, jclass *globalRef);

void deleteGlobalRef(JNIEnv *env, jclass *globalRef) {
    if (*globalRef != NULL) {
        (*env)->DeleteGlobalRef(env, *globalRef);
        *globalRef = NULL;
    }
}

void setGlobalRef(JNIEnv *env, jclass *globalRef, jclass localRef) {
    deleteGlobalRef(env, globalRef);
    *globalRef = (*env)->NewGlobalRef(env, localRef);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherActivity_nOnCreate(JNIEnv *env, jobject thiz) {
    if (h2co3Launcher == NULL) {
        return;
    }
    // Get the H2CO3LauncherActivity class
    jclass class_H2CO3LauncherActivity = (*env)->GetObjectClass(env, thiz);
    setGlobalRef(env, &h2co3Launcher->class_H2CO3LauncherActivity, class_H2CO3LauncherActivity);

    // Get the setCursorMode function from the H2CO3LauncherActivity class
    jmethodID setCursorMode = (*env)->GetMethodID(env,
                                                  class_H2CO3LauncherActivity,
                                                  "setCursorMode", "(I)V");
    if (setCursorMode == NULL) {
        H2CO3_INTERNAL_LOG("Failed to get method: setCursorMode.");
        return;
    }
    h2co3Launcher->setCursorMode = setCursorMode;
    h2co3Launcher->class_H2CO3LauncherActivity = (*env)->NewGlobalRef(env, thiz);
}