#include "h2co3Launcher_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>
#include <android/log.h>
#include <assert.h>

struct H2CO3LauncherInternal *h2co3Launcher;

void initialize_h2co3Launcher() {
    h2co3Launcher = malloc(sizeof(struct H2CO3LauncherInternal));
    if (!h2co3Launcher) {
        __android_log_print(ANDROID_LOG_ERROR, "Environ",
                            "Failed to allocate memory for h2co3Launcher.");
        abort();
    }
    memset(h2co3Launcher, 0, sizeof(struct H2CO3LauncherInternal));
}

void env_init() {
    char *strptr_env = getenv("H2CO3LAUNCHER_ENVIRON");
    if (strptr_env == NULL) {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "No environ found, creating...");
        initialize_h2co3Launcher();
        if (asprintf(&strptr_env, "%p", h2co3Launcher) == -1) {
            free(h2co3Launcher);
            __android_log_print(ANDROID_LOG_ERROR, "Environ", "Failed to create environ string.");
            abort();
        }
        setenv("H2CO3LAUNCHER_ENVIRON", strptr_env, 1);
        free(strptr_env);
    } else {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "Found existing environ: %s", strptr_env);
        h2co3Launcher = (void *) strtoul(strptr_env, NULL, 0x10);
    }
    __android_log_print(ANDROID_LOG_INFO, "Environ", "%p", h2co3Launcher);
}

ANativeWindow *h2co3LauncherGetNativeWindow() {
    return h2co3Launcher->window;
}

void h2co3LauncherSetPrimaryClipString(const char *string) {
    PrepareH2CO3LauncherBridgeJNI();
    CallH2CO3LauncherBridgeJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V",
                                     (*env)->NewStringUTF(env, string));
}

const char *h2co3LauncherGetPrimaryClipString() {
    PrepareH2CO3LauncherBridgeJNI();
    if (h2co3Launcher->clipboard_string != NULL) {
        free(h2co3Launcher->clipboard_string);
        h2co3Launcher->clipboard_string = NULL;
    }
    CallH2CO3LauncherBridgeJNIFunc(jstring clipstr =, Object, getPrimaryClipString,
                                   "()Ljava/lang/String;");
    const char *string = NULL;
    if (clipstr != NULL) {
        string = (*env)->GetStringUTFChars(env, clipstr, NULL);
        if (string != NULL) {
            h2co3Launcher->clipboard_string = strdup(string);
        }
    }
    return h2co3Launcher->clipboard_string;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_h2co3LauncherSetNativeWindow(
        JNIEnv *env, jclass clazz, jobject surface) {
    h2co3Launcher->window = ANativeWindow_fromSurface(env, surface);
    H2CO3_INTERNAL_LOG("setH2CO3LauncherNativeWindow : %p, size : %dx%d", h2co3Launcher->window,
                       ANativeWindow_getWidth(h2co3Launcher->window),
                       ANativeWindow_getHeight(h2co3Launcher->window));
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_setH2CO3LauncherBridge(
        JNIEnv *env,
        jobject thiz,
        jobject h2co3Launcher_bridge) {
    if (h2co3Launcher->object_H2CO3LauncherBridge) {
        (*env)->DeleteGlobalRef(env, h2co3Launcher->object_H2CO3LauncherBridge);
    }
    h2co3Launcher->object_H2CO3LauncherBridge = (jclass) (*env)->NewGlobalRef(env,
                                                                              h2co3Launcher_bridge);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    initialize_h2co3Launcher();
    h2co3Launcher->android_jvm = vm;
    JNIEnv *env = NULL;
    jint result = (*vm)->AttachCurrentThread(vm, &env, NULL);
    if (result != JNI_OK || env == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "JNI", "Failed to attach thread to JavaVM.");
        abort();
    }
    jclass class_H2CO3LauncherBridge = (*env)->FindClass(env,
                                                         "org/koishi/launcher/h2co3/launcher/utils/H2CO3LauncherBridge");
    if (class_H2CO3LauncherBridge == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "JNI", "Failed to find class H2CO3LauncherBridge.");
        abort();
    }
    h2co3Launcher->class_H2CO3LauncherBridge = (jclass) (*env)->NewGlobalRef(env,
                                                                             class_H2CO3LauncherBridge);
    return JNI_VERSION_1_2;
}
