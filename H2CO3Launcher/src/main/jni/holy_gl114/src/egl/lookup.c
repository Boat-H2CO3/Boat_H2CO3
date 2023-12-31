#include "../gl/attributes.h"
#include "../gl/init.h"
#include "../gl/logs.h"

#define STUB_FCT eglStub
#include "../gl/gl_lookup.h"

#include "egl.h"
#include "../glx/hardext.h"

extern void *gl4es_glXGetProcAddress(const char *name);

void glXStub(void *x, ...) {
    return;
}

void* gl4es_eglGetProcAddress(const char *name) {
    _EX(eglGetError);
    _EX(eglGetDisplay);
    _EX(eglInitialize);
    _EX(eglTerminate);
    _EX(eglQueryString);
    _EX(eglGetConfigs);
    _EX(eglChooseConfig);
    _EX(eglGetConfigAttrib);

    _EX(eglCreateWindowSurface);
    _EX(eglCreatePbufferSurface);
    _EX(eglCreatePixmapSurface);
    _EX(eglDestroySurface);
    _EX(eglQuerySurface);
    _EX(eglBindAPI);
    _EX(eglQueryAPI);

    _EX(eglWaitClient);
    _EX(eglReleaseThread);
    _EX(eglCreatePbufferFromClientBuffer);
    _EX(eglSurfaceAttrib);
    _EX(eglBindTexImage);
    _EX(eglReleaseTexImage);
    _EX(eglSwapInterval);
    _EX(eglCreateContext);
    _EX(eglDestroyContext);
    _EX(eglMakeCurrent);
    _EX(eglGetCurrentContext);
    _EX(eglGetCurrentSurface);
    _EX(eglGetCurrentDisplay);
    _EX(eglQueryContext);
    _EX(eglWaitGL);
    _EX(eglWaitNative);
    _EX(eglSwapBuffers);
    _EX(eglCopyBuffers);

    _EX(glXGetProcAddress);
    _ARB(glXGetProcAddress);
    _EX(eglGetProcAddress);

    return gl4es_GetProcAddress(name);
}

__eglMustCastToProperFunctionPointerType eglGetProcAddress(const char *name) AliasExport("gl4es_eglGetProcAddress");