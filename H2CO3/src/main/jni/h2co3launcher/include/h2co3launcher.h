#ifndef h2co3launcher_H
#define h2co3launcher_H

#include <android/native_window.h>
#include "h2co3launcher_event.h"

ANativeWindow *h2co3launcherGetNativeWindow(void);

int h2co3launcherWaitForEvent(int timeout);

int h2co3launcherPollEvent(H2CO3LauncherEvent *event);

int h2co3launcherGetEventFd(void);

void h2co3launcherSetCursorMode(int mode);

void h2co3launcherSetPrimaryClipString(const char *string);

const char *h2co3launcherGetPrimaryClipString(void);

void h2co3launcherSetInjectorMode(int mode);

int h2co3launcherGetInjectorMode();

void h2co3launcherSetHitResultType(int type);


#endif

