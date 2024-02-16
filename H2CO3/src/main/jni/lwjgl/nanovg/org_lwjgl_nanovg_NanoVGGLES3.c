/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
#include "lwjgl/common_tools.h"
#include "lwjgl/lwjgl_malloc.h"
#define NVG_MALLOC(sz)     org_lwjgl_malloc(sz)
#define NVG_REALLOC(p, sz)  org_lwjgl_realloc(p,sz)
#define NVG_FREE(p)        org_lwjgl_free(p)

int DISABLE_WARNINGS();

#define NANOVG_GLES3_IMPLEMENTATION

#include "nanovg.h"
#include "nanovg_gl.h"
#include "nanovg_gl_utils.h"

int ENABLE_WARNINGS();

EXTERN_C_ENTER

JNIEXPORT jint JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvglCreateImageFromHandle(JNIEnv *__env, jclass clazz,
                                                             jlong ctxAddress, jint textureId,
                                                             jint w, jint h, jint flags) {
    NVGcontext *ctx = (NVGcontext *) (uintptr_t) ctxAddress;
    UNUSED_PARAMS(__env, clazz)
    return (jint) nvglCreateImageFromHandleGLES3(ctx, (GLuint) textureId, w, h, flags);
}

JNIEXPORT jint JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvglImageHandle(JNIEnv *__env, jclass clazz, jlong ctxAddress,
                                                   jint image) {
    NVGcontext *ctx = (NVGcontext *) (uintptr_t) ctxAddress;
    UNUSED_PARAMS(__env, clazz)
    return (jint) nvglImageHandleGLES3(ctx, image);
}

JNIEXPORT jlong JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvgCreate(JNIEnv *__env, jclass clazz, jint flags) {
    UNUSED_PARAM(clazz)
    return (jlong) (uintptr_t) nvgCreateGLES3(__env, flags);
}

JNIEXPORT void JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvgDelete(JNIEnv *__env, jclass clazz, jlong ctxAddress) {
    NVGcontext *ctx = (NVGcontext *) (uintptr_t) ctxAddress;
    UNUSED_PARAMS(__env, clazz)
    nvgDeleteGLES3(ctx);
}

JNIEXPORT jlong JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvgluCreateFramebuffer(JNIEnv *__env, jclass clazz,
                                                          jlong ctxAddress, jint w, jint h,
                                                          jint imageFlags) {
    NVGcontext *ctx = (NVGcontext *) (uintptr_t) ctxAddress;
    UNUSED_PARAMS(__env, clazz)
    return (jlong) (uintptr_t) nvgluCreateFramebufferGLES3(ctx, w, h, imageFlags);
}

JNIEXPORT void JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvgluBindFramebuffer(JNIEnv *__env, jclass clazz,
                                                        jlong ctxAddress, jlong fbAddress) {
    NVGcontext *ctx = (NVGcontext *) (uintptr_t) ctxAddress;
    NVGLUframebuffer *fb = (NVGLUframebuffer *) (uintptr_t) fbAddress;
    UNUSED_PARAMS(__env, clazz)
    nvgluBindFramebufferGLES3(ctx, fb);
}

JNIEXPORT void JNICALL
Java_org_lwjgl_nanovg_NanoVGGLES3_nnvgluDeleteFramebuffer(JNIEnv *__env, jclass clazz,
                                                          jlong ctxAddress, jlong fbAddress) {
    NVGcontext *ctx = (NVGcontext *) (uintptr_t) ctxAddress;
    NVGLUframebuffer *fb = (NVGLUframebuffer *) (uintptr_t) fbAddress;
    UNUSED_PARAMS(__env, clazz)
    nvgluDeleteFramebufferGLES3(ctx, fb);
}

EXTERN_C_EXIT
