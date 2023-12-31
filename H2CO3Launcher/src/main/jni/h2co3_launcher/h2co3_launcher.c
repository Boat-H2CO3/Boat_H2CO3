#include "h2co3_launcher_internal.h"
#include <android/native_window_jni.h>
#include <jni.h>
#include <stdlib.h>

struct H2CO3LauncherInternal *h2co3Launcher = NULL;

void releaseResources() {
    if (h2co3Launcher != NULL) {
        if (h2co3Launcher->window != NULL) {
            ANativeWindow_release(h2co3Launcher->window);
            h2co3Launcher->window = NULL;
        }
        if (h2co3Launcher->class_H2CO3LauncherLib != NULL) {
            JNIEnv *env = NULL;
            jint result = (*h2co3Launcher->android_jvm)->AttachCurrentThread(h2co3Launcher->android_jvm, &env, NULL);
            if (result == JNI_OK && env != NULL) {
                (*env)->DeleteGlobalRef(env, h2co3Launcher->class_H2CO3LauncherLib);
            }
            h2co3Launcher->class_H2CO3LauncherLib = NULL;
        }
        if (h2co3Launcher->class_H2CO3LauncherActivity != NULL) {
            JNIEnv *env = NULL;
            jint result = (*h2co3Launcher->android_jvm)->AttachCurrentThread(h2co3Launcher->android_jvm, &env, NULL);
            if (result == JNI_OK && env != NULL) {
                (*env)->DeleteGlobalRef(env, h2co3Launcher->class_H2CO3LauncherActivity);
            }
            h2co3Launcher->class_H2CO3LauncherActivity = NULL;
        }
        free(h2co3Launcher);
        h2co3Launcher = NULL;
    }
}

void logError(const char *message) {
    H2CO3_INTERNAL_LOG("%s", message);
    releaseResources();
}

ANativeWindow *h2co3GetNativeWindow() {
    if (h2co3Launcher == NULL) {
        return NULL;
    }
    return h2co3Launcher->window;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_setH2CO3LauncherNativeWindow(JNIEnv *env,
                                                                                      jclass clazz,
                                                                                      jobject surface) {
    if (h2co3Launcher == NULL) {
        return;
    }
    if (surface == NULL) {
        logError("setH2CO3LauncherNativeWindow : surface is NULL");
        return;
    }
    if (h2co3Launcher->window != NULL) {
        ANativeWindow_release(h2co3Launcher->window);
        h2co3Launcher->window = NULL;
    }
    h2co3Launcher->window = ANativeWindow_fromSurface(env, surface);
    H2CO3_INTERNAL_LOG("setH2CO3LauncherNativeWindow : %p", h2co3Launcher->window);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    h2co3Launcher = (struct H2CO3LauncherInternal *) malloc(sizeof(struct H2CO3LauncherInternal));
    if (h2co3Launcher == NULL) {
        return JNI_ERR;
    }
    memset(h2co3Launcher, 0, sizeof(struct H2CO3LauncherInternal));
    h2co3Launcher->android_jvm = vm;
    JNIEnv *env = NULL;
    jint result = (*h2co3Launcher->android_jvm)->AttachCurrentThread(h2co3Launcher->android_jvm, &env, NULL);
    if (result != JNI_OK || env == NULL) {
        logError("Failed to attach thread to JavaVM.");
        return JNI_ERR;
    }
    jclass class_H2CO3LauncherLib = (*env)->FindClass(env, "org/koishi/launcher/h2co3/launcher/H2CO3LauncherLib");
    if (class_H2CO3LauncherLib == NULL) {
        logError("Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3LauncherLib.");
        return JNI_ERR;
    }
    h2co3Launcher->class_H2CO3LauncherLib = (jclass) (*env)->NewGlobalRef(env, class_H2CO3LauncherLib);
    jclass class_H2CO3LauncherActivity = (*env)->FindClass(env, "org/koishi/launcher/h2co3/launcher/H2CO3LauncherActivity");
    if (class_H2CO3LauncherActivity == NULL) {
        logError("Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3LauncherActivity.");
        return JNI_ERR;
    }
    h2co3Launcher->class_H2CO3LauncherActivity = (jclass) (*env)->NewGlobalRef(env, class_H2CO3LauncherActivity);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherActivity_nOnCreate(JNIEnv *env, jobject thiz) {
    if (h2co3Launcher == NULL) {
        return;
    }
    // Get the H2CO3LauncherActivity class
    jclass class_H2CO3LauncherActivity = (*env)->GetObjectClass(env, thiz);
    if (class_H2CO3LauncherActivity == NULL) {
        logError("Failed to get class: org/koishi/launcher/h2co3/launcher/H2CO3LauncherActivity.");
        return;
    }
    h2co3Launcher->class_H2CO3LauncherActivity = (*env)->NewGlobalRef(env, class_H2CO3LauncherActivity);

    // Get the setCursorMode function from the H2CO3LauncherActivity class
    h2co3Launcher->setCursorMode = (*env)->GetMethodID(env, h2co3Launcher->class_H2CO3LauncherActivity, "setCursorMode", "(I)V");
    if (h2co3Launcher->setCursorMode == NULL) {
        logError("Failed to get method: setCursorMode.");
        return;
    }
    h2co3Launcher->h2co3Activity = (*env)->NewGlobalRef(env, thiz);
}