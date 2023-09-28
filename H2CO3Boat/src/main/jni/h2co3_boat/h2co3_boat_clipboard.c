#include "h2co3_boat_internal.h"

void h2co3SetPrimaryClipString(const char *string) {
    PrepareH2CO3BoatLibJNI();
    CallH2CO3BoatLibJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V",
                              (*env)->NewStringUTF(env, string));
}

const char *h2co3GetPrimaryClipString() {
    PrepareH2CO3BoatLibJNI();
    if (mBoat.clipboard_string != NULL) {
        free(mBoat.clipboard_string);
        mBoat.clipboard_string = NULL;
    }
    CallH2CO3BoatLibJNIFunc(jstring clipstr =, Object, getPrimaryClipString,
                            "()Ljava/lang/String;");
    const char *string = NULL;
    if (clipstr != NULL) {
        string = (*env)->GetStringUTFChars(env, clipstr, NULL);
        if (string != NULL) {
            mBoat.clipboard_string = strdup(string);
        }
    }
    return mBoat.clipboard_string;
}
