/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system.linux;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.linux.DynamicLinkLoader.RTLD_GLOBAL;
import static org.lwjgl.system.linux.DynamicLinkLoader.RTLD_LAZY;
import static org.lwjgl.system.linux.DynamicLinkLoader.dlclose;
import static org.lwjgl.system.linux.DynamicLinkLoader.dlerror;
import static org.lwjgl.system.linux.DynamicLinkLoader.dlopen;
import static org.lwjgl.system.linux.DynamicLinkLoader.dlsym;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.h2co3.H2CO3LauncherLibrary;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/**
 * Implements a {@link SharedLibrary} on the Linux OS.
 */
public class LinuxLibrary extends H2CO3LauncherLibrary {

    public LinuxLibrary(String name) {
        this(name, loadLibrary(name));
    }

    public LinuxLibrary(String name, long handle) {
        super(name, handle);
    }

    private static long loadLibrary(String name) {
        long handle;
        try (MemoryStack stack = stackPush()) {
            handle = dlopen(stack.UTF8(name), RTLD_LAZY | (PointerBuffer.is64Bit() ? RTLD_GLOBAL : 2));
        }
        if (handle == NULL) {
            throw new UnsatisfiedLinkError("Failed to dynamically load library: " + name + "(error = " + dlerror() + ")");
        }
        return handle;
    }

    @Nullable
    @Override
    public String getPath() {
        return super.getPath();
    }

    @Override
    public long getFunctionAddress(ByteBuffer functionName) {
        return dlsym(address(), functionName);
    }

    @Override
    public void free() {
        dlclose(address());
    }

}