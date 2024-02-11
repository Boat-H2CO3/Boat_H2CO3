#ifndef H2CO3LAUNCHER_BRIDGE_H
#define H2CO3LAUNCHER_BRIDGE_H

#include <android/native_window.h>
#include "h2co3launcher_event.h"

ANativeWindow *h2co3LauncherGetNativeWindow(void);

int h2co3LauncherWaitForEvent(int timeout);

int h2co3LauncherPollEvent(H2CO3LauncherEvent *event);

int h2co3LauncherGetEventFd(void);

void h2co3LauncherSetCursorMode(int mode);

void h2co3LauncherSetPrimaryClipString(const char *string);

const char *h2co3LauncherGetPrimaryClipString(void);

void h2co3LauncherSetInjectorMode(int mode);

int h2co3LauncherGetInjectorMode();

void h2co3LauncherSetHitResultType(int type);


#endif
