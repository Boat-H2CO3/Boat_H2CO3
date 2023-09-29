#include <android/log.h>
#include "h2co3_boat_internal.h"

H2CO3BoatEvent current_event;

void EventQueue_init(EventQueue *queue) {
    queue->count = 0;
    queue->head = NULL;
    queue->tail = NULL;
}

H2CO3BoatEvent *EventQueue_add(EventQueue *queue) {
    H2CO3BoatEvent *ret = NULL;
    QueueElement *e = malloc(sizeof(QueueElement));
    if (e != NULL) {
        e->next = NULL;
        if (queue->count > 0) {
            queue->tail->next = e;
            queue->tail = e;
        } else { // count == 0
            queue->head = e;
            queue->tail = e;
        }
        queue->count++;
        ret = &queue->tail->event;
    }
    return ret;
}

int EventQueue_take(EventQueue *queue, H2CO3BoatEvent *event) {
    int ret = 0;
    if (queue->count > 0) {
        QueueElement *e = queue->head;
        if (queue->count == 1) {
            queue->head = NULL;
            queue->tail = NULL;
        } else {
            queue->head = e->next;
        }
        queue->count--;
        ret = 1;
        if (event != NULL) {
            memcpy(event, &e->event, sizeof(H2CO3BoatEvent));
        }
        free(e);
    }
    return ret;
}

void EventQueue_clear(EventQueue *queue) {
    while (queue->count > 0) {
        EventQueue_take(queue, NULL);
    }
}

void h2co3SetCursorMode(int mode) {
    if (h2co3Boat->android_jvm == 0) {
        return;
    }
    JNIEnv *env = 0;

    jint result = (*h2co3Boat->android_jvm)->AttachCurrentThread(h2co3Boat->android_jvm, &env, 0);

    if (result != JNI_OK || env == 0) {
        //__android_log_print(ANDROID_LOG_ERROR, "Boat", "Failed to attach thread to JavaVM.");
        abort();
    }

    jclass class_H2CO3BoatActivity = h2co3Boat->class_H2CO3BoatActivity;

    if (class_H2CO3BoatActivity == 0) {
        //__android_log_print(ANDROID_LOG_ERROR, "Boat", "Failed to find class: org/koishi/launcher/h2co3/boat/H2CO3BoatActivity.");
        abort();
    }

    jmethodID H2CO3BoatActivity_setCursorMode = h2co3Boat->setCursorMode;

    if (H2CO3BoatActivity_setCursorMode == 0) {
        //__android_log_print(ANDROID_LOG_ERROR, "Boat", "Failed to find method H2CO3BoatActivity::setCursorMode");
        abort();
    }
    (*env)->CallVoidMethod(env, h2co3Boat->h2co3Activity, H2CO3BoatActivity_setCursorMode, mode);
    (*env)->CallVoidMethod(env, h2co3Boat->class_H2CO3BoatActivity, h2co3Boat->setGrabCursorId,
                           mode == CursorDisabled ? JNI_TRUE : JNI_FALSE);
    (*h2co3Boat->android_jvm)->DetachCurrentThread(h2co3Boat->android_jvm);
}

int h2co3GetEventFd() {
    if (!h2co3Boat->has_event_pipe) {
        return -1;
    }
    return h2co3Boat->event_pipe_fd[0];
}

int h2co3WaitForEvent(int timeout) {
    if (!h2co3Boat->has_event_pipe) {
        return 0;
    }
    struct epoll_event ev;
    int ret = epoll_wait(h2co3Boat->epoll_fd, &ev, 1, timeout);
    if (ret > 0 && (ev.events & EPOLLIN)) {
        return 1;
    }
    return 0;
}

int h2co3PollEvent(H2CO3BoatEvent *event) {
    if (!h2co3Boat->has_event_pipe) {
        return 0;
    }
    if (pthread_mutex_lock(&h2co3Boat->event_queue_mutex)) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to acquire mutex");
        return 0;
    }
    char c;
    int ret = 0;
    if (read(h2co3Boat->event_pipe_fd[0], &c, 1) > 0) {
        ret = EventQueue_take(&h2co3Boat->event_queue, event);
    }
    if (pthread_mutex_unlock(&h2co3Boat->event_queue_mutex)) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to release mutex");
        return 0;
    }
    return ret;
}

JNIEXPORT jintArray JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatLib_getPointer(JNIEnv *env, jclass thiz) {
    jintArray ja = (*env)->NewIntArray(env, 2);
    int arr[2] = {current_event.x, current_event.y};
    (*env)->SetIntArrayRegion(env, ja, 0, 2, arr);
    return ja;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatLib_pushEvent(JNIEnv *env, jclass clazz, jlong time,
                                                           jint type, jint p1,
                                                           jint p2) {
    if (!h2co3Boat->has_event_pipe) {
        return;
    }
    if (pthread_mutex_lock(&h2co3Boat->event_queue_mutex)) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to acquire mutex");
        return;
    }
    H2CO3BoatEvent *event = EventQueue_add(&h2co3Boat->event_queue);
    if (event == NULL) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to add event to event queue");
        return;
    }
    event->time = time;
    event->type = type;
    event->state = 0;
    switch (type) {
        case MotionNotify:
            event->x = p1;
            event->y = p2;
            current_event.time = time;
            current_event.x = p1;
            current_event.y = p2;
            break;
        case ButtonPress:
        case ButtonRelease:
            event->button = p1;
            break;
        case KeyPress:
        case KeyRelease:
            event->keycode = p1;
            event->keychar = p2;
            break;
        case ConfigureNotify:
            event->width = p1;
            event->height = p2;
            break;
        case BoatMessage:
            event->message = p1;
            break;
    }
    write(h2co3Boat->event_pipe_fd[1], "E", 1);
    if (pthread_mutex_unlock(&h2co3Boat->event_queue_mutex)) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to release mutex");
    }
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_boat_H2CO3BoatLib_setEventPipe(JNIEnv *env, jclass clazz) {
    if (pipe(h2co3Boat->event_pipe_fd) == -1) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to create event pipe : %s", strerror(errno));
        return;
    }
    h2co3Boat->epoll_fd = epoll_create(3);
    if (h2co3Boat->epoll_fd == -1) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to get epoll fd : %s", strerror(errno));
        return;
    }
    struct epoll_event ev;
    ev.events = EPOLLIN;
    ev.data.fd = h2co3Boat->event_pipe_fd[0];
    if (epoll_ctl(h2co3Boat->epoll_fd, EPOLL_CTL_ADD, h2co3Boat->event_pipe_fd[0], &ev) == -1) {
        H2CO3_BOAT_INTERNAL_LOG("Failed to add epoll event : %s", strerror(errno));
        return;
    }
    EventQueue_init(&h2co3Boat->event_queue);
    pthread_mutex_init(&h2co3Boat->event_queue_mutex, NULL);
    h2co3Boat->has_event_pipe = 1;
    H2CO3_BOAT_INTERNAL_LOG("Succeeded to set event pipe");
}
int injector_mode = 0;

void h2co3SetInjectorMode(int mode) {
    injector_mode = mode;
}

int h2co3GetInjectorMode() {
    return injector_mode;
}

void h2co3SetHitResultType(int type) {
    if (!h2co3Boat->has_event_pipe) {
        return;
    }
    PrepareH2CO3BoatLibJNI();
    CallH2CO3BoatLibJNIFunc(, Void, setHitResultType, "(I)V", type);
}



