#include "h2co3_boat_internal.h"

void h2co3SetPrimaryClipString(const char *string) {
    PrepareH2CO3BoatLibJNI();
    if (string == NULL) {
        // 处理空指针异常
        return;
    }
    jstring jstr = (*env)->NewStringUTF(env, string);
    if (jstr == NULL) {
        // 处理JNI函数调用失败
        return;
    }
    CallH2CO3BoatLibJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V", jstr);
}

const char *h2co3GetPrimaryClipString() {
    PrepareH2CO3BoatLibJNI();
    if (h2co3Boat == NULL || h2co3Boat->clipboard_string == NULL) {
        // 处理空指针异常
        return NULL;
    }
    free(h2co3Boat->clipboard_string);
    h2co3Boat->clipboard_string = NULL;
    CallH2CO3BoatLibJNIFunc(jstring clipstr =, Object, getPrimaryClipString, "()Ljava/lang/String;");
    if (clipstr == NULL) {
        // 处理JNI函数调用失败
        return NULL;
    }
    const char *string = (*env)->GetStringUTFChars(env, clipstr, NULL);
    if (string == NULL) {
        // 处理JNI函数调用失败
        return NULL;
    }
    h2co3Boat->clipboard_string = strdup(string);
    (*env)->ReleaseStringUTFChars(env, clipstr, string);
    return h2co3Boat->clipboard_string;
}