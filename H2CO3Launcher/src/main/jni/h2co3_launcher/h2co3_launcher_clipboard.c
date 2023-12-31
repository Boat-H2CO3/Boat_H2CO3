#include "h2co3_launcher_internal.h"

void h2co3SetPrimaryClipString(const char *string) {
    PrepareH2CO3LauncherLibJNI();
    if (string == NULL) {
        H2CO3_INTERNAL_LOG("h2co3SetPrimaryClipString: string is NULL");
        return;
    }
    jstring jstr = (*env)->NewStringUTF(env, string);
    if (jstr == NULL) {
        H2CO3_INTERNAL_LOG("h2co3SetPrimaryClipString: Failed to create jstring");
        return;
    }
    CallH2CO3LauncherLibJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V", jstr);
}

const char *h2co3GetPrimaryClipString() {
    PrepareH2CO3LauncherLibJNI();
    if (h2co3Launcher == NULL || h2co3Launcher->clipboard_string == NULL) {
        H2CO3_INTERNAL_LOG("h2co3GetPrimaryClipString: clipboard_string is NULL");
        return NULL;
    }
    free(h2co3Launcher->clipboard_string);
    h2co3Launcher->clipboard_string = NULL;
    CallH2CO3LauncherLibJNIFunc(jstring clipstr =, Object, getPrimaryClipString, "()Ljava/lang/String;");
    if (clipstr == NULL) {
        H2CO3_INTERNAL_LOG("h2co3GetPrimaryClipString: Failed to get clipstr");
        return NULL;
    }
    const char *string = (*env)->GetStringUTFChars(env, clipstr, NULL);
    if (string == NULL) {
        H2CO3_INTERNAL_LOG("h2co3GetPrimaryClipString: Failed to get string");
        return NULL;
    }
    h2co3Launcher->clipboard_string = strdup(string);
    (*env)->ReleaseStringUTFChars(env, clipstr, string);
    return h2co3Launcher->clipboard_string;
}