#ifndef h2co3_launcher_INTERNAL_H
#define h2co3_launcher_INTERNAL_H

#include <stdlib.h>
#include <pthread.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <errno.h>

#include "h2co3_launcher.h"
#include "h2co3_launcher_keycodes.h"

typedef struct _QueueElement {
    struct _QueueElement *next;
    H2CO3LauncherEvent event;
} QueueElement;

typedef struct {
    int count;
    int capacity;
    QueueElement *head;
    QueueElement *tail;
} EventQueue;

struct H2CO3LauncherInternal {
    uint8_t isLoaded;
    JavaVM *android_jvm;
    jclass class_H2CO3LauncherLib;
    jclass class_H2CO3LauncherActivity;
    jobject h2co3Activity;
    jmethodID setCursorMode;
    ANativeWindow *window;
    char *clipboard_string;
    EventQueue event_queue;
    pthread_mutex_t event_queue_mutex;
    int has_event_pipe;
    int event_pipe_fd[2];
    int epoll_fd;
};

extern struct H2CO3LauncherInternal *h2co3Launcher;

#define H2CO3_INTERNAL_LOG(x...) do { \
    fprintf(stderr, "[Boat Internal] %s:%d\n", __FILE__, __LINE__); \
    fprintf(stderr, x); \
    fprintf(stderr, "\n"); \
    fflush(stderr); \
    } while (0)

#define PrepareH2CO3LauncherLibJNI() \
    JavaVM* vm = h2co3Launcher->android_jvm; \
    JNIEnv* env = NULL; \
    jint attached = (*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_2); \
    if (attached == JNI_EDETACHED) { \
        attached = (*vm)->AttachCurrentThread(vm, &env, NULL); \
        if (attached != JNI_OK || env == NULL) { \
            H2CO3_INTERNAL_LOG("Failed to attach thread to Android JavaVM."); \
        } \
    } \
    do {} while(0)

#define CallH2CO3LauncherLibJNIFunc(return_exp, func_type, func_name, signature, args...) \
    jmethodID H2CO3LauncherLib_##func_name = (*env)->GetStaticMethodID(env, h2co3Launcher->class_H2CO3LauncherLib, #func_name, signature); \
    if (H2CO3LauncherLib_##func_name == NULL) { \
        H2CO3_INTERNAL_LOG("Failed to find static method H2CO3LauncherLib"#func_name ); \
    } \
    return_exp (*env)->CallStatic##func_type##Method(env, h2co3Launcher->class_H2CO3LauncherLib, H2CO3LauncherLib_##func_name, ##args); \
    do {} while(0)

#endif // h2co3_launcher_INTERNAL_H
