/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
#include "lwjgl/common_tools.h"
#include "opengl.h"

typedef void (APIENTRY *glBlendParameteriNVPROC) (jint, jint);
typedef void (APIENTRY *glBlendBarrierNVPROC) (void);

EXTERN_C_ENTER

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVBlendEquationAdvanced_glBlendParameteriNV(JNIEnv *__env, jclass clazz, jint pname, jint value) {
    glBlendParameteriNVPROC glBlendParameteriNV = (glBlendParameteriNVPROC)tlsGetFunction(1925);
    UNUSED_PARAM(clazz)
    glBlendParameteriNV(pname, value);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_NVBlendEquationAdvanced_glBlendBarrierNV(JNIEnv *__env, jclass clazz) {
    glBlendBarrierNVPROC glBlendBarrierNV = (glBlendBarrierNVPROC)tlsGetFunction(1926);
    UNUSED_PARAM(clazz)
    glBlendBarrierNV();
}

EXTERN_C_EXIT
