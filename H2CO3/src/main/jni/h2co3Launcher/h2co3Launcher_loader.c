#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <android/log.h>
#include <xhook.h>
#include <string.h>
#include <h2co3Launcher_internal.h>
#include <sys/mman.h>
#include <pthread.h>

#define FULL_VERSION "1.8.0-internal"
#define DOT_VERSION "1.8"
#define PROGNAME "java"
#define LAUNCHER_NAME "openjdk"

static char *const_progname = PROGNAME;
static const char *const_launcher = LAUNCHER_NAME;
static const char **const_jargs = NULL;
static const char **const_appclasspath = NULL;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jboolean const_javaw = JNI_FALSE;
static const jint const_ergo_class = 0;    //DEFAULT_POLICY

typedef void (*android_update_LD_LIBRARY_PATH_t)(const char *);

static volatile jobject exitTrap_bridge;
static volatile jmethodID exitTrap_method;
static JavaVM *exitTrap_jvm;
static volatile jmethodID log_method;
static JavaVM *log_pipe_jvm;
static int h2co3LauncherFd[2];
static pthread_t logger;

void correctUtfBytes(char *bytes) {
    char three = 0;
    while (*bytes != '\0') {
        unsigned char utf8 = *(bytes++);
        three = 0;
        // Switch on the high four bits.
        switch (utf8 >> 4) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
                // Bit pattern 0xxx. No need for any extra bytes.
                break;
            case 0x08:
            case 0x09:
            case 0x0a:
            case 0x0b:
            case 0x0f:
                /*
                 * Bit pattern 10xx or 1111, which are illegal start bytes.
                 * Note: 1111 is valid for normal UTF-8, but not the
                 * modified UTF-8 used here.
                 */
                *(bytes - 1) = '?';
                break;
            case 0x0e:
                // Bit pattern 1110, so there are two additional bytes.
                utf8 = *(bytes++);
                if ((utf8 & 0xc0) != 0x80) {
                    --bytes;
                    *(bytes - 1) = '?';
                    break;
                }
                three = 1;
                // Fall through to take care of the final byte.
            case 0x0c:
            case 0x0d:
                // Bit pattern 110x, so there is one additional byte.
                utf8 = *(bytes++);
                if ((utf8 & 0xc0) != 0x80) {
                    --bytes;
                    if (three)--bytes;
                    *(bytes - 1) = '?';
                }
                break;
        }
    }
}

static void *logger_thread() {
    JNIEnv *env;
    JavaVM *vm = h2co3Launcher->android_jvm;
    (*vm)->AttachCurrentThread(vm, &env, NULL);
    char buffer[1024];
    ssize_t _s;
    jstring str;
    while (1) {
        memset(buffer, '\0', sizeof(buffer));
        _s = read(h2co3LauncherFd[0], buffer, sizeof(buffer) - 1);
        if (_s < 0) {
            __android_log_print(ANDROID_LOG_ERROR, "H2CO3", "Failed to read log!");
            close(h2co3LauncherFd[0]);
            close(h2co3LauncherFd[1]);
            (*vm)->DetachCurrentThread(vm);
            return NULL;
        } else {
            buffer[_s] = '\0';
        }
        if (buffer[0] == '\0')
            continue;
        else {
            //fix "input is not valid Modified UTF-8" caused by NewStringUTF
            correctUtfBytes(buffer);
            str = (*env)->NewStringUTF(env, buffer);
            (*env)->GetJavaVM(env, &log_pipe_jvm);
            (*env)->CallVoidMethod(env, h2co3Launcher->object_H2CO3LauncherBridge, log_method, str);
            (*env)->DeleteLocalRef(env, str);
        }
    }
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_redirectStdio(JNIEnv *env,
                                                                                jobject thisObj,
                                                                                jstring path) {
    // Set the buffer for stdout and stderr
    setvbuf(stdout, NULL, _IOLBF, 0);
    setvbuf(stderr, NULL, _IONBF, 0);

    // Create a pipe for logging
    if (pipe(h2co3LauncherFd) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher", "Failed to create log pipe!");
        return 1;
    }

    // Redirect stdout and stderr to the pipe
    if (dup2(h2co3LauncherFd[1], STDOUT_FILENO) == -1 ||
        dup2(h2co3LauncherFd[1], STDERR_FILENO) == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher", "Failed to redirect stdio!");
        close(h2co3LauncherFd[0]);
        close(h2co3LauncherFd[1]);
        return 2;
    }

    // Find the Java method for logging
    jclass bridge = (*env)->FindClass(env,
                                      "org/koishi/launcher/h2co3/launcher/utils/H2CO3LauncherBridge");
    log_method = (*env)->GetMethodID(env, bridge, "receiveLog", "(Ljava/lang/String;)V");
    if (!log_method) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher", "Failed to find receive method!");
        return 3;
    }

    // Open a file descriptor for logging
    h2co3Launcher->logFile = fdopen(h2co3LauncherFd[1], "a");
    if (!h2co3Launcher->logFile) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher", "Failed to open log file!");
        close(h2co3LauncherFd[0]);
        close(h2co3LauncherFd[1]);
        return 4;
    }

    H2CO3_INTERNAL_LOG("Log pipe ready.");

    // Start the logger thread
    if (pthread_create(&logger, NULL, logger_thread, NULL) != 0) {
        fclose(h2co3Launcher->logFile);
        close(h2co3LauncherFd[0]);
        close(h2co3LauncherFd[1]);
        return 5;
    }

    pthread_detach(logger);
    return 0;
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_chdir(JNIEnv *env,
                                                                        jobject thisObj,
                                                                        jstring path) {
    const char *dir = (*env)->GetStringUTFChars(env, path, NULL);
    if (!dir) { // Check if GetStringUTFChars returned NULL
        return -1;
    }

    int result = chdir(dir);

    (*env)->ReleaseStringUTFChars(env, path, dir);
    return result;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_setenv(JNIEnv *env,
                                                                         jobject jobject,
                                                                         jstring str1,
                                                                         jstring str2) {
    char const *name = (*env)->GetStringUTFChars(env, str1, 0);
    if (name == NULL) {
        return;
    }

    char const *value = (*env)->GetStringUTFChars(env, str2, 0);
    if (value == NULL) {
        (*env)->ReleaseStringUTFChars(env, str1, name);
        return;
    }

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, str1, name);
    (*env)->ReleaseStringUTFChars(env, str2, value);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_dlopen(JNIEnv *env,
                                                                         jobject jobject,
                                                                         jstring str) {
    dlerror();

    char const *lib_name = (*env)->GetStringUTFChars(env, str, 0);
    if (lib_name == NULL) {
        return -1;
    }

    void *handle = dlopen(lib_name, RTLD_GLOBAL | RTLD_LAZY);
    char *error = dlerror();

    if (error == NULL) {
        __android_log_print(ANDROID_LOG_INFO, "H2CO3Launcher", "Successfully loaded %s", lib_name);
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher", "Error loading %s (error = %s)",
                            lib_name, error);
    }

    (*env)->ReleaseStringUTFChars(env, str, lib_name);

    return handle != NULL ? 0 : -1;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_setLdLibraryPath(JNIEnv *env,
                                                                                   jobject jobject,
                                                                                   jstring ldLibraryPath) {
    const char *libdl_names[] = {"libdl.so", "__loader_android_update_LD_LIBRARY_PATH", NULL};
    void *libdl_handle = dlopen(libdl_names[0], RTLD_LAZY);
    if (libdl_handle == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher",
                            "Failed to open %s, error = %s", libdl_names[0], dlerror());
        return;
    }

    android_update_LD_LIBRARY_PATH_t android_update_LD_LIBRARY_PATH = NULL;
    for (int i = 1; libdl_names[i] != NULL && android_update_LD_LIBRARY_PATH == NULL; i++) {
        android_update_LD_LIBRARY_PATH = (android_update_LD_LIBRARY_PATH_t) dlsym(libdl_handle,
                                                                                  libdl_names[i]);
        if (android_update_LD_LIBRARY_PATH == NULL) {
            __android_log_print(ANDROID_LOG_WARN, "H2CO3Launcher",
                                "Failed to find symbol %s, error = %s", libdl_names[i], dlerror());
        }
    }

    if (android_update_LD_LIBRARY_PATH == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3Launcher",
                            "Could not find android_update_LD_LIBRARY_PATH");
        dlclose(libdl_handle);
        return;
    }

    const char *ldLibPathUtf = (*env)->GetStringUTFChars(env, ldLibraryPath, 0);
    android_update_LD_LIBRARY_PATH(ldLibPathUtf);
    (*env)->ReleaseStringUTFChars(env, ldLibraryPath, ldLibPathUtf);
    dlclose(libdl_handle);
}

void (*old_exit)(int code);

void custom_exit(int code) {
    __android_log_print(code == 0 ? ANDROID_LOG_INFO : ANDROID_LOG_ERROR, "H2CO3Launcher",
                        "JVM exit with code %d.", code);
    JNIEnv *env;
    (*exitTrap_jvm)->AttachCurrentThread(exitTrap_jvm, &env, NULL);
    (*env)->CallVoidMethod(env, exitTrap_bridge, exitTrap_method, code);
    (*env)->DeleteGlobalRef(env, exitTrap_bridge);
    (*exitTrap_jvm)->DetachCurrentThread(exitTrap_jvm);
    old_exit(code);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_setupExitTrap(JNIEnv *env,
                                                                                jobject jobject1,
                                                                                jobject bridge) {
    exitTrap_bridge = (*env)->NewGlobalRef(env, bridge);
    (*env)->GetJavaVM(env, &exitTrap_jvm);
    jclass exitTrap_exitClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env,
                                                                            "org/koishi/launcher/h2co3/launcher/utils/H2CO3LauncherBridge"));
    exitTrap_method = (*env)->GetMethodID(env, exitTrap_exitClass, "onExit", "(I)V");
    (*env)->DeleteGlobalRef(env, exitTrap_exitClass);
    // Enable xhook debug mode here
    // xhook_enable_debug(1);
    xhook_register(".*\\.so$", "exit", custom_exit, (void **) &old_exit);
    return xhook_refresh(1);
}

int
(*JLI_Launch)(int argc, char **argv,              /* main argc, argc */
              int jargc, const char **jargv,          /* java args */
              int appclassc, const char **appclassv,  /* app classpath */
              const char *fullversion,                /* full version defined */
              const char *dotversion,                 /* dot version defined */
              const char *pname,                      /* program name */
              const char *lname,                      /* launcher name */
              jboolean javaargs,                      /* JAVA_ARGS */
              jboolean cpwildcard,                    /* classpath wildcard */
              jboolean javaw,                         /* windows-only javaw */
              jint ergo_class                     /* ergnomics policy */
);

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_setupJLI(JNIEnv *env,
                                                                           jobject jobject) {
    void *handle;
    handle = dlopen("libjli.so", RTLD_LAZY | RTLD_GLOBAL);
    if (!handle) {
        fprintf(stderr, "Error loading libjli.so: %s\n", dlerror());
        exit(1);
    }

    JLI_Launch = (int (*)(int, char **, int, const char **, int, const char **, const char *,
                          const char *, const char *, const char *, jboolean, jboolean, jboolean,
                          jint)) dlsym(handle, "JLI_Launch");
    if (!JLI_Launch) {
        fprintf(stderr, "Error locating JLI_Launch: %s\n", dlerror());
        dlclose(handle);
        exit(1);
    }
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_launcher_utils_H2CO3LauncherBridge_jliLaunch(JNIEnv *env,
                                                                            jobject thisObj,
                                                                            jobjectArray argsArray) {
    int argCount = (*env)->GetArrayLength(env, argsArray);
    char *args[argCount];
    for (int i = 0; i < argCount; i++) {
        jstring stringArg = (jstring) (*env)->GetObjectArrayElement(env, argsArray, i);
        const char *rawString = (*env)->GetStringUTFChars(env, stringArg, 0);
        if (rawString == NULL) { // Check if GetStringUTFChars returned NULL
            for (int j = 0; j < i; j++) { // Free previously allocated memory
                free(args[j]);
            }
            return -1; // Out of memory
        }
        args[i] = (char *) malloc(strlen(rawString) + 1);
        if (args[i] == NULL) { // Check if malloc succeeded
            (*env)->ReleaseStringUTFChars(env, stringArg, rawString); // Release JNI string
            for (int j = 0; j < i; j++) { // Free previously allocated memory
                free(args[j]);
            }
            return -1; // Out of memory
        }
        strcpy(args[i], rawString);
        (*env)->ReleaseStringUTFChars(env, stringArg, rawString); // Release JNI string
    }

    for (int i = 0; i < argCount; i++) {
        __android_log_print(ANDROID_LOG_DEBUG, "H2CO3LauncherBridge", "jliLaunch: args[%d]=%s", i,
                            args[i]);
    }

    jint result = JLI_Launch(argCount, args,
                             sizeof(const_jargs) / sizeof(char *), const_jargs,
                             sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                             FULL_VERSION,
                             DOT_VERSION,
                             (const_progname != NULL) ? const_progname : args[0],
                             (const_launcher != NULL) ? const_launcher : args[0],
                             (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                             const_cpwildcard, const_javaw, const_ergo_class);

    for (int i = 0; i < argCount; i++) { // Free allocated memory
        free(args[i]);
    }

    return result;
}