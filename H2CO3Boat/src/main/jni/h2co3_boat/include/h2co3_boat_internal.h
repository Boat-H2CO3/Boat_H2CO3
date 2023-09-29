#ifndef H2CO3_BOAT_INTERNAL_H
#define H2CO3_BOAT_INTERNAL_H

#include <stdlib.h>
#include <pthread.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <errno.h>

#include "h2co3_boat.h"
#include "h2co3_boat_keycodes.h"

typedef struct _QueueElement {
    struct _QueueElement *next;
    H2CO3BoatEvent event;
} QueueElement;

typedef struct {
    int count;
    int capacity;
    QueueElement *head;
    QueueElement *tail;
} EventQueue;

struct H2CO3BoatInternal {
    uint8_t isLoaded;
    JavaVM *android_jvm;
    jmethodID setGrabCursorId;
    jclass class_H2CO3BoatLib;
    jclass class_H2CO3BoatActivity;
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

extern struct H2CO3BoatInternal *h2co3Boat;

#define H2CO3_BOAT_INTERNAL_LOG(x...) do { \
    fprintf(stderr, "[Boat Internal] %s:%d\n", __FILE__, __LINE__); \
    fprintf(stderr, x); \
    fprintf(stderr, "\n"); \
    fflush(stderr); \
    } while (0)

#define PrepareH2CO3BoatLibJNI() \
    JavaVM* vm = h2co3Boat->android_jvm; \
    JNIEnv* env = NULL; \
    jint attached = (*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_2); \
    if (attached == JNI_EDETACHED) { \
        attached = (*vm)->AttachCurrentThread(vm, &env, NULL); \
        if (attached != JNI_OK || env == NULL) { \
            H2CO3_BOAT_INTERNAL_LOG("Failed to attach thread to Android JavaVM."); \
        } \
    } \
    do {} while(0)

#define CallH2CO3BoatLibJNIFunc(return_exp, func_type, func_name, signature, args...) \
    jmethodID H2CO3BoatLib_##func_name = (*env)->GetStaticMethodID(env, h2co3Boat->class_H2CO3BoatLib, #func_name, signature); \
    if (H2CO3BoatLib_##func_name == NULL) { \
        H2CO3_BOAT_INTERNAL_LOG("Failed to find static method H2CO3BoatLib_"#func_name ); \
    } \
    return_exp (*env)->CallStatic##func_type##Method(env, h2co3Boat->class_H2CO3BoatLib, H2CO3BoatLib_##func_name, ##args); \
    do {} while(0)

#endif // H2CO3_BOAT_INTERNAL_H
