#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <android/log.h>
#include <sys/mman.h>
#include <xhook.h>
#include <string.h>
#include "h2co3launcher_internal.h"

#define BUFFER_SIZE 1024
#define LOG_TAG "H2CO3Launcher"

static volatile jobject exitTrap_ctx;
static volatile jclass exitTrap_exitClass;
static volatile jmethodID exitTrap_staticMethod;
static JavaVM *exitTrap_jvm;

jstring CStr2Jstring(JNIEnv *env, const char *buffer);

void redirectStdio(JNIEnv *env, jclass clazz) {
    int boatPipe[2];

    if (pipe(boatPipe) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to create log pipe!");
        return;
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "succeed to create log pipe!");
    }

    if (dup2(boatPipe[1], STDOUT_FILENO) != STDOUT_FILENO &&
        dup2(boatPipe[1], STDERR_FILENO) != STDERR_FILENO) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to redirect stdio !");
        return;
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "succeed to redirect stdio !");
    }

    char buffer[BUFFER_SIZE];

    jclass loadme = (*env)->FindClass(env,
                                      "org/koishi/launcher/h2co3/launcher/H2CO3LauncherLoader");
    jmethodID loadme_static_method_receiveLog = (*env)->GetStaticMethodID(env, loadme, "receiveLog",
                                                                          "(Ljava/lang/String;)V");
    if (loadme_static_method_receiveLog == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to find receive method !");
        return;
    }

    while (1) {
        memset(buffer, '\0', sizeof(buffer));
        ssize_t _s = read(boatPipe[0], buffer, sizeof(buffer) - 1);
        if (_s < 0) {
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to read log !");
            close(boatPipe[0]);
            close(boatPipe[1]);
            return;
        } else {
            buffer[_s] = '\0';
        }
        if (buffer[0] == '\0') {
            continue;
        } else {
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "%s", buffer);
            (*env)->CallStaticVoidMethod(env, clazz, loadme_static_method_receiveLog,
                                         CStr2Jstring(env, buffer));
        }
    }
}

jstring CStr2Jstring(JNIEnv *env, const char *buffer) {
    jsize len = strlen(buffer);
    jbyteArray bytes = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *) buffer);
    jstring encoding = (*env)->NewStringUTF(env, "UTF-8");
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([BLjava/lang/String;)V");
    return (jstring) (*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}

void custom_exit(int code) {
    JNIEnv *env;
    (*exitTrap_jvm)->AttachCurrentThread(exitTrap_jvm, &env, NULL);
    (*env)->CallStaticVoidMethod(env, exitTrap_exitClass, exitTrap_staticMethod, exitTrap_ctx,
                                 code);
    (*env)->DeleteGlobalRef(env, exitTrap_ctx);
    (*env)->DeleteGlobalRef(env, exitTrap_exitClass);
    (*exitTrap_jvm)->DetachCurrentThread(exitTrap_jvm);
    exit(code);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_redirectStdio(JNIEnv *env, jclass clazz) {
    redirectStdio(env, clazz);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_saveLogToPath(JNIEnv *env, jclass clazz, jstring path) {
    const char *file = (*env)->GetStringUTFChars(env, path, NULL);

    int fd = open(file, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    if (fd != -1) {
        dup2(fd, 1);
        dup2(fd, 2);
    }

    (*env)->ReleaseStringUTFChars(env, path, file);

    // 调用 receiveLog 方法，并将日志字符串作为参数传递
    jclass loadme = (*env)->FindClass(env, "org/koishi/launcher/h2co3/launcher/H2CO3LauncherLoader");
    jmethodID loadme_static_method_receiveLog = (*env)->GetStaticMethodID(env, loadme, "receiveLog", "(Ljava/lang/String;)V");
    if (loadme_static_method_receiveLog == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to find receiveLog method !");
        return;
    }

    char buffer[BUFFER_SIZE];
    while (1) {
        ssize_t _s = read(fd, buffer, sizeof(buffer) - 1);
        if (_s < 0) {
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "failed to read log !");
            close(fd);
            return;
        } else {
            buffer[_s] = '\0';
        }
        if (buffer[0] == '\0') {
            continue;
        } else {
            __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "%s", buffer);
            jstring logString = CStr2Jstring(env, buffer);
            (*env)->CallStaticVoidMethod(env, loadme, loadme_static_method_receiveLog, logString);
            (*env)->DeleteLocalRef(env, logString);
        }
    }
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_chdir(JNIEnv *env, jclass clazz,
                                                                  jstring path) {
    const char *dir = (*env)->GetStringUTFChars(env, path, NULL);

    int ret = chdir(dir);

    (*env)->ReleaseStringUTFChars(env, path, dir);
    return ret;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_setenv(JNIEnv *env, jclass clazz,
                                                                   jstring str1, jstring str2) {
    const char *name = (*env)->GetStringUTFChars(env, str1, NULL);
    const char *value = (*env)->GetStringUTFChars(env, str2, NULL);

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, str1, name);
    (*env)->ReleaseStringUTFChars(env, str2, value);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_dlopen(JNIEnv *env, jclass clazz,jstring str1) {
    dlerror();

    int ret = 0;
    char const* lib_name = (*env)->GetStringUTFChars(env, str1, 0);

    void* handle;
    dlerror();
    handle = dlopen(lib_name, RTLD_GLOBAL);
    __android_log_print(ANDROID_LOG_ERROR, "H2CO3DLOpen", "loading %s (error = %s)", lib_name, dlerror());

    if (handle == NULL) {
        ret = -1;
    }

    (*env)->ReleaseStringUTFChars(env, str1, lib_name);
    return ret;

}

void stub() {

}

#define PAGE_START(x) ((void*)((unsigned long)(x) & ~((unsigned long)getpagesize() - 1)))

void (*old_exit)(int code);

JNIEXPORT jint JNICALL Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_setupExitTrap(JNIEnv *env, jobject jobject1, jobject bridge) {
    exitTrap_ctx = (*env)->NewGlobalRef(env, bridge);
    (*env)->GetJavaVM(env, &exitTrap_jvm);
    exitTrap_exitClass = (*env)->NewGlobalRef(env,(*env)->FindClass(env, "org/koishi/launcher/h2co3/launcher/H2CO3LauncherActivity"));
    exitTrap_staticMethod = (*env)->GetStaticMethodID(env, exitTrap_exitClass, "onExit","(Landroid/content/Context;I)V");
    (*env)->DeleteGlobalRef(env, exitTrap_exitClass);
    // Enable xhook debug mode here
    // xhook_enable_debug(1);
    xhook_register(".*\\.so$", "exit", custom_exit, (void **) &old_exit);
    return xhook_refresh(1);
}

JNIEXPORT int JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_dlexec(JNIEnv *env, jclass clazz,
                                                                   jobjectArray argsArray) {
    dlerror();

    int argc = (*env)->GetArrayLength(env, argsArray);
    char* argv[argc];
    for (int i = 0; i < argc; i++) {
        jstring str = (jstring)(*env)->GetObjectArrayElement(env, argsArray, i);
        const char* utfChars = (*env)->GetStringUTFChars(env, str, NULL);
        argv[i] = strdup(utfChars);
        (*env)->ReleaseStringUTFChars(env, str, utfChars);
    }
    char** envp = environ;

    jstring str0 = (jstring)(*env)->GetObjectArrayElement(env, argsArray, 0);
    const char* lib_name = (*env)->GetStringUTFChars(env, str0, NULL);

    void* handle = dlopen(lib_name, RTLD_GLOBAL);
    __android_log_print(ANDROID_LOG_ERROR, "H2CO3DLExec", "loading %s (error = %s)", lib_name, dlerror());
    if (handle == NULL) {
        for (int i = 0; i < argc; i++) {
            free(argv[i]);
        }
        (*env)->ReleaseStringUTFChars(env, str0, lib_name);
        return -1;
    }

    int (*main_func)(int, char**, char**) = (int (*)(int, char**, char**))dlsym(handle, "main");
    __android_log_print(ANDROID_LOG_ERROR, "H2CO3DLExec", "getting main() in %s (error = %s)", lib_name, dlerror());
    if (main_func == NULL) {
        dlclose(handle);
        for (int i = 0; i < argc; i++) {
            free(argv[i]);
        }
        (*env)->ReleaseStringUTFChars(env, str0, lib_name);
        return -2;
    }

    int ret = main_func(argc, argv, envp);

    dlclose(handle);
    for (int i = 0; i < argc; i++) {
        free(argv[i]);
    }
    (*env)->ReleaseStringUTFChars(env, str0, lib_name);
    return ret;
}

unsigned gen_ldr_pc(unsigned rt, signed long off) {
    unsigned result = 0x0;

    result |= 0x58; // 01 011 0 00;
    result <<= 19;

    signed long imm = off / 4;
    imm &= 0x7ffff;
    result |= imm;
    result <<= 5;

    rt &= 0x1f;
    result |= rt;

    return result;
}

unsigned gen_ret(unsigned lr) {
    unsigned result = 0x0;

    result |= 0x3597c0;  // 1101011 0 0 10 11111 0000 0 0
    result <<= 5;
    lr &= 0x1f;
    result |= lr;
    result <<= 5;
    result |= 0x0;
    return result;
}

unsigned gen_mov_reg(unsigned tr, unsigned sr) {
    unsigned result = 0x0;

    result |= 0x550;  // 1 01 01010 00 0
    result <<= 5;

    sr &= 0x1f;
    result |= sr;
    result <<= 11;

    result |= 0x1f;  // 000000 11111
    result <<= 5;

    tr &= 0x1f;
    result |= tr;

    return result;
}

unsigned gen_mov_imm(unsigned tr, signed short imm) {
    unsigned result = 0x0;

    result |= 0x694;  // 1 10 100101 00
    result <<= 16;

    result |= imm;
    result <<= 5;

    tr &= 0x1f;
    result |= tr;

    return result;
}

void patchLinker(JNIEnv *env, jclass clazz) {
    void *libdl_handle = dlopen("libdl.so", RTLD_GLOBAL);
    if (libdl_handle == NULL) {
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Failed to open libdl.so");
        return;
    }

    unsigned *dlopen_addr = (unsigned *) dlsym(libdl_handle, "dlopen");
    unsigned *dlsym_addr = (unsigned *) dlsym(libdl_handle, "dlsym");
    unsigned *dlvsym_addr = (unsigned *) dlsym(libdl_handle, "dlvsym");
    unsigned *buffer = (unsigned *) dlsym(libdl_handle, "android_get_LD_LIBRARY_PATH");
    if (dlopen_addr == NULL || dlsym_addr == NULL || dlvsym_addr == NULL || buffer == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to find symbols");
        dlclose(libdl_handle);
        return;
    }

    mprotect(PAGE_START(buffer), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC);
    mprotect(PAGE_START(dlopen_addr), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC);
    mprotect(PAGE_START(dlsym_addr), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC);
    mprotect(PAGE_START(dlvsym_addr), getpagesize(), PROT_READ | PROT_WRITE | PROT_EXEC);

    unsigned ins_ret = gen_ret(30);
    unsigned ins_mov_x0_0 = gen_mov_imm(0, 0);

    buffer[0] = ins_mov_x0_0;
    buffer[1] = ins_ret;

    void **tmp_addr = (void **) (buffer + 2);
    *tmp_addr = stub;

    unsigned ins_mov_x2_x30 = gen_mov_reg(2, 30);
    unsigned ins_mov_x3_x30 = gen_mov_reg(3, 30);

    int dlopen_hooked = 0;
    int dlsym_hooked = 0;
    int dlvsym_hooked = 0;
    for (int i = 0; dlopen_addr[i] != ins_ret; i++) {
        if (dlopen_addr[i] == ins_mov_x2_x30) {
            dlopen_addr[i] = gen_ldr_pc(2,
                                        (unsigned long) tmp_addr - (unsigned long) &dlopen_addr[i]);
            dlopen_hooked = 1;
            break;
        }
    }
    for (int i = 0; dlsym_addr[i] != ins_ret; i++) {
        if (dlsym_addr[i] == ins_mov_x2_x30) {
            dlsym_addr[i] = gen_ldr_pc(2,
                                       (unsigned long) tmp_addr - (unsigned long) &dlsym_addr[i]);
            dlsym_hooked = 1;
            break;
        }
    }
    for (int i = 0; dlvsym_addr[i] != ins_ret; i++) {
        if (dlvsym_addr[i] == ins_mov_x3_x30) {
            dlvsym_addr[i] = gen_ldr_pc(3,
                                        (unsigned long) tmp_addr - (unsigned long) &dlvsym_addr[i]);
            dlvsym_hooked = 1;
            break;
        }
    }

    if (dlopen_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "dlopen() not patched");
    }
    if (dlsym_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "dlsym() not patched");
    }
    if (dlvsym_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "dlvsym() not patched");
    }

    dlclose(libdl_handle);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLoader_patchLinker(JNIEnv *env, jclass clazz) {
    patchLinker(env, clazz);
}