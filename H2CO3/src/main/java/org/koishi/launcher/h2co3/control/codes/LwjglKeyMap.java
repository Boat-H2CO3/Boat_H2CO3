package org.koishi.launcher.h2co3.control.codes;


import org.koishi.launcher.h2co3.control.definitions.map.KeyMap;
import org.koishi.launcher.h2co3.core.lwjgl.input.Keyboard;

import java.util.HashMap;

public class LwjglKeyMap implements KeyMap, CoKeyMap {
    private final HashMap<String, Integer> lwjglMap = new HashMap<>();

    public LwjglKeyMap() {
        init();
    }

    private void init() {
        this.lwjglMap.put(KEYMAP_KEY_0, 11);
        this.lwjglMap.put(KEYMAP_KEY_1, 2);
        this.lwjglMap.put(KEYMAP_KEY_2, 3);
        this.lwjglMap.put(KEYMAP_KEY_3, 4);
        this.lwjglMap.put(KEYMAP_KEY_4, 5);
        this.lwjglMap.put(KEYMAP_KEY_5, 6);
        this.lwjglMap.put(KEYMAP_KEY_6, 7);
        this.lwjglMap.put(KEYMAP_KEY_7, 8);
        this.lwjglMap.put(KEYMAP_KEY_8, 9);
        this.lwjglMap.put(KEYMAP_KEY_9, 10);
        this.lwjglMap.put(KEYMAP_KEY_A, 30);
        this.lwjglMap.put(KEYMAP_KEY_B, 48);
        this.lwjglMap.put(KEYMAP_KEY_C, 46);
        this.lwjglMap.put(KEYMAP_KEY_D, 32);
        this.lwjglMap.put(KEYMAP_KEY_E, 18);
        this.lwjglMap.put(KEYMAP_KEY_F, 33);
        this.lwjglMap.put(KEYMAP_KEY_G, 34);
        this.lwjglMap.put(KEYMAP_KEY_H, 35);
        this.lwjglMap.put(KEYMAP_KEY_I, 23);
        this.lwjglMap.put(KEYMAP_KEY_J, 36);
        this.lwjglMap.put(KEYMAP_KEY_K, 37);
        this.lwjglMap.put(KEYMAP_KEY_L, 38);
        this.lwjglMap.put(KEYMAP_KEY_M, 50);
        this.lwjglMap.put(KEYMAP_KEY_N, 49);
        this.lwjglMap.put(KEYMAP_KEY_O, 24);
        this.lwjglMap.put(KEYMAP_KEY_P, 25);
        this.lwjglMap.put(KEYMAP_KEY_Q, 16);
        this.lwjglMap.put(KEYMAP_KEY_R, 19);
        this.lwjglMap.put(KEYMAP_KEY_S, 31);
        this.lwjglMap.put(KEYMAP_KEY_T, 20);
        this.lwjglMap.put(KEYMAP_KEY_U, 22);
        this.lwjglMap.put(KEYMAP_KEY_V, 47);
        this.lwjglMap.put(KEYMAP_KEY_W, 17);
        this.lwjglMap.put(KEYMAP_KEY_X, 45);
        this.lwjglMap.put(KEYMAP_KEY_Y, 21);
        this.lwjglMap.put(KEYMAP_KEY_Z, 44);
        this.lwjglMap.put(KEYMAP_KEY_MINUS, 12);
        this.lwjglMap.put(KEYMAP_KEY_EQUALS, 13);
        this.lwjglMap.put(KEYMAP_KEY_LBRACKET, 26);
        this.lwjglMap.put(KEYMAP_KEY_RBRACKET, 27);
        this.lwjglMap.put(KEYMAP_KEY_SEMICOLON, 39);
        this.lwjglMap.put(KEYMAP_KEY_APOSTROPHE, 40);
        this.lwjglMap.put(KEYMAP_KEY_GRAVE, 41);
        this.lwjglMap.put(KEYMAP_KEY_BACKSLASH, 43);
        this.lwjglMap.put(KEYMAP_KEY_COMMA, 51);
        this.lwjglMap.put(KEYMAP_KEY_PERIOD, 52);
        this.lwjglMap.put("/", 53);
        this.lwjglMap.put(KEYMAP_KEY_ESC, 1);
        this.lwjglMap.put(KEYMAP_KEY_F1, 59);
        this.lwjglMap.put(KEYMAP_KEY_F2, 60);
        this.lwjglMap.put(KEYMAP_KEY_F3, 61);
        this.lwjglMap.put(KEYMAP_KEY_F4, 62);
        this.lwjglMap.put(KEYMAP_KEY_F5, 63);
        this.lwjglMap.put(KEYMAP_KEY_F6, 64);
        this.lwjglMap.put(KEYMAP_KEY_F7, 65);
        this.lwjglMap.put(KEYMAP_KEY_F8, 66);
        this.lwjglMap.put(KEYMAP_KEY_F9, 67);
        this.lwjglMap.put(KEYMAP_KEY_F10, 68);
        this.lwjglMap.put(KEYMAP_KEY_F11, 87);
        this.lwjglMap.put(KEYMAP_KEY_F12, 88);
        this.lwjglMap.put(KEYMAP_KEY_TAB, 15);
        this.lwjglMap.put(KEYMAP_KEY_BACKSPACE, 14);
        this.lwjglMap.put(KEYMAP_KEY_SPACE, 57);
        this.lwjglMap.put(KEYMAP_KEY_CAPITAL, 58);
        this.lwjglMap.put(KEYMAP_KEY_ENTER, 28);
        this.lwjglMap.put(KEYMAP_KEY_LSHIFT, 42);
        this.lwjglMap.put(KEYMAP_KEY_LCTRL, 29);
        this.lwjglMap.put(KEYMAP_KEY_LALT, 56);
        this.lwjglMap.put(KEYMAP_KEY_RSHIFT, 54);
        this.lwjglMap.put(KEYMAP_KEY_RCTRL, Keyboard.KEY_RCONTROL);
        this.lwjglMap.put(KEYMAP_KEY_RALT, 184);
        this.lwjglMap.put(KEYMAP_KEY_UP, 200);
        this.lwjglMap.put(KEYMAP_KEY_DOWN, Keyboard.KEY_DOWN);
        this.lwjglMap.put(KEYMAP_KEY_LEFT, Keyboard.KEY_LEFT);
        this.lwjglMap.put(KEYMAP_KEY_RIGHT, Keyboard.KEY_RIGHT);
        this.lwjglMap.put(KEYMAP_KEY_PAGEUP, Keyboard.KEY_PRIOR);
        this.lwjglMap.put(KEYMAP_KEY_PAGEDOWN, Keyboard.KEY_NEXT);
        this.lwjglMap.put(KEYMAP_KEY_HOME, Keyboard.KEY_HOME);
        this.lwjglMap.put(KEYMAP_KEY_END, Keyboard.KEY_END);
        this.lwjglMap.put(KEYMAP_KEY_INSERT, Keyboard.KEY_INSERT);
        this.lwjglMap.put(KEYMAP_KEY_DELETE, Keyboard.KEY_DELETE);
        this.lwjglMap.put(KEYMAP_KEY_PAUSE, Keyboard.KEY_PAUSE);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD0, 82);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD1, 79);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD2, 80);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD3, 81);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD4, 75);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD5, 76);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD6, 77);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD7, 71);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD8, 72);
        this.lwjglMap.put(KEYMAP_KEY_NUMPAD9, 73);
        this.lwjglMap.put(KEYMAP_KEY_NUMLOCK, 69);
        this.lwjglMap.put(KEYMAP_KEY_SCROLL, 70);
        this.lwjglMap.put(KEYMAP_KEY_SUBTRACT, 74);
        this.lwjglMap.put(KEYMAP_KEY_ADD, 78);
        this.lwjglMap.put(KEYMAP_KEY_DECIMAL, 83);
        this.lwjglMap.put(KEYMAP_KEY_NUMPADENTER, Keyboard.KEY_NUMPADENTER);
        this.lwjglMap.put(KEYMAP_KEY_DIVIDE, Keyboard.KEY_DIVIDE);
        this.lwjglMap.put(KEYMAP_KEY_MULTIPLY, 55);
        this.lwjglMap.put(KEYMAP_KEY_PRINT, 183);
        this.lwjglMap.put(KEYMAP_KEY_LWIN, 219);
        this.lwjglMap.put(KEYMAP_KEY_RWIN, 220);
    }

    public Object translate(java.io.Serializable obj) {
        if (this.lwjglMap.containsKey(obj)) {
            return this.lwjglMap.get(obj);
        }
        return -1;
    }
}
