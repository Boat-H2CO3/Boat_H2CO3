#include "h2co3launcher_internal.h"
#include <android/native_window_jni.h>
#include <jni.h>
#include <stdlib.h>

#define CLASS_H2CO3_LAUNCHER_LIB "org/koishi/launcher/h2co3/launcher/H2CO3LauncherLib"
#define CLASS_H2CO3_LAUNCHER_ACTIVITY "org/koishi/launcher/h2co3/launcher/H2CO3LauncherActivity"

struct H2CO3LauncherInternal *h2co3launcher = NULL;


void deleteGlobalRef(JNIEnv *env, jclass *globalRef);

ANativeWindow *h2co3launcherGetNativeWindow() {
    if (h2co3launcher == NULL) {
        return NULL;
    }
    return h2co3launcher->window;
}

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
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_h2co3launcherSetNativeWindow(JNIEnv *env,
                                                                                      jclass clazz,
                                                                                      jobject surface) {
    h2co3launcher->window = ANativeWindow_fromSurface(env, surface);
    H2CO3_INTERNAL_LOG("setFCLNativeWindow : %p, size : %dx%d", h2co3launcher->window, ANativeWindow_getWidth(h2co3launcher->window), ANativeWindow_getHeight(h2co3launcher->window));
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    h2co3launcher = (struct H2CO3LauncherInternal *) malloc(sizeof(struct H2CO3LauncherInternal));
    if (h2co3launcher == NULL) {
        return JNI_ERR;
    }
    memset(h2co3launcher, 0, sizeof(struct H2CO3LauncherInternal));
    h2co3launcher->android_jvm = vm;
    JNIEnv *env = NULL;
    jint result = (*h2co3launcher->android_jvm)->AttachCurrentThread(h2co3launcher->android_jvm, &env, NULL);
    if (result != JNI_OK || env == NULL) {
        H2CO3_INTERNAL_LOG("Failed to attach thread to JavaVM.");
        return JNI_ERR;
    }
    jclass class_H2CO3LauncherLib = (*env)->FindClass(env, CLASS_H2CO3_LAUNCHER_LIB);
    if (class_H2CO3LauncherLib == NULL) {
        H2CO3_INTERNAL_LOG("Failed to find class: %s.", CLASS_H2CO3_LAUNCHER_LIB);
        return JNI_ERR;
    }
    setGlobalRef(env, &h2co3launcher->class_H2CO3LauncherLib, class_H2CO3LauncherLib);
    jclass class_H2CO3LauncherActivity = (*env)->FindClass(env, CLASS_H2CO3_LAUNCHER_ACTIVITY);
    if (class_H2CO3LauncherActivity == NULL) {
        H2CO3_INTERNAL_LOG("Failed to find class: %s.", CLASS_H2CO3_LAUNCHER_ACTIVITY);
        return JNI_ERR;
    }
    setGlobalRef(env, &h2co3launcher->class_H2CO3LauncherActivity, class_H2CO3LauncherActivity);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherActivity_nOnCreate(JNIEnv *env, jobject thiz) {
    if (h2co3launcher == NULL) {
        return;
    }
    // Get the H2CO3LauncherActivity class
    jclass class_H2CO3LauncherActivity = (*env)->GetObjectClass(env, thiz);
    if (class_H2CO3LauncherActivity == NULL) {
        H2CO3_INTERNAL_LOG("Failed to get class: %s.", CLASS_H2CO3_LAUNCHER_ACTIVITY);
        return;
    }
    setGlobalRef(env, &h2co3launcher->class_H2CO3LauncherActivity, class_H2CO3LauncherActivity);

    // Get the setCursorMode function from the H2CO3LauncherActivity class
    h2co3launcher->setCursorMode = (*env)->GetMethodID(env, h2co3launcher->class_H2CO3LauncherActivity, "setCursorMode", "(I)V");
    if (h2co3launcher->setCursorMode == NULL) {
        H2CO3_INTERNAL_LOG("Failed to get method: setCursorMode.");
        return;
    }
    h2co3launcher->h2co3launcherActivity = (*env)->NewGlobalRef(env, thiz);
}
