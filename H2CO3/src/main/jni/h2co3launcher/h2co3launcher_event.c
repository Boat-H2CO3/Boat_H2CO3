#include <android/log.h>
#include "h2co3launcher_internal.h"

H2CO3LauncherEvent current_event;

void EventQueue_init(EventQueue *queue) {
    if (queue == NULL) {
        return;
    }
    queue->count = 0;
    queue->head = NULL;
    queue->tail = NULL;
}

void EventQueue_add(EventQueue *queue, H2CO3LauncherEvent *event) {
    if (queue == NULL || event == NULL) {
        return;
    }
    QueueElement *e = calloc(1, sizeof(QueueElement));
    if (e == NULL) {
        return;
    }
    if (queue->count > 0) {
        queue->tail->next = e;
        queue->tail = e;
    } else { // count == 0
        queue->head = e;
        queue->tail = e;
    }
    queue->count++;
    memcpy(&queue->tail->event, event, sizeof(H2CO3LauncherEvent));
}

int EventQueue_take(EventQueue *queue, H2CO3LauncherEvent *event) {
    if (queue == NULL || queue->count == 0) {
        return 0;
    }
    QueueElement *e = queue->head;
    if (queue->count == 1) {
        queue->head = NULL;
        queue->tail = NULL;
    } else {
        queue->head = e->next;
    }
    queue->count--;
    if (event != NULL) {
        memcpy(event, &e->event, sizeof(H2CO3LauncherEvent));
    }
    free(e);
    return 1;
}

void EventQueue_clear(EventQueue *queue) {
    if (queue == NULL) {
        return;
    }
    while (queue->count > 0) {
        EventQueue_take(queue, NULL);
    }
}

void h2co3LauncherSetCursorMode(int mode) {
    if (h2co3Launcher == NULL || h2co3Launcher->android_jvm == 0) {
        H2CO3_INTERNAL_LOG("h2co3LauncherSetCursorMode:h2co3Launcher or android_jvm is null");
        return;
    }
    JNIEnv *env = 0;
    jint result = (*h2co3Launcher->android_jvm)->AttachCurrentThread(h2co3Launcher->android_jvm,
                                                                     &env, 0);
    if (result != JNI_OK || env == 0) {
        H2CO3_INTERNAL_LOG("h2co3LauncherSetCursorMode:Failed to attach thread");
        abort();
    }

    jclass class_H2CO3LauncherActivity = h2co3Launcher->class_H2CO3LauncherActivity;

    if (class_H2CO3LauncherActivity == 0) {
        H2CO3_INTERNAL_LOG("h2co3LauncherSetCursorMode:class_H2CO3LauncherActivity is null");
        abort();
    }

    jmethodID H2CO3LauncherActivity_setCursorMode = h2co3Launcher->setCursorMode;

    if (H2CO3LauncherActivity_setCursorMode == 0) {
        H2CO3_INTERNAL_LOG("h2co3SetCursorMode:H2CO3LauncherActivity_setCursorMode is null");
        abort();
    }
    (*env)->CallVoidMethod(env, h2co3Launcher->class_H2CO3LauncherActivity,
                           H2CO3LauncherActivity_setCursorMode, mode);
    (*h2co3Launcher->android_jvm)->DetachCurrentThread(h2co3Launcher->android_jvm);
}

int h2co3LauncherGetEventFd() {
    if (!h2co3Launcher->has_event_pipe) {
        return -1;
    }
    return h2co3Launcher->event_pipe_fd[0];
}

int h2co3LauncherWaitForEvent(int timeout) {
    if (!h2co3Launcher->has_event_pipe) {
        return 0;
    }
    struct epoll_event ev;
    int ret = epoll_wait(h2co3Launcher->epoll_fd, &ev, 1, timeout);
    if (ret > 0 && (ev.events & EPOLLIN)) {
        return 1;
    }
    return 0;
}

int h2co3LauncherPollEvent(H2CO3LauncherEvent *event) {
    if (!h2co3Launcher->has_event_pipe) {
        return 0;
    }
    if (pthread_mutex_lock(&h2co3Launcher->event_queue_mutex) != 0) {
        H2CO3_INTERNAL_LOG("h2co3LauncherPollEvent:Failed to acquire mutex");
        return 0;
    }
    char c;
    int ret = 0;
    if (read(h2co3Launcher->event_pipe_fd[0], &c, 1) > 0) {
        ret = EventQueue_take(&h2co3Launcher->event_queue, event);
    }
    if (pthread_mutex_unlock(&h2co3Launcher->event_queue_mutex) != 0) {
        H2CO3_INTERNAL_LOG("h2co3LauncherPollEvent:Failed to release mutex");
        return 0;
    }
    return ret;
}

JNIEXPORT jintArray JNICALL
Java_org_koishi_launcher_h2co3_core_game_H2CO3LauncherBridge_getPointer(JNIEnv *env, jclass thiz) {
    jintArray ja = (*env)->NewIntArray(env, 2);
    int arr[2] = {current_event.x, current_event.y};
    (*env)->SetIntArrayRegion(env, ja, 0, 2, arr);
    return ja;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_game_H2CO3LauncherBridge_pushEvent(JNIEnv *env, jclass clazz,
                                                                   jlong time,
                                                                   jint type, jint p1,
                                                                   jint p2) {
    if (!h2co3Launcher->has_event_pipe) {
        return;
    }

    H2CO3LauncherEvent event;
    event.time = time;
    event.type = type;
    event.state = 0;

    switch (type) {
        case MotionNotify:
            event.x = p1;
            event.y = p2;
            current_event.time = time;
            current_event.x = p1;
            current_event.y = p2;
            break;
        case ButtonPress:
        case ButtonRelease:
            event.button = p1;
            break;
        case KeyPress:
        case KeyRelease:
            event.keycode = p1;
            event.keychar = p2;
            break;
        case ConfigureNotify:
            event.width = p1;
            event.height = p2;
            break;
        case H2CO3LauncherMessage:
            event.message = p1;
            break;
        case KeyChar:
            event.keychar = p2;
            break;
        default:
            break;
    }

    if (pthread_mutex_lock(&h2co3Launcher->event_queue_mutex) != 0) {
        H2CO3_INTERNAL_LOG(
                "Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_pushEvent:Failed to acquire mutex");
        return;
    }

    EventQueue_add(&h2co3Launcher->event_queue, &event);

    write(h2co3Launcher->event_pipe_fd[1], "E", 1);

    if (pthread_mutex_unlock(&h2co3Launcher->event_queue_mutex) != 0) {
        H2CO3_INTERNAL_LOG(
                "Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_pushEvent:Failed to release mutex");
    }
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_game_H2CO3LauncherBridge_setEventPipe(JNIEnv *env, jclass clazz) {
    if (pipe(h2co3Launcher->event_pipe_fd) == -1) {
        H2CO3_INTERNAL_LOG(
                "Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_setEventPipe:Failed to create event pipe : %s",
                strerror(errno));
        return;
    }
    h2co3Launcher->epoll_fd = epoll_create(3);
    if (h2co3Launcher->epoll_fd == -1) {
        H2CO3_INTERNAL_LOG(
                "Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_setEventPipe:Failed to get epoll fd : %s",
                strerror(errno));
        return;
    }
    struct epoll_event ev;
    ev.events = EPOLLIN;
    ev.data.fd = h2co3Launcher->event_pipe_fd[0];
    if (epoll_ctl(h2co3Launcher->epoll_fd, EPOLL_CTL_ADD, h2co3Launcher->event_pipe_fd[0], &ev) ==
        -1) {
        H2CO3_INTERNAL_LOG(
                "Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_setEventPipe:Failed to add epoll event : %s",
                strerror(errno));
        return;
    }
    EventQueue_init(&h2co3Launcher->event_queue);
    pthread_mutex_init(&h2co3Launcher->event_queue_mutex, NULL);
    h2co3Launcher->has_event_pipe = 1;
    H2CO3_INTERNAL_LOG(
            "Java_org_koishi_launcher_h2co3_launcher_H2CO3LauncherLib_setEventPipe:Succeeded to set event pipe");
}

int injector_mode = 0;

void h2co3LauncherSetInjectorMode(int mode) {
    injector_mode = mode;
}

int h2co3LauncherGetInjectorMode() {
    return injector_mode;
}

void h2co3LauncherSetHitResultType(int type) {
    if (!h2co3Launcher->has_event_pipe) {
        return;
    }
    PrepareH2CO3LauncherBridgeJNI();
    CallH2CO3LauncherBridgeJNIFunc(, Void, setHitResultType, "(I)V", type);
}