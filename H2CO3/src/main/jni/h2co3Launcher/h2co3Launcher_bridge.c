#include "h2co3Launcher_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>

struct H2CO3LauncherInternal *h2co3Launcher = NULL;

__attribute__((constructor))
void env_init() {
    h2co3Launcher = calloc(1, sizeof(struct H2CO3LauncherInternal));
    if (!h2co3Launcher) {
        __android_log_print(ANDROID_LOG_ERROR, "Environ",
                            "Failed to allocate memory for h2co3Launcher.");
        abort();
    }
    __android_log_print(ANDROID_LOG_INFO, "Environ", "h2co3Launcher initialized: %p",
                        h2co3Launcher);
}

ANativeWindow *h2co3LauncherGetNativeWindow() {
    if (h2co3Launcher) {
        return h2co3Launcher->window;
    }
    return NULL;
}

void h2co3LauncherSetPrimaryClipString(const char *string) {
    if (!h2co3Launcher) {
        __android_log_print(ANDROID_LOG_ERROR, "h2co3Launcher",
                            "h2co3Launcher is not initialized.");
        return;
    }
    PrepareH2CO3LauncherBridgeJNI();
    jstring jstr = (*env)->NewStringUTF(env, string);
    CallH2CO3LauncherBridgeJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V", jstr);
    (*env)->DeleteLocalRef(env, jstr);
}

const char *h2co3LauncherGetPrimaryClipString() {
    if (!h2co3Launcher) {
        __android_log_print(ANDROID_LOG_ERROR, "h2co3Launcher",
                            "h2co3Launcher is not initialized.");
        return NULL;
    }
    PrepareH2CO3LauncherBridgeJNI();
    if (h2co3Launcher->clipboard_string) {
        free(h2co3Launcher->clipboard_string);
        h2co3Launcher->clipboard_string = NULL;
    }
    CallH2CO3LauncherBridgeJNIFunc(jstring clipstr =, Object, getPrimaryClipString,
                                   "()Ljava/lang/String;");
    const char *string = NULL;
    if (clipstr) {
        string = (*env)->GetStringUTFChars(env, clipstr, NULL);
        if (string) {
            h2co3Launcher->clipboard_string = strdup(string);
            (*env)->ReleaseStringUTFChars(env, clipstr, string);
        }
        (*env)->DeleteLocalRef(env, clipstr);
    }
    return h2co3Launcher->clipboard_string;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_game_H2CO3LauncherBridge_h2co3LauncherSetNativeWindow(
        JNIEnv *env, jclass clazz, jobject surface) {
    if (!surface || !h2co3Launcher) {
        __android_log_print(ANDROID_LOG_ERROR, "h2co3Launcher",
                            "Surface or h2co3Launcher is NULL.");
        return;
    }
    h2co3Launcher->window = ANativeWindow_fromSurface(env, surface);
    H2CO3_INTERNAL_LOG("setH2CO3LauncherNativeWindow : %p, size : %dx%d", h2co3Launcher->window,
                       ANativeWindow_getWidth(h2co3Launcher->window),
                       ANativeWindow_getHeight(h2co3Launcher->window));
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_game_H2CO3LauncherBridge_setH2CO3LauncherBridge(JNIEnv *env,
                                                                                    jobject thiz,
                                                                                    jobject h2co3Launcher_bridge) {
    if (!thiz) {
        __android_log_print(ANDROID_LOG_ERROR, "h2co3Launcher", "thiz is NULL.");
        return;
    }
    h2co3Launcher->object_H2CO3LauncherBridge = (*env)->NewGlobalRef(env, thiz);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    env_init();

    if (!h2co3Launcher->android_jvm) {
        h2co3Launcher->android_jvm = vm;
        JNIEnv *env = NULL;
        jint result = (*vm)->AttachCurrentThread(vm, &env, NULL);
        if (result != JNI_OK || !env) {
            H2CO3_INTERNAL_LOG("Failed to attach thread to JavaVM.");
            abort();
        }
        jclass class_H2CO3LauncherBridge = (*env)->FindClass(env,
                                                             "org/koishi/launcher/h2co3/core/game/H2CO3LauncherBridge");
        if (!class_H2CO3LauncherBridge) {
            H2CO3_INTERNAL_LOG(
                    "Failed to find class: org/koishi/launcher/h2co3/core/game/H2CO3LauncherBridge.");
            abort();
        }
        h2co3Launcher->class_H2CO3LauncherBridge = (*env)->NewGlobalRef(env, class_H2CO3LauncherBridge);
    }
    return JNI_VERSION_1_2;
}