// hook.c
#include <android/dlext.h>
#include <string.h>
#include <stdio.h>

// Function pointers
static void* (*android_dlopen_ext_ptr)(const char* filename,
                                       int flags,
                                       const android_dlextinfo* extinfo,
                                       const void* caller_addr);
static struct android_namespace_t* (*android_get_exported_namespace_ptr)(const char* name);

// Global handle
static void* ready_handle;

// List of namespaces
static const char *sphal_namespaces[] = {
        "sphal", "vendor", "default"
};

// Constant
static const char *vulkan_prefix = "vulkan.";

// Pass function pointers to the hook
__attribute__((visibility("default"), used)) void app__pojav_linkerhook_pass_handles(void* data, void* android_dlopen_ext, void* android_get_exported_namespace) {
    ready_handle = data;
    android_dlopen_ext_ptr = android_dlopen_ext;
    android_get_exported_namespace_ptr = android_get_exported_namespace;
}

// Custom implementation of android_dlopen_ext
__attribute__((visibility("default"), used)) void *android_dlopen_ext(const char *filename, int flags, const android_dlextinfo *extinfo) {
    if(strstr(filename, vulkan_prefix) == NULL) {
        if(android_dlopen_ext_ptr != NULL) {
            return android_dlopen_ext_ptr(filename, flags, extinfo, &android_dlopen_ext);
        }
    }
    return ready_handle;
}

// Custom implementation of android_load_sphal_library
__attribute__((visibility("default"), used)) void *android_load_sphal_library(const char *filename, int flags) {
    if(strstr(filename, vulkan_prefix) != NULL || android_dlopen_ext_ptr == NULL) {
        return ready_handle;
    }
    struct android_namespace_t* android_namespace;
    for(int i = 0; i < sizeof(sphal_namespaces) / sizeof(sphal_namespaces[0]); i++) {
        android_namespace = android_get_exported_namespace_ptr(sphal_namespaces[i]);
        if(android_namespace != NULL) {
            break;
        }
    }
    android_dlextinfo info;
    info.flags = ANDROID_DLEXT_USE_NAMESPACE;
    info.library_namespace = android_namespace;
    return android_dlopen_ext_ptr(filename, flags, &info, &android_dlopen_ext);
}

// Custom implementation of atrace_get_enabled_tags
__attribute__((visibility("default"), used)) uint64_t atrace_get_enabled_tags() {
    return 0;
}