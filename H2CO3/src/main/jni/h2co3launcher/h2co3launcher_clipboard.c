#include "h2co3launcher_internal.h"

void h2co3launcherSetPrimaryClipString(const char *string) {
    if (string == NULL) {
        H2CO3_INTERNAL_LOG("h2co3launcherSetPrimaryClipString: string is NULL");
        return;
    }
    PrepareH2CO3LauncherLibJNI();
    jstring jstr = (*env)->NewStringUTF(env, string);
    if (jstr == NULL) {
        H2CO3_INTERNAL_LOG("h2co3launcherSetPrimaryClipString: Failed to create jstring");
        return;
    }
    CallH2CO3LauncherLibJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V", jstr);
}

const char *h2co3launcherGetPrimaryClipString() {
    if (h2co3launcher == NULL || h2co3launcher->clipboard_string == NULL) {
        H2CO3_INTERNAL_LOG("h2co3launcherGetPrimaryClipString: clipboard_string is NULL");
        return NULL;
    }
    PrepareH2CO3LauncherLibJNI();
    free(h2co3launcher->clipboard_string);
    h2co3launcher->clipboard_string = NULL;
    CallH2CO3LauncherLibJNIFunc(jstring clipstr =, Object, getPrimaryClipString,
                                "()Ljava/lang/String;");
    if (clipstr == NULL) {
        H2CO3_INTERNAL_LOG("h2co3launcherGetPrimaryClipString: Failed to get clipstr");
        return NULL;
    }
    const char *string = (*env)->GetStringUTFChars(env, clipstr, NULL);
    if (string == NULL) {
        H2CO3_INTERNAL_LOG("h2co3launcherGetPrimaryClipString: Failed to get string");
        return NULL;
    }
    h2co3launcher->clipboard_string = strdup(string);
    (*env)->ReleaseStringUTFChars(env, clipstr, string);
    return h2co3launcher->clipboard_string;
}