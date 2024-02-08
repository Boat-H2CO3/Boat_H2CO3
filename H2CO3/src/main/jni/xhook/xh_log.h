// Created by caikelun on 2018-04-11.

#ifndef XH_LOG_H
#define XH_LOG_H 1

#include <android/log.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

extern android_LogPriority xh_log_priority;
extern bool enableLog;

#define XH_LOG_TAG "xhook"
#define XH_LOG_DEBUG(fmt, ...) do{if(enableLog && xh_log_priority <= ANDROID_LOG_DEBUG) __android_log_print(ANDROID_LOG_DEBUG, XH_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#define XH_LOG_INFO(fmt, ...)  do{if(enableLog && xh_log_priority <= ANDROID_LOG_INFO)  __android_log_print(ANDROID_LOG_INFO,  XH_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#define XH_LOG_WARN(fmt, ...)  do{if(enableLog && xh_log_priority <= ANDROID_LOG_WARN)  __android_log_print(ANDROID_LOG_WARN,  XH_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#define XH_LOG_ERROR(fmt, ...) do{if(enableLog && xh_log_priority <= ANDROID_LOG_ERROR) __android_log_print(ANDROID_LOG_ERROR, XH_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)

#ifdef __cplusplus
}
#endif

#endif