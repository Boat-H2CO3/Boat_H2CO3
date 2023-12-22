
#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <android/log.h>
#include <sys/mman.h>
#include <xhook.h>
#include <string.h>

#define LOG_TAG "H2CO3"

static volatile jobject exitTrap_ctx;
static volatile jclass exitTrap_exitClass;
static volatile jmethodID exitTrap_staticMethod;
static JavaVM *exitTrap_jvm;

jstring CStr2Jstring(JNIEnv *env, const char *buffer);

jstring CStr2Jstring(JNIEnv *env, const char *buffer) {
    jsize len = strlen(buffer);
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    jstring encoding = (*env)->NewStringUTF(env, "UTF-8");
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>", "([BLjava/lang/String;)V");
    jbyteArray bytes = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, bytes, 0, len, (jbyte *) buffer);
    return (jstring) (*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}

void (*old_exit)(int code);

void custom_exit(int code) {
    JNIEnv *env;
    (*exitTrap_jvm)->AttachCurrentThread(exitTrap_jvm, &env, NULL);
    (*env)->CallStaticVoidMethod(env, exitTrap_exitClass, exitTrap_staticMethod, exitTrap_ctx,
                                 code);
    (*env)->DeleteGlobalRef(env, exitTrap_ctx);
    (*env)->DeleteGlobalRef(env, exitTrap_exitClass);
    (*exitTrap_jvm)->DetachCurrentThread(exitTrap_jvm);
    old_exit(code);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_redirectStdio(JNIEnv *env, jclass clazz, jstring path) {
    const char *file = (*env)->GetStringUTFChars(env, path, 0);

    int fd = open(file, O_WRONLY | O_CREAT | O_TRUNC, 0666);
    if (fd != -1) {
        dup2(fd, 1);
        dup2(fd, 2);
    }

    (*env)->ReleaseStringUTFChars(env, path, file);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_chdir(JNIEnv *env, jclass clazz, jstring path) {
    const char *dir = (*env)->GetStringUTFChars(env, path, 0);

    int ret = chdir(dir);

    (*env)->ReleaseStringUTFChars(env, path, dir);
    return ret;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_setenv(JNIEnv *env, jclass clazz, jstring str1,
                                                       jstring str2) {
    const char *name = (*env)->GetStringUTFChars(env, str1, 0);
    const char *value = (*env)->GetStringUTFChars(env, str2, 0);

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, str1, name);
    (*env)->ReleaseStringUTFChars(env, str2, value);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_dlopen(JNIEnv *env, jclass clazz, jstring str1) {
    const char *lib_name = (*env)->GetStringUTFChars(env, str1, 0);

    void *handle = dlopen(lib_name, RTLD_GLOBAL);
    if (handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "loading %s (error = %s)", lib_name, dlerror());
        (*env)->ReleaseStringUTFChars(env, str1, lib_name);
        return -1;
    }

    (*env)->ReleaseStringUTFChars(env, str1, lib_name);
    return 0;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_setupExitTrap(JNIEnv *env, jclass clazz,
                                                              jobject context) {
    exitTrap_ctx = (*env)->NewGlobalRef(env, context);
    (*env)->GetJavaVM(env, &exitTrap_jvm);
    exitTrap_exitClass = (*env)->NewGlobalRef(env,
                                              (*env)->FindClass(env,
                                                                "org/koishi/launcher/h2co3/boat/H2CO3BoatActivity"));
    exitTrap_staticMethod = (*env)->GetStaticMethodID(env, exitTrap_exitClass, "onExit",
                                                      "(Landroid/content/Context;I)V");
    xhook_enable_debug(1);
    xhook_register(".*\\.so$", "exit", custom_exit, (void **) &old_exit);
    xhook_refresh(1);
}

JNIEXPORT int JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_dlexec(JNIEnv *env, jclass clazz,
                                                       jobjectArray argsArray) {
    int argc = (*env)->GetArrayLength(env, argsArray);
    char *argv[argc];
    for (int i = 0; i < argc; i++) {
        jstring str = (*env)->GetObjectArrayElement(env, argsArray, i);
        const char *arg = (*env)->GetStringUTFChars(env, str, 0);
        argv[i] = strdup(arg);
        (*env)->ReleaseStringUTFChars(env, str, arg);
        (*env)->DeleteLocalRef(env, str);  // 释放局部引用
    }
    char **envp = environ;

    jstring str0 = (*env)->GetObjectArrayElement(env, argsArray, 0);
    const char *lib_name = (*env)->GetStringUTFChars(env, str0, 0);

    void *handle = dlopen(lib_name, RTLD_GLOBAL);
    if (handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "loading %s (error = %s)", lib_name, dlerror());
        (*env)->ReleaseStringUTFChars(env, str0, lib_name);
        return -1;
    }

    int (*main_func)(int, char **, char **) = (int (*)()) dlsym(handle, "main");
    if (main_func == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "getting main() in %s (error = %s)", lib_name,
                            dlerror());
        (*env)->ReleaseStringUTFChars(env, str0, lib_name);
        return -2;
    }
    int ret = main_func(argc, argv, envp);
    (*env)->ReleaseStringUTFChars(env, str0, lib_name);

    // 释放argv数组中的内存
    for (int i = 0; i < argc; i++) {
        free(argv[i]);
    }

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

void stub() {

}

#define PAGE_START(x) ((void*)((unsigned long)(x) & ~((unsigned long)getpagesize() - 1)))

void patchLinker(JNIEnv *env, jclass clazz) {
    void *libdl_handle = dlopen("libdl.so", RTLD_GLOBAL);
    if (libdl_handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "BOAT", "Failed to open libdl.so");
        return;
    }

    unsigned *dlopen_addr = (unsigned *) dlsym(libdl_handle, "dlopen");
    unsigned *dlsym_addr = (unsigned *) dlsym(libdl_handle, "dlsym");
    unsigned *dlvsym_addr = (unsigned *) dlsym(libdl_handle, "dlvsym");
    unsigned *buffer = (unsigned *) dlsym(libdl_handle, "android_get_LD_LIBRARY_PATH");
    if (dlopen_addr == NULL || dlsym_addr == NULL || dlvsym_addr == NULL || buffer == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "BOAT", "Failed to find symbols");
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
        __android_log_print(ANDROID_LOG_ERROR, "BOAT", "dlopen() not patched");
    }
    if (dlsym_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, "BOAT", "dlsym() not patched");
    }
    if (dlvsym_hooked == 0) {
        __android_log_print(ANDROID_LOG_ERROR, "BOAT", "dlvsym() not patched");
    }

    dlclose(libdl_handle);
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_patchLinker(JNIEnv *env, jclass clazz) {
    patchLinker(env, clazz);
}