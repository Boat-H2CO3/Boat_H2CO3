#include "h2co3_boat_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>

struct H2CO3BoatInternal *h2co3Boat = NULL;

ANativeWindow *h2co3GetNativeWindow() {
    if (h2co3Boat == NULL) {
        return NULL;
    }
    return h2co3Boat->window;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatLib_setH2CO3NativeWindow(JNIEnv *env,
                                                                      jclass clazz,
                                                                      jobject surface) {
    if (h2co3Boat == NULL) {
        return;
    }
    h2co3Boat->window = ANativeWindow_fromSurface(env, surface);
    H2CO3_BOAT_INTERNAL_LOG("setH2CO3BoatNativeWindow : %p", h2co3Boat->window);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    h2co3Boat = (struct H2CO3BoatInternal *) malloc(sizeof(struct H2CO3BoatInternal));
    if (h2co3Boat == NULL) {
        return JNI_ERR;
    }
    memset(h2co3Boat, 0, sizeof(struct H2CO3BoatInternal));
    h2co3Boat->android_jvm = vm;
    JNIEnv *env = NULL;
    jint result = (*h2co3Boat->android_jvm)->AttachCurrentThread(h2co3Boat->android_jvm, &env, NULL);
    if (result != JNI_OK || env == NULL) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to attach thread to JavaVM.");
        free(h2co3Boat);
        h2co3Boat = NULL;
        return JNI_ERR;
    }
    jclass class_H2CO3BoatLib = (*env)->FindClass(env,
                                                  "org/koishi/launcher/h2co3/boat/H2CO3BoatLib");
    if (class_H2CO3BoatLib == NULL) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3BoatLib.");
        free(h2co3Boat);
        h2co3Boat = NULL;
        return JNI_ERR;
    }
    h2co3Boat->class_H2CO3BoatLib = (jclass) (*env)->NewGlobalRef(env, class_H2CO3BoatLib);
    jclass class_H2CO3BoatActivity = (*env)->FindClass(env,
                                                       "org/koishi/launcher/h2co3/boat/H2CO3BoatActivity");
    if (class_H2CO3BoatActivity == NULL) {
        H2CO3_BOAT_INTERNAL_LOG(
                "Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3BoatActivity.");
        free(h2co3Boat);
        h2co3Boat = NULL;
        return JNI_ERR;
    }
    h2co3Boat->class_H2CO3BoatActivity = (jclass) (*env)->NewGlobalRef(env, class_H2CO3BoatActivity);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatActivity_nOnCreate(JNIEnv *env, jobject thiz) {
    if (h2co3Boat == NULL) {
        return;
    }
    // Get the H2CO3BoatActivity class
    jclass class_H2CO3BoatActivity = (*env)->GetObjectClass(env, thiz);
    if (class_H2CO3BoatActivity == NULL) {
        free(h2co3Boat);
        h2co3Boat = NULL;
        return;
    }
    h2co3Boat->class_H2CO3BoatActivity = (*env)->NewGlobalRef(env, class_H2CO3BoatActivity);

    // Get the setCursorMode function from the H2CO3BoatActivity class
    h2co3Boat->setCursorMode = (*env)->GetMethodID(env, h2co3Boat->class_H2CO3BoatActivity, "setCursorMode",
                                                   "(I)V");
    h2co3Boat->setGrabCursorId = (*env)->GetMethodID(env,
                                                     h2co3Boat->class_H2CO3BoatActivity, "setGrabCursor",
                                                     "(Z)V");
    if (h2co3Boat->setGrabCursorId == NULL) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to find method: H2CO3BoatActivity::setGrabCursor");
        free(h2co3Boat);
        h2co3Boat = NULL;
        return;
    }
    h2co3Boat->h2co3Activity = (*env)->NewGlobalRef(env, thiz);
}