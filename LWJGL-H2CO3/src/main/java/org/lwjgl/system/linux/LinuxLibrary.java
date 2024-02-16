/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system.linux;

import org.lwjgl.system.*;
import org.lwjgl.system.h2co3.H2CO3LauncherLibrary;

import javax.annotation.*;

import java.nio.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.h2co3.DynamicLinkLoader.*;

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
            handle = dlopen(stack.UTF8(name), RTLD_LAZY | RTLD_LOCAL);
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
        return super.getFunctionAddress(functionName);
    }

    @Override
    public void free() {
        super.free();
    }

}