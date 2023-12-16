#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <android/log.h>
#include <sys/mman.h>
#include <xhook.h>

#define LOG_TAG "H2CO3"

static volatile jobject exitTrap_ctx;
static volatile jclass exitTrap_exitClass;
static volatile jmethodID exitTrap_staticMethod;
static JavaVM *exitTrap_jvm;

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
    dup2(fd, 1);
    dup2(fd, 2);

    (*env)->ReleaseStringUTFChars(env, path, file);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_chdir(JNIEnv *env, jclass clazz, jstring path) {
    const char *dir = (*env)->GetStringUTFChars(env, path, 0);

    int b = chdir(dir);

    (*env)->ReleaseStringUTFChars(env, path, dir);
    return b;
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
    dlerror();

    int ret = 0;
    const char *lib_name = (*env)->GetStringUTFChars(env, str1, 0);

    void *handle;
    dlerror();
    handle = dlopen(lib_name, RTLD_GLOBAL);
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "loading %s (error = %s)", lib_name, dlerror());

    if (handle == NULL) {
        ret = -1;
    }

    (*env)->ReleaseStringUTFChars(env, str1, lib_name);
    return ret;
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
    dlerror();

    int argc = (*env)->GetArrayLength(env, argsArray);
    char *argv[argc];
    for (int i = 0; i < argc; i++) {
        jstring str = (*env)->GetObjectArrayElement(env, argsArray, i);
        int len = (*env)->GetStringUTFLength(env, str);
        char *buf = malloc(len + 1);
        int characterLen = (*env)->GetStringLength(env, str);
        (*env)->GetStringUTFRegion(env, str, 0, characterLen, buf);
        buf[len] = 0;
        argv[i] = buf;
    }
    char **envp = environ;

    jstring str0 = (*env)->GetObjectArrayElement(env, argsArray, 0);
    const char *lib_name = (*env)->GetStringUTFChars(env, str0, 0);

    void *handle;
    handle = dlopen(lib_name, RTLD_GLOBAL);
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "loading %s (error = %s)", lib_name, dlerror());
    if (handle == NULL) {
        return -1;
    }

    int (*main_func)(int, char **, char **) = (int (*)()) dlsym(handle, "main");
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "getting main() in %s (error = %s)", lib_name,
                        dlerror());
    if (main_func == NULL) {
        return -2;
    }
    int ret = main_func(argc, argv, envp);
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

void stub() {

}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3LoadMe_patchLinker(JNIEnv *env, jclass clazz) {
    void *libdl_handle = dlopen("libdl.so", RTLD_GLOBAL);
    if (libdl_handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to open libdl.so");
        return;
    }

    void *dlopen_addr = dlsym(libdl_handle, "dlopen");
    if (dlopen_addr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to find dlopen");
        return;
    }
    void *dlsym_addr = dlsym(libdl_handle, "dlsym");
    if (dlsym_addr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to find dlsym");
        return;
    }
    void *dlvsym_addr = dlsym(libdl_handle, "dlvsym");
    if (dlvsym_addr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to find dlvsym");
        return;
    }

    void *buffer = dlsym(libdl_handle, "android_get_LD_LIBRARY_PATH");
    if (buffer == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to find android_get_LD_LIBRARY_PATH");
        return;
    }

    size_t page_size = getpagesize();
    void *page_start_buffer = (void*)((uintptr_t)buffer & ~(page_size - 1));
    void *page_start_dlopen = (void*)((uintptr_t)dlopen_addr & ~(page_size - 1));
    void *page_start_dlsym = (void*)((uintptr_t)dlsym_addr & ~(page_size - 1));
    void *page_start_dlvsym = (void*)((uintptr_t)dlvsym_addr & ~(page_size - 1));

    if (mprotect(page_start_buffer, page_size, PROT_READ | PROT_WRITE | PROT_EXEC) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to mprotect buffer");
        return;
    }
    if (mprotect(page_start_dlopen, page_size, PROT_READ | PROT_WRITE | PROT_EXEC) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to mprotect dlopen_addr");
        return;
    }
    if (mprotect(page_start_dlsym, page_size, PROT_READ | PROT_WRITE | PROT_EXEC) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to mprotect dlsym_addr");
        return;
    }
    if (mprotect(page_start_dlvsym, page_size, PROT_READ | PROT_WRITE | PROT_EXEC) != 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Failed to mprotect dlvsym_addr");
        return;
    }

    unsigned ins_ret = gen_ret(30);
    unsigned ins_mov_x0_0 = gen_mov_imm(0, 0);

    unsigned *buffer_instructions = (unsigned *)buffer;
    buffer_instructions[0] = ins_mov_x0_0;
    buffer_instructions[1] = ins_ret;

    void **tmp_addr = (void **)(buffer_instructions + 2);
    *tmp_addr = stub;

    unsigned ins_mov_x2_x30 = gen_mov_reg(2, 30);
    unsigned ins_mov_x3_x30 = gen_mov_reg(3, 30);

    int dlopen_hooked = 0;
    int dlsym_hooked = 0;
    int dlvsym_hooked = 0;

    unsigned *dlopen_instructions = (unsigned *)dlopen_addr;
    unsigned *dlsym_instructions = (unsigned *)dlsym_addr;
    unsigned *dlvsym_instructions = (unsigned *)dlvsym_addr;

    for (int i = 0; dlopen_instructions[i] != ins_ret; i++) {
        if (dlopen_instructions[i] == ins_mov_x2_x30) {
            dlopen_instructions[i] = gen_ldr_pc(2, (long)(tmp_addr - (long)&dlopen_instructions[i]));
            dlopen_hooked = 1;
            break;
        }
    }
    for (int i = 0; dlsym_instructions[i] != ins_ret; i++) {
        if (dlsym_instructions[i] == ins_mov_x2_x30) {
            dlsym_instructions[i] = gen_ldr_pc(2, (long)(tmp_addr - (long)&dlsym_instructions[i]));
            dlsym_hooked = 1;
            break;
        }
    }
    for (int i = 0; dlvsym_instructions[i] != ins_ret; i++) {
        if (dlvsym_instructions[i] == ins_mov_x3_x30) {
            dlvsym_instructions[i] = gen_ldr_pc(3, (long)(tmp_addr - (long)&dlvsym_instructions[i]));
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
}