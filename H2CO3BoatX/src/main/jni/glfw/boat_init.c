//
// Created by Tungsten on 2022/10/11.
//
//========================================================================
// This file is derived from x11_init.c
//========================================================================

#include <internal.h>

#include <boat_keycodes.h>

#include <string.h>
#include <stdio.h>


// Create key code translation tables
// derived from wl_init.c
//
static void createKeyTables(void)
{
    int scancode;

    memset(_glfw.boat.keycodes, -1, sizeof(_glfw.boat.keycodes));
    memset(_glfw.boat.scancodes, -1, sizeof(_glfw.boat.scancodes));

    _glfw.boat.keycodes[KEY_GRAVE]      = GLFW_KEY_GRAVE_ACCENT;
    _glfw.boat.keycodes[KEY_1]          = GLFW_KEY_1;
    _glfw.boat.keycodes[KEY_2]          = GLFW_KEY_2;
    _glfw.boat.keycodes[KEY_3]          = GLFW_KEY_3;
    _glfw.boat.keycodes[KEY_4]          = GLFW_KEY_4;
    _glfw.boat.keycodes[KEY_5]          = GLFW_KEY_5;
    _glfw.boat.keycodes[KEY_6]          = GLFW_KEY_6;
    _glfw.boat.keycodes[KEY_7]          = GLFW_KEY_7;
    _glfw.boat.keycodes[KEY_8]          = GLFW_KEY_8;
    _glfw.boat.keycodes[KEY_9]          = GLFW_KEY_9;
    _glfw.boat.keycodes[KEY_0]          = GLFW_KEY_0;
    _glfw.boat.keycodes[KEY_SPACE]      = GLFW_KEY_SPACE;
    _glfw.boat.keycodes[KEY_MINUS]      = GLFW_KEY_MINUS;
    _glfw.boat.keycodes[KEY_EQUAL]      = GLFW_KEY_EQUAL;
    _glfw.boat.keycodes[KEY_Q]          = GLFW_KEY_Q;
    _glfw.boat.keycodes[KEY_W]          = GLFW_KEY_W;
    _glfw.boat.keycodes[KEY_E]          = GLFW_KEY_E;
    _glfw.boat.keycodes[KEY_R]          = GLFW_KEY_R;
    _glfw.boat.keycodes[KEY_T]          = GLFW_KEY_T;
    _glfw.boat.keycodes[KEY_Y]          = GLFW_KEY_Y;
    _glfw.boat.keycodes[KEY_U]          = GLFW_KEY_U;
    _glfw.boat.keycodes[KEY_I]          = GLFW_KEY_I;
    _glfw.boat.keycodes[KEY_O]          = GLFW_KEY_O;
    _glfw.boat.keycodes[KEY_P]          = GLFW_KEY_P;
    _glfw.boat.keycodes[KEY_LEFTBRACE]  = GLFW_KEY_LEFT_BRACKET;
    _glfw.boat.keycodes[KEY_RIGHTBRACE] = GLFW_KEY_RIGHT_BRACKET;
    _glfw.boat.keycodes[KEY_A]          = GLFW_KEY_A;
    _glfw.boat.keycodes[KEY_S]          = GLFW_KEY_S;
    _glfw.boat.keycodes[KEY_D]          = GLFW_KEY_D;
    _glfw.boat.keycodes[KEY_F]          = GLFW_KEY_F;
    _glfw.boat.keycodes[KEY_G]          = GLFW_KEY_G;
    _glfw.boat.keycodes[KEY_H]          = GLFW_KEY_H;
    _glfw.boat.keycodes[KEY_J]          = GLFW_KEY_J;
    _glfw.boat.keycodes[KEY_K]          = GLFW_KEY_K;
    _glfw.boat.keycodes[KEY_L]          = GLFW_KEY_L;
    _glfw.boat.keycodes[KEY_SEMICOLON]  = GLFW_KEY_SEMICOLON;
    _glfw.boat.keycodes[KEY_APOSTROPHE] = GLFW_KEY_APOSTROPHE;
    _glfw.boat.keycodes[KEY_Z]          = GLFW_KEY_Z;
    _glfw.boat.keycodes[KEY_X]          = GLFW_KEY_X;
    _glfw.boat.keycodes[KEY_C]          = GLFW_KEY_C;
    _glfw.boat.keycodes[KEY_V]          = GLFW_KEY_V;
    _glfw.boat.keycodes[KEY_B]          = GLFW_KEY_B;
    _glfw.boat.keycodes[KEY_N]          = GLFW_KEY_N;
    _glfw.boat.keycodes[KEY_M]          = GLFW_KEY_M;
    _glfw.boat.keycodes[KEY_COMMA]      = GLFW_KEY_COMMA;
    _glfw.boat.keycodes[KEY_DOT]        = GLFW_KEY_PERIOD;
    _glfw.boat.keycodes[KEY_SLASH]      = GLFW_KEY_SLASH;
    _glfw.boat.keycodes[KEY_BACKSLASH]  = GLFW_KEY_BACKSLASH;
    _glfw.boat.keycodes[KEY_ESC]        = GLFW_KEY_ESCAPE;
    _glfw.boat.keycodes[KEY_TAB]        = GLFW_KEY_TAB;
    _glfw.boat.keycodes[KEY_LEFTSHIFT]  = GLFW_KEY_LEFT_SHIFT;
    _glfw.boat.keycodes[KEY_RIGHTSHIFT] = GLFW_KEY_RIGHT_SHIFT;
    _glfw.boat.keycodes[KEY_LEFTCTRL]   = GLFW_KEY_LEFT_CONTROL;
    _glfw.boat.keycodes[KEY_RIGHTCTRL]  = GLFW_KEY_RIGHT_CONTROL;
    _glfw.boat.keycodes[KEY_LEFTALT]    = GLFW_KEY_LEFT_ALT;
    _glfw.boat.keycodes[KEY_RIGHTALT]   = GLFW_KEY_RIGHT_ALT;
    _glfw.boat.keycodes[KEY_LEFTMETA]   = GLFW_KEY_LEFT_SUPER;
    _glfw.boat.keycodes[KEY_RIGHTMETA]  = GLFW_KEY_RIGHT_SUPER;
    _glfw.boat.keycodes[KEY_MENU]       = GLFW_KEY_MENU;
    _glfw.boat.keycodes[KEY_NUMLOCK]    = GLFW_KEY_NUM_LOCK;
    _glfw.boat.keycodes[KEY_CAPSLOCK]   = GLFW_KEY_CAPS_LOCK;
    _glfw.boat.keycodes[KEY_PRINT]      = GLFW_KEY_PRINT_SCREEN;
    _glfw.boat.keycodes[KEY_SCROLLLOCK] = GLFW_KEY_SCROLL_LOCK;
    _glfw.boat.keycodes[KEY_PAUSE]      = GLFW_KEY_PAUSE;
    _glfw.boat.keycodes[KEY_DELETE]     = GLFW_KEY_DELETE;
    _glfw.boat.keycodes[KEY_BACKSPACE]  = GLFW_KEY_BACKSPACE;
    _glfw.boat.keycodes[KEY_ENTER]      = GLFW_KEY_ENTER;
    _glfw.boat.keycodes[KEY_HOME]       = GLFW_KEY_HOME;
    _glfw.boat.keycodes[KEY_END]        = GLFW_KEY_END;
    _glfw.boat.keycodes[KEY_PAGEUP]     = GLFW_KEY_PAGE_UP;
    _glfw.boat.keycodes[KEY_PAGEDOWN]   = GLFW_KEY_PAGE_DOWN;
    _glfw.boat.keycodes[KEY_INSERT]     = GLFW_KEY_INSERT;
    _glfw.boat.keycodes[KEY_LEFT]       = GLFW_KEY_LEFT;
    _glfw.boat.keycodes[KEY_RIGHT]      = GLFW_KEY_RIGHT;
    _glfw.boat.keycodes[KEY_DOWN]       = GLFW_KEY_DOWN;
    _glfw.boat.keycodes[KEY_UP]         = GLFW_KEY_UP;
    _glfw.boat.keycodes[KEY_F1]         = GLFW_KEY_F1;
    _glfw.boat.keycodes[KEY_F2]         = GLFW_KEY_F2;
    _glfw.boat.keycodes[KEY_F3]         = GLFW_KEY_F3;
    _glfw.boat.keycodes[KEY_F4]         = GLFW_KEY_F4;
    _glfw.boat.keycodes[KEY_F5]         = GLFW_KEY_F5;
    _glfw.boat.keycodes[KEY_F6]         = GLFW_KEY_F6;
    _glfw.boat.keycodes[KEY_F7]         = GLFW_KEY_F7;
    _glfw.boat.keycodes[KEY_F8]         = GLFW_KEY_F8;
    _glfw.boat.keycodes[KEY_F9]         = GLFW_KEY_F9;
    _glfw.boat.keycodes[KEY_F10]        = GLFW_KEY_F10;
    _glfw.boat.keycodes[KEY_F11]        = GLFW_KEY_F11;
    _glfw.boat.keycodes[KEY_F12]        = GLFW_KEY_F12;
    _glfw.boat.keycodes[KEY_F13]        = GLFW_KEY_F13;
    _glfw.boat.keycodes[KEY_F14]        = GLFW_KEY_F14;
    _glfw.boat.keycodes[KEY_F15]        = GLFW_KEY_F15;
    _glfw.boat.keycodes[KEY_F16]        = GLFW_KEY_F16;
    _glfw.boat.keycodes[KEY_F17]        = GLFW_KEY_F17;
    _glfw.boat.keycodes[KEY_F18]        = GLFW_KEY_F18;
    _glfw.boat.keycodes[KEY_F19]        = GLFW_KEY_F19;
    _glfw.boat.keycodes[KEY_F20]        = GLFW_KEY_F20;
    _glfw.boat.keycodes[KEY_F21]        = GLFW_KEY_F21;
    _glfw.boat.keycodes[KEY_F22]        = GLFW_KEY_F22;
    _glfw.boat.keycodes[KEY_F23]        = GLFW_KEY_F23;
    _glfw.boat.keycodes[KEY_F24]        = GLFW_KEY_F24;
    _glfw.boat.keycodes[KEY_KPSLASH]    = GLFW_KEY_KP_DIVIDE;
    _glfw.boat.keycodes[KEY_KPASTERISK] = GLFW_KEY_KP_MULTIPLY;
    _glfw.boat.keycodes[KEY_KPMINUS]    = GLFW_KEY_KP_SUBTRACT;
    _glfw.boat.keycodes[KEY_KPPLUS]     = GLFW_KEY_KP_ADD;
    _glfw.boat.keycodes[KEY_KP0]        = GLFW_KEY_KP_0;
    _glfw.boat.keycodes[KEY_KP1]        = GLFW_KEY_KP_1;
    _glfw.boat.keycodes[KEY_KP2]        = GLFW_KEY_KP_2;
    _glfw.boat.keycodes[KEY_KP3]        = GLFW_KEY_KP_3;
    _glfw.boat.keycodes[KEY_KP4]        = GLFW_KEY_KP_4;
    _glfw.boat.keycodes[KEY_KP5]        = GLFW_KEY_KP_5;
    _glfw.boat.keycodes[KEY_KP6]        = GLFW_KEY_KP_6;
    _glfw.boat.keycodes[KEY_KP7]        = GLFW_KEY_KP_7;
    _glfw.boat.keycodes[KEY_KP8]        = GLFW_KEY_KP_8;
    _glfw.boat.keycodes[KEY_KP9]        = GLFW_KEY_KP_9;
    _glfw.boat.keycodes[KEY_KPEQUAL]    = GLFW_KEY_KP_EQUAL;
    _glfw.boat.keycodes[KEY_KPENTER]    = GLFW_KEY_KP_ENTER;
    _glfw.boat.keycodes[KEY_KPDOT]      = GLFW_KEY_KP_DECIMAL;

    for (scancode = 0;  scancode < 256;  scancode++)
    {
        // Store the reverse translation for faster key name lookup
        if (_glfw.boat.keycodes[scancode] > 0)
            _glfw.boat.scancodes[_glfw.boat.keycodes[scancode]] = scancode;
    }

    strcpy(_glfw.boat.keynames[GLFW_KEY_GRAVE_ACCENT],   "`");
    strcpy(_glfw.boat.keynames[GLFW_KEY_1],              "1");
    strcpy(_glfw.boat.keynames[GLFW_KEY_2],              "2");
    strcpy(_glfw.boat.keynames[GLFW_KEY_3],              "3");
    strcpy(_glfw.boat.keynames[GLFW_KEY_4],              "4");
    strcpy(_glfw.boat.keynames[GLFW_KEY_5],              "5");
    strcpy(_glfw.boat.keynames[GLFW_KEY_6],              "6");
    strcpy(_glfw.boat.keynames[GLFW_KEY_7],              "7");
    strcpy(_glfw.boat.keynames[GLFW_KEY_8],              "8");
    strcpy(_glfw.boat.keynames[GLFW_KEY_9],              "9");
    strcpy(_glfw.boat.keynames[GLFW_KEY_0],              "0");
    strcpy(_glfw.boat.keynames[GLFW_KEY_MINUS],          "-");
    strcpy(_glfw.boat.keynames[GLFW_KEY_EQUAL],          "=");
    strcpy(_glfw.boat.keynames[GLFW_KEY_Q],              "q");
    strcpy(_glfw.boat.keynames[GLFW_KEY_W],              "w");
    strcpy(_glfw.boat.keynames[GLFW_KEY_E],              "e");
    strcpy(_glfw.boat.keynames[GLFW_KEY_R],              "r");
    strcpy(_glfw.boat.keynames[GLFW_KEY_T],              "t");
    strcpy(_glfw.boat.keynames[GLFW_KEY_Y],              "y");
    strcpy(_glfw.boat.keynames[GLFW_KEY_U],              "u");
    strcpy(_glfw.boat.keynames[GLFW_KEY_I],              "i");
    strcpy(_glfw.boat.keynames[GLFW_KEY_O],              "o");
    strcpy(_glfw.boat.keynames[GLFW_KEY_P],              "p");
    strcpy(_glfw.boat.keynames[GLFW_KEY_LEFT_BRACKET],   "[");
    strcpy(_glfw.boat.keynames[GLFW_KEY_RIGHT_BRACKET],  "]");
    strcpy(_glfw.boat.keynames[GLFW_KEY_A],              "a");
    strcpy(_glfw.boat.keynames[GLFW_KEY_S],              "s");
    strcpy(_glfw.boat.keynames[GLFW_KEY_D],              "d");
    strcpy(_glfw.boat.keynames[GLFW_KEY_F],              "f");
    strcpy(_glfw.boat.keynames[GLFW_KEY_G],              "g");
    strcpy(_glfw.boat.keynames[GLFW_KEY_H],              "h");
    strcpy(_glfw.boat.keynames[GLFW_KEY_J],              "j");
    strcpy(_glfw.boat.keynames[GLFW_KEY_K],              "k");
    strcpy(_glfw.boat.keynames[GLFW_KEY_L],              "l");
    strcpy(_glfw.boat.keynames[GLFW_KEY_SEMICOLON],      ";");
    strcpy(_glfw.boat.keynames[GLFW_KEY_APOSTROPHE],     "'");
    strcpy(_glfw.boat.keynames[GLFW_KEY_Z],              "z");
    strcpy(_glfw.boat.keynames[GLFW_KEY_X],              "x");
    strcpy(_glfw.boat.keynames[GLFW_KEY_C],              "c");
    strcpy(_glfw.boat.keynames[GLFW_KEY_V],              "v");
    strcpy(_glfw.boat.keynames[GLFW_KEY_B],              "b");
    strcpy(_glfw.boat.keynames[GLFW_KEY_N],              "n");
    strcpy(_glfw.boat.keynames[GLFW_KEY_M],              "m");
    strcpy(_glfw.boat.keynames[GLFW_KEY_COMMA],          ",");
    strcpy(_glfw.boat.keynames[GLFW_KEY_PERIOD],         ".");
    strcpy(_glfw.boat.keynames[GLFW_KEY_SLASH],          "/");
    strcpy(_glfw.boat.keynames[GLFW_KEY_BACKSLASH],      "\\");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_DIVIDE],      "/");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_MULTIPLY],    "*");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_ADD],         "+");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_SUBTRACT],    "-");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_0],           "0");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_1],           "1");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_2],           "2");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_3],           "3");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_4],           "4");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_5],           "5");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_6],           "6");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_7],           "7");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_8],           "8");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_9],           "9");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_EQUAL],       "=");
    strcpy(_glfw.boat.keynames[GLFW_KEY_KP_DECIMAL],     ".");
}

// Retrieve system content scale via folklore heuristics
//
static void getSystemContentScale(float* xscale, float* yscale)
{
    *xscale = 1.f;
    *yscale = 1.f;
}


//////////////////////////////////////////////////////////////////////////
//////                       GLFW platform API                      //////
//////////////////////////////////////////////////////////////////////////

int _glfwPlatformInit(void)
{
    getSystemContentScale(&_glfw.boat.contentScaleX, &_glfw.boat.contentScaleY);
    createKeyTables();

    _glfwInitTimerPOSIX();
    _glfwPollMonitorsBOAT();
    return GLFW_TRUE;
}

void _glfwPlatformTerminate(void)
{
    _glfwTerminateEGL();
}

const char* _glfwPlatformGetVersionString(void)
{
    return _GLFW_VERSION_NUMBER " BOAT EGL OSMesa"
#if defined(_POSIX_TIMERS) && defined(_POSIX_MONOTONIC_CLOCK)
        " clock_gettime"
#else
        " gettimeofday"
#endif
#if defined(__linux__)
        " evdev"
#endif
#if defined(_GLFW_BUILD_DLL)
        " shared"
#endif
        ;
}

