/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
#include "lwjgl/common_tools.h"
#include "opengl.h"

typedef void (APIENTRY *glImportMemoryWin32HandleEXTPROC) (jint, jlong, jint, uintptr_t);
typedef void (APIENTRY *glImportMemoryWin32NameEXTPROC) (jint, jlong, jint, uintptr_t);

EXTERN_C_ENTER

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_EXTMemoryObjectWin32_nglImportMemoryWin32HandleEXT(JNIEnv *__env, jclass clazz, jint memory, jlong size, jint handleType, jlong handleAddress) {
    glImportMemoryWin32HandleEXTPROC glImportMemoryWin32HandleEXT = (glImportMemoryWin32HandleEXTPROC)tlsGetFunction(1812);
    uintptr_t handle = (uintptr_t)handleAddress;
    UNUSED_PARAM(clazz)
    glImportMemoryWin32HandleEXT(memory, size, handleType, handle);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_EXTMemoryObjectWin32_nglImportMemoryWin32NameEXT(JNIEnv *__env, jclass clazz, jint memory, jlong size, jint handleType, jlong nameAddress) {
    glImportMemoryWin32NameEXTPROC glImportMemoryWin32NameEXT = (glImportMemoryWin32NameEXTPROC)tlsGetFunction(1813);
    uintptr_t name = (uintptr_t)nameAddress;
    UNUSED_PARAM(clazz)
    glImportMemoryWin32NameEXT(memory, size, handleType, name);
}

EXTERN_C_EXIT
