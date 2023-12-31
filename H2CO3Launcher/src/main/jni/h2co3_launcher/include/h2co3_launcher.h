#ifndef h2co3_launcher_H
#define h2co3_launcher_H

#include <android/native_window.h>
#include "h2co3_launcher_event.h"

ANativeWindow *h2co3GetNativeWindow(void);

int h2co3WaitForEvent(int timeout);

int h2co3PollEvent(H2CO3LauncherEvent *event);

int h2co3GetEventFd(void);

void h2co3SetCursorMode(int mode);

void h2co3SetPrimaryClipString(const char *string);

const char *h2co3GetPrimaryClipString(void);

void h2co3SetInjectorMode(int mode);

int h2co3GetInjectorMode();

void h2co3SetHitResultType(int type);


#endif

