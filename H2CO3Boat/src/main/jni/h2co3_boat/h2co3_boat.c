#include "h2co3_boat_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>

BoatInternal mBoat;

ANativeWindow *h2co3GetNativeWindow() {
    return mBoat.window;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatActivity_setH2CO3NativeWindow(JNIEnv *env,
                                                                           jclass clazz,
                                                                           jobject surface) {
    mBoat.window = ANativeWindow_fromSurface(env, surface);
    Boat_INTERNAL_LOG("setBoatNativeWindow : %p", mBoat.window);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    memset(&mBoat, 0, sizeof(mBoat));
    mBoat.android_jvm = vm;
    JNIEnv *env = 0;
    jint result = (*mBoat.android_jvm)->AttachCurrentThread(mBoat.android_jvm, &env, 0);
    if (result != JNI_OK || env == 0) {
        Boat_INTERNAL_LOG("Failed to attach thread to JavaVM.");
        abort();
    }
    jclass class_H2CO3BoatLib = (*env)->FindClass(env,
                                                  "org/koishi/launcher/h2co3/boat/H2CO3BoatLib");
    if (class_H2CO3BoatLib == 0) {
        Boat_INTERNAL_LOG("Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3BoatLib.");
        abort();
    }
    mBoat.class_H2CO3BoatLib = (jclass) (*env)->NewGlobalRef(env, class_H2CO3BoatLib);
    jclass class_H2CO3BoatActivity = (*env)->FindClass(env,
                                                       "org/koishi/launcher/h2co3/boat/H2CO3BoatActivity");
    if (class_H2CO3BoatActivity == 0) {
        Boat_INTERNAL_LOG(
                "Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3BoatActivity.");
        abort();
    }
    mBoat.class_H2CO3BoatActivity = (jclass) (*env)->NewGlobalRef(env, class_H2CO3BoatActivity);
    return JNI_VERSION_1_2;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatActivity_nOnCreate(JNIEnv *env, jobject thiz) {

    // Get the H2CO3BoatActivity class
    jclass class_H2CO3BoatActivity = (*env)->GetObjectClass(env, thiz);
    mBoat.class_H2CO3BoatActivity = (*env)->NewGlobalRef(env, class_H2CO3BoatActivity);

    // Get the setCursorMode function from the H2CO3BoatActivity class
    mBoat.setCursorMode = (*env)->GetMethodID(env, mBoat.class_H2CO3BoatActivity, "setCursorMode",
                                              "(I)V");
    mBoat.setGrabCursorId = (*env)->GetMethodID(env,
                                                mBoat.class_H2CO3BoatActivity, "setGrabCursor",
                                                "(Z)V");
    if (mBoat.setGrabCursorId == NULL) {
        Boat_INTERNAL_LOG("Failed to find method: H2CO3BoatActivity::setGrabCursor");
        abort();
    }
    mBoat.h2co3Activity = (*env)->NewGlobalRef(env, thiz);
}

