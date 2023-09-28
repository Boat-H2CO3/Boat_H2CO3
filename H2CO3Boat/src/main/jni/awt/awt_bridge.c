#include <jni.h>
#include <assert.h>
#include <string.h>
#include <stdio.h>
#include "h2co3_boat_internal.h"

static JavaVM *dalvikJavaVMPtr;

jclass class_H2CO3BoatLib;
jmethodID method_OpenLink;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    if (dalvikJavaVMPtr == NULL) {
        dalvikJavaVMPtr = vm;
        JNIEnv *env = NULL;
        (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4);
        class_H2CO3BoatLib = mBoat.class_H2CO3BoatLib;
        method_OpenLink = (*env)->GetStaticMethodID(env, class_H2CO3BoatLib, "openLink",
                                                    "(Ljava/lang/String;)V");
    }
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL
Java_net_java_openjdk_cacio_ctc_CTCDesktopPeer_openFile(JNIEnv *env, jclass clazz,
                                                        jstring filePath) {
    JNIEnv *dalvikEnv;
    char detachable = 0;
    if ((*dalvikJavaVMPtr)->GetEnv(dalvikJavaVMPtr, (void **) &dalvikEnv, JNI_VERSION_1_6) ==
        JNI_EDETACHED) {
        (*dalvikJavaVMPtr)->AttachCurrentThread(dalvikJavaVMPtr, &dalvikEnv, NULL);
        detachable = 1;
    }
    const char *stringChars = (*env)->GetStringUTFChars(env, filePath, NULL);
    (*dalvikEnv)->CallStaticVoidMethod(dalvikEnv, class_H2CO3BoatLib, method_OpenLink,
                                       (*dalvikEnv)->NewStringUTF(dalvikEnv, stringChars));
    (*env)->ReleaseStringUTFChars(env, filePath, stringChars);
    if (detachable) (*dalvikJavaVMPtr)->DetachCurrentThread(dalvikJavaVMPtr);
}

JNIEXPORT void JNICALL
Java_net_java_openjdk_cacio_ctc_CTCDesktopPeer_openUri(JNIEnv *env, jclass clazz, jstring uri) {
    JNIEnv *dalvikEnv;
    char detachable = 0;
    if ((*dalvikJavaVMPtr)->GetEnv(dalvikJavaVMPtr, (void **) &dalvikEnv, JNI_VERSION_1_6) ==
        JNI_EDETACHED) {
        (*dalvikJavaVMPtr)->AttachCurrentThread(dalvikJavaVMPtr, &dalvikEnv, NULL);
        detachable = 1;
    }
    const char *stringChars = (*env)->GetStringUTFChars(env, uri, NULL);
    (*dalvikEnv)->CallStaticVoidMethod(dalvikEnv, class_H2CO3BoatLib, method_OpenLink,
                                       (*dalvikEnv)->NewStringUTF(dalvikEnv, stringChars));
    (*env)->ReleaseStringUTFChars(env, uri, stringChars);
    if (detachable) (*dalvikJavaVMPtr)->DetachCurrentThread(dalvikJavaVMPtr);
}

JNIEXPORT void JNICALL
Java_sun_awt_peer_cacio_BoatClipboard_clipboardCopy(JNIEnv *env, jclass clazz, jstring str) {
    const char *stringChars = (*env)->GetStringUTFChars(env, str, NULL);
    h2co3SetPrimaryClipString(stringChars);
    (*env)->ReleaseStringUTFChars(env, str, stringChars);
}
