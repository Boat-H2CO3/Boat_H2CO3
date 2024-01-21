//
// Created by Tungsten on 2022/10/11.
//
//========================================================================
// This file is derived from x11_init.c
//========================================================================

#include <internal.h>

#include <h2co3launcher_keycodes.h>

#include <string.h>
#include <stdio.h>


// Create key code translation tables
// derived from wl_init.c
//
static void createKeyTables(void)
{
    int scancode;

    memset(_glfw.h2co3launcher.keycodes, -1, sizeof(_glfw.h2co3launcher.keycodes));
    memset(_glfw.h2co3launcher.scancodes, -1, sizeof(_glfw.h2co3launcher.scancodes));

    _glfw.h2co3launcher.keycodes[KEY_GRAVE]      = GLFW_KEY_GRAVE_ACCENT;
    _glfw.h2co3launcher.keycodes[KEY_1]          = GLFW_KEY_1;
    _glfw.h2co3launcher.keycodes[KEY_2]          = GLFW_KEY_2;
    _glfw.h2co3launcher.keycodes[KEY_3]          = GLFW_KEY_3;
    _glfw.h2co3launcher.keycodes[KEY_4]          = GLFW_KEY_4;
    _glfw.h2co3launcher.keycodes[KEY_5]          = GLFW_KEY_5;
    _glfw.h2co3launcher.keycodes[KEY_6]          = GLFW_KEY_6;
    _glfw.h2co3launcher.keycodes[KEY_7]          = GLFW_KEY_7;
    _glfw.h2co3launcher.keycodes[KEY_8]          = GLFW_KEY_8;
    _glfw.h2co3launcher.keycodes[KEY_9]          = GLFW_KEY_9;
    _glfw.h2co3launcher.keycodes[KEY_0]          = GLFW_KEY_0;
    _glfw.h2co3launcher.keycodes[KEY_SPACE]      = GLFW_KEY_SPACE;
    _glfw.h2co3launcher.keycodes[KEY_MINUS]      = GLFW_KEY_MINUS;
    _glfw.h2co3launcher.keycodes[KEY_EQUAL]      = GLFW_KEY_EQUAL;
    _glfw.h2co3launcher.keycodes[KEY_Q]          = GLFW_KEY_Q;
    _glfw.h2co3launcher.keycodes[KEY_W]          = GLFW_KEY_W;
    _glfw.h2co3launcher.keycodes[KEY_E]          = GLFW_KEY_E;
    _glfw.h2co3launcher.keycodes[KEY_R]          = GLFW_KEY_R;
    _glfw.h2co3launcher.keycodes[KEY_T]          = GLFW_KEY_T;
    _glfw.h2co3launcher.keycodes[KEY_Y]          = GLFW_KEY_Y;
    _glfw.h2co3launcher.keycodes[KEY_U]          = GLFW_KEY_U;
    _glfw.h2co3launcher.keycodes[KEY_I]          = GLFW_KEY_I;
    _glfw.h2co3launcher.keycodes[KEY_O]          = GLFW_KEY_O;
    _glfw.h2co3launcher.keycodes[KEY_P]          = GLFW_KEY_P;
    _glfw.h2co3launcher.keycodes[KEY_LEFTBRACE]  = GLFW_KEY_LEFT_BRACKET;
    _glfw.h2co3launcher.keycodes[KEY_RIGHTBRACE] = GLFW_KEY_RIGHT_BRACKET;
    _glfw.h2co3launcher.keycodes[KEY_A]          = GLFW_KEY_A;
    _glfw.h2co3launcher.keycodes[KEY_S]          = GLFW_KEY_S;
    _glfw.h2co3launcher.keycodes[KEY_D]          = GLFW_KEY_D;
    _glfw.h2co3launcher.keycodes[KEY_F]          = GLFW_KEY_F;
    _glfw.h2co3launcher.keycodes[KEY_G]          = GLFW_KEY_G;
    _glfw.h2co3launcher.keycodes[KEY_H]          = GLFW_KEY_H;
    _glfw.h2co3launcher.keycodes[KEY_J]          = GLFW_KEY_J;
    _glfw.h2co3launcher.keycodes[KEY_K]          = GLFW_KEY_K;
    _glfw.h2co3launcher.keycodes[KEY_L]          = GLFW_KEY_L;
    _glfw.h2co3launcher.keycodes[KEY_SEMICOLON]  = GLFW_KEY_SEMICOLON;
    _glfw.h2co3launcher.keycodes[KEY_APOSTROPHE] = GLFW_KEY_APOSTROPHE;
    _glfw.h2co3launcher.keycodes[KEY_Z]          = GLFW_KEY_Z;
    _glfw.h2co3launcher.keycodes[KEY_X]          = GLFW_KEY_X;
    _glfw.h2co3launcher.keycodes[KEY_C]          = GLFW_KEY_C;
    _glfw.h2co3launcher.keycodes[KEY_V]          = GLFW_KEY_V;
    _glfw.h2co3launcher.keycodes[KEY_B]          = GLFW_KEY_B;
    _glfw.h2co3launcher.keycodes[KEY_N]          = GLFW_KEY_N;
    _glfw.h2co3launcher.keycodes[KEY_M]          = GLFW_KEY_M;
    _glfw.h2co3launcher.keycodes[KEY_COMMA]      = GLFW_KEY_COMMA;
    _glfw.h2co3launcher.keycodes[KEY_DOT]        = GLFW_KEY_PERIOD;
    _glfw.h2co3launcher.keycodes[KEY_SLASH]      = GLFW_KEY_SLASH;
    _glfw.h2co3launcher.keycodes[KEY_BACKSLASH]  = GLFW_KEY_BACKSLASH;
    _glfw.h2co3launcher.keycodes[KEY_ESC]        = GLFW_KEY_ESCAPE;
    _glfw.h2co3launcher.keycodes[KEY_TAB]        = GLFW_KEY_TAB;
    _glfw.h2co3launcher.keycodes[KEY_LEFTSHIFT]  = GLFW_KEY_LEFT_SHIFT;
    _glfw.h2co3launcher.keycodes[KEY_RIGHTSHIFT] = GLFW_KEY_RIGHT_SHIFT;
    _glfw.h2co3launcher.keycodes[KEY_LEFTCTRL]   = GLFW_KEY_LEFT_CONTROL;
    _glfw.h2co3launcher.keycodes[KEY_RIGHTCTRL]  = GLFW_KEY_RIGHT_CONTROL;
    _glfw.h2co3launcher.keycodes[KEY_LEFTALT]    = GLFW_KEY_LEFT_ALT;
    _glfw.h2co3launcher.keycodes[KEY_RIGHTALT]   = GLFW_KEY_RIGHT_ALT;
    _glfw.h2co3launcher.keycodes[KEY_LEFTMETA]   = GLFW_KEY_LEFT_SUPER;
    _glfw.h2co3launcher.keycodes[KEY_RIGHTMETA]  = GLFW_KEY_RIGHT_SUPER;
    _glfw.h2co3launcher.keycodes[KEY_MENU]       = GLFW_KEY_MENU;
    _glfw.h2co3launcher.keycodes[KEY_NUMLOCK]    = GLFW_KEY_NUM_LOCK;
    _glfw.h2co3launcher.keycodes[KEY_CAPSLOCK]   = GLFW_KEY_CAPS_LOCK;
    _glfw.h2co3launcher.keycodes[KEY_PRINT]      = GLFW_KEY_PRINT_SCREEN;
    _glfw.h2co3launcher.keycodes[KEY_SCROLLLOCK] = GLFW_KEY_SCROLL_LOCK;
    _glfw.h2co3launcher.keycodes[KEY_PAUSE]      = GLFW_KEY_PAUSE;
    _glfw.h2co3launcher.keycodes[KEY_DELETE]     = GLFW_KEY_DELETE;
    _glfw.h2co3launcher.keycodes[KEY_BACKSPACE]  = GLFW_KEY_BACKSPACE;
    _glfw.h2co3launcher.keycodes[KEY_ENTER]      = GLFW_KEY_ENTER;
    _glfw.h2co3launcher.keycodes[KEY_HOME]       = GLFW_KEY_HOME;
    _glfw.h2co3launcher.keycodes[KEY_END]        = GLFW_KEY_END;
    _glfw.h2co3launcher.keycodes[KEY_PAGEUP]     = GLFW_KEY_PAGE_UP;
    _glfw.h2co3launcher.keycodes[KEY_PAGEDOWN]   = GLFW_KEY_PAGE_DOWN;
    _glfw.h2co3launcher.keycodes[KEY_INSERT]     = GLFW_KEY_INSERT;
    _glfw.h2co3launcher.keycodes[KEY_LEFT]       = GLFW_KEY_LEFT;
    _glfw.h2co3launcher.keycodes[KEY_RIGHT]      = GLFW_KEY_RIGHT;
    _glfw.h2co3launcher.keycodes[KEY_DOWN]       = GLFW_KEY_DOWN;
    _glfw.h2co3launcher.keycodes[KEY_UP]         = GLFW_KEY_UP;
    _glfw.h2co3launcher.keycodes[KEY_F1]         = GLFW_KEY_F1;
    _glfw.h2co3launcher.keycodes[KEY_F2]         = GLFW_KEY_F2;
    _glfw.h2co3launcher.keycodes[KEY_F3]         = GLFW_KEY_F3;
    _glfw.h2co3launcher.keycodes[KEY_F4]         = GLFW_KEY_F4;
    _glfw.h2co3launcher.keycodes[KEY_F5]         = GLFW_KEY_F5;
    _glfw.h2co3launcher.keycodes[KEY_F6]         = GLFW_KEY_F6;
    _glfw.h2co3launcher.keycodes[KEY_F7]         = GLFW_KEY_F7;
    _glfw.h2co3launcher.keycodes[KEY_F8]         = GLFW_KEY_F8;
    _glfw.h2co3launcher.keycodes[KEY_F9]         = GLFW_KEY_F9;
    _glfw.h2co3launcher.keycodes[KEY_F10]        = GLFW_KEY_F10;
    _glfw.h2co3launcher.keycodes[KEY_F11]        = GLFW_KEY_F11;
    _glfw.h2co3launcher.keycodes[KEY_F12]        = GLFW_KEY_F12;
    _glfw.h2co3launcher.keycodes[KEY_F13]        = GLFW_KEY_F13;
    _glfw.h2co3launcher.keycodes[KEY_F14]        = GLFW_KEY_F14;
    _glfw.h2co3launcher.keycodes[KEY_F15]        = GLFW_KEY_F15;
    _glfw.h2co3launcher.keycodes[KEY_F16]        = GLFW_KEY_F16;
    _glfw.h2co3launcher.keycodes[KEY_F17]        = GLFW_KEY_F17;
    _glfw.h2co3launcher.keycodes[KEY_F18]        = GLFW_KEY_F18;
    _glfw.h2co3launcher.keycodes[KEY_F19]        = GLFW_KEY_F19;
    _glfw.h2co3launcher.keycodes[KEY_F20]        = GLFW_KEY_F20;
    _glfw.h2co3launcher.keycodes[KEY_F21]        = GLFW_KEY_F21;
    _glfw.h2co3launcher.keycodes[KEY_F22]        = GLFW_KEY_F22;
    _glfw.h2co3launcher.keycodes[KEY_F23]        = GLFW_KEY_F23;
    _glfw.h2co3launcher.keycodes[KEY_F24]        = GLFW_KEY_F24;
    _glfw.h2co3launcher.keycodes[KEY_KPSLASH]    = GLFW_KEY_KP_DIVIDE;
    _glfw.h2co3launcher.keycodes[KEY_KPASTERISK] = GLFW_KEY_KP_MULTIPLY;
    _glfw.h2co3launcher.keycodes[KEY_KPMINUS]    = GLFW_KEY_KP_SUBTRACT;
    _glfw.h2co3launcher.keycodes[KEY_KPPLUS]     = GLFW_KEY_KP_ADD;
    _glfw.h2co3launcher.keycodes[KEY_KP0]        = GLFW_KEY_KP_0;
    _glfw.h2co3launcher.keycodes[KEY_KP1]        = GLFW_KEY_KP_1;
    _glfw.h2co3launcher.keycodes[KEY_KP2]        = GLFW_KEY_KP_2;
    _glfw.h2co3launcher.keycodes[KEY_KP3]        = GLFW_KEY_KP_3;
    _glfw.h2co3launcher.keycodes[KEY_KP4]        = GLFW_KEY_KP_4;
    _glfw.h2co3launcher.keycodes[KEY_KP5]        = GLFW_KEY_KP_5;
    _glfw.h2co3launcher.keycodes[KEY_KP6]        = GLFW_KEY_KP_6;
    _glfw.h2co3launcher.keycodes[KEY_KP7]        = GLFW_KEY_KP_7;
    _glfw.h2co3launcher.keycodes[KEY_KP8]        = GLFW_KEY_KP_8;
    _glfw.h2co3launcher.keycodes[KEY_KP9]        = GLFW_KEY_KP_9;
    _glfw.h2co3launcher.keycodes[KEY_KPEQUAL]    = GLFW_KEY_KP_EQUAL;
    _glfw.h2co3launcher.keycodes[KEY_KPENTER]    = GLFW_KEY_KP_ENTER;
    _glfw.h2co3launcher.keycodes[KEY_KPDOT]      = GLFW_KEY_KP_DECIMAL;

    for (scancode = 0;  scancode < 256;  scancode++)
    {
        // Store the reverse translation for faster key name lookup
        if (_glfw.h2co3launcher.keycodes[scancode] > 0)
            _glfw.h2co3launcher.scancodes[_glfw.h2co3launcher.keycodes[scancode]] = scancode;
    }

    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_GRAVE_ACCENT],   "`");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_1],              "1");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_2],              "2");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_3],              "3");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_4],              "4");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_5],              "5");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_6],              "6");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_7],              "7");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_8],              "8");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_9],              "9");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_0],              "0");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_MINUS],          "-");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_EQUAL],          "=");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_Q],              "q");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_W],              "w");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_E],              "e");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_R],              "r");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_T],              "t");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_Y],              "y");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_U],              "u");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_I],              "i");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_O],              "o");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_P],              "p");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_LEFT_BRACKET],   "[");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_RIGHT_BRACKET],  "]");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_A],              "a");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_S],              "s");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_D],              "d");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_F],              "f");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_G],              "g");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_H],              "h");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_J],              "j");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_K],              "k");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_L],              "l");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_SEMICOLON],      ";");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_APOSTROPHE],     "'");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_Z],              "z");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_X],              "x");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_C],              "c");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_V],              "v");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_B],              "b");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_N],              "n");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_M],              "m");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_COMMA],          ",");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_PERIOD],         ".");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_SLASH],          "/");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_BACKSLASH],      "\\");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_DIVIDE],      "/");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_MULTIPLY],    "*");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_ADD],         "+");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_SUBTRACT],    "-");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_0],           "0");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_1],           "1");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_2],           "2");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_3],           "3");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_4],           "4");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_5],           "5");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_6],           "6");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_7],           "7");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_8],           "8");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_9],           "9");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_EQUAL],       "=");
    strcpy(_glfw.h2co3launcher.keynames[GLFW_KEY_KP_DECIMAL],     ".");
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
    getSystemContentScale(&_glfw.h2co3launcher.contentScaleX, &_glfw.h2co3launcher.contentScaleY);
    createKeyTables();

    _glfwInitTimerPOSIX();
    _glfwPollMonitorsH2CO3Launcher();
    return GLFW_TRUE;
}

void _glfwPlatformTerminate(void)
{
    _glfwTerminateEGL();
}

const char* _glfwPlatformGetVersionString(void)
{
    return _GLFW_VERSION_NUMBER " H2CO3Launcher EGL OSMesa"
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

