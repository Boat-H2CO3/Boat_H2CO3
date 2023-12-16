package org.koishi.launcher.h2co3.control.codes;

import org.koishi.launcher.h2co3.control.definitions.map.KeyMap;
import org.koishi.launcher.h2co3.control.definitions.map.MouseMap;

import java.util.HashMap;

public class XKeyMap implements KeyMap, CoKeyMap {
    private final HashMap<String, Integer> xKeyMap = new HashMap<>();

    public XKeyMap() {
        init();
    }

    private void init() {
        this.xKeyMap.put(KEYMAP_KEY_0, 11);
        HashMap<String, Integer> hashMap = this.xKeyMap;
        Integer valueOf = 2;
        hashMap.put(KEYMAP_KEY_1, valueOf);
        Integer valueOf2 = 3;
        hashMap.put(KEYMAP_KEY_2, valueOf2);
        Integer valueOf3 = 4;
        hashMap.put(KEYMAP_KEY_3, valueOf3);
        Integer valueOf4 = 5;
        hashMap.put(KEYMAP_KEY_4, valueOf4);
        this.xKeyMap.put(KEYMAP_KEY_5, 6);
        this.xKeyMap.put(KEYMAP_KEY_6, 7);
        this.xKeyMap.put(KEYMAP_KEY_7, 8);
        this.xKeyMap.put(KEYMAP_KEY_8, 9);
        this.xKeyMap.put(KEYMAP_KEY_9, 10);
        this.xKeyMap.put(KEYMAP_KEY_A, 30);
        this.xKeyMap.put(KEYMAP_KEY_B, 48);
        this.xKeyMap.put(KEYMAP_KEY_C, 46);
        this.xKeyMap.put(KEYMAP_KEY_D, 32);
        this.xKeyMap.put(KEYMAP_KEY_E, 18);
        this.xKeyMap.put(KEYMAP_KEY_F, 33);
        this.xKeyMap.put(KEYMAP_KEY_G, 34);
        this.xKeyMap.put(KEYMAP_KEY_H, 35);
        this.xKeyMap.put(KEYMAP_KEY_I, 23);
        this.xKeyMap.put(KEYMAP_KEY_J, 36);
        this.xKeyMap.put(KEYMAP_KEY_K, 37);
        this.xKeyMap.put(KEYMAP_KEY_L, 38);
        this.xKeyMap.put(KEYMAP_KEY_M, 50);
        this.xKeyMap.put(KEYMAP_KEY_N, 49);
        this.xKeyMap.put(KEYMAP_KEY_O, 24);
        Integer valueOf5 = 25;
        hashMap.put(KEYMAP_KEY_P, valueOf5);
        this.xKeyMap.put(KEYMAP_KEY_Q, 16);
        this.xKeyMap.put(KEYMAP_KEY_R, 19);
        this.xKeyMap.put(KEYMAP_KEY_S, 31);
        this.xKeyMap.put(KEYMAP_KEY_T, 20);
        this.xKeyMap.put(KEYMAP_KEY_U, 22);
        this.xKeyMap.put(KEYMAP_KEY_V, 47);
        this.xKeyMap.put(KEYMAP_KEY_W, 17);
        this.xKeyMap.put(KEYMAP_KEY_X, 45);
        this.xKeyMap.put(KEYMAP_KEY_Y, 21);
        this.xKeyMap.put(KEYMAP_KEY_Z, 44);
        this.xKeyMap.put(KEYMAP_KEY_MINUS, 12);
        this.xKeyMap.put(KEYMAP_KEY_EQUALS, 13);
        this.xKeyMap.put(KEYMAP_KEY_LBRACKET, 26);
        this.xKeyMap.put(KEYMAP_KEY_RBRACKET, 27);
        this.xKeyMap.put(KEYMAP_KEY_SEMICOLON, 39);
        this.xKeyMap.put(KEYMAP_KEY_APOSTROPHE, 40);
        this.xKeyMap.put(KEYMAP_KEY_GRAVE, 41);
        this.xKeyMap.put(KEYMAP_KEY_BACKSLASH, 43);
        this.xKeyMap.put(KEYMAP_KEY_COMMA, 51);
        this.xKeyMap.put(KEYMAP_KEY_PERIOD, 52);
        this.xKeyMap.put("/", 53);
        Integer valueOf6 = 1;
        hashMap.put(KEYMAP_KEY_ESC, valueOf6);
        this.xKeyMap.put(KEYMAP_KEY_F1, 59);
        this.xKeyMap.put(KEYMAP_KEY_F2, 60);
        this.xKeyMap.put(KEYMAP_KEY_F3, 61);
        this.xKeyMap.put(KEYMAP_KEY_F4, 62);
        this.xKeyMap.put(KEYMAP_KEY_F5, 63);
        this.xKeyMap.put(KEYMAP_KEY_F6, 64);
        this.xKeyMap.put(KEYMAP_KEY_F7, 65);
        this.xKeyMap.put(KEYMAP_KEY_F8, 66);
        this.xKeyMap.put(KEYMAP_KEY_F9, 67);
        this.xKeyMap.put(KEYMAP_KEY_F10, 68);
        this.xKeyMap.put(KEYMAP_KEY_F11, 87);
        this.xKeyMap.put(KEYMAP_KEY_F12, 88);
        this.xKeyMap.put(KEYMAP_KEY_TAB, 15);
        this.xKeyMap.put(KEYMAP_KEY_BACKSPACE, 14);
        this.xKeyMap.put(KEYMAP_KEY_SPACE, 57);
        this.xKeyMap.put(KEYMAP_KEY_CAPITAL, 58);
        this.xKeyMap.put(KEYMAP_KEY_ENTER, 28);
        this.xKeyMap.put(KEYMAP_KEY_LSHIFT, 42);
        this.xKeyMap.put(KEYMAP_KEY_LCTRL, 29);
        this.xKeyMap.put(KEYMAP_KEY_LALT, 56);
        this.xKeyMap.put(KEYMAP_KEY_RSHIFT, 54);
        this.xKeyMap.put(KEYMAP_KEY_RCTRL, 97);
        this.xKeyMap.put(KEYMAP_KEY_RALT, 100);
        this.xKeyMap.put(KEYMAP_KEY_UP, 103);
        this.xKeyMap.put(KEYMAP_KEY_DOWN, 108);
        this.xKeyMap.put(KEYMAP_KEY_LEFT, 105);
        this.xKeyMap.put(KEYMAP_KEY_RIGHT, 106);
        this.xKeyMap.put(KEYMAP_KEY_PAGEUP, 104);
        this.xKeyMap.put(KEYMAP_KEY_PAGEDOWN, 109);
        this.xKeyMap.put(KEYMAP_KEY_HOME, 102);
        this.xKeyMap.put(KEYMAP_KEY_END, 107);
        this.xKeyMap.put(KEYMAP_KEY_INSERT, 110);
        this.xKeyMap.put(KEYMAP_KEY_DELETE, 111);
        this.xKeyMap.put(KEYMAP_KEY_PAUSE, 119);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD0, 82);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD1, 79);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD2, 80);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD3, 81);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD4, 75);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD5, 76);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD6, 77);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD7, 71);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD8, 72);
        this.xKeyMap.put(KEYMAP_KEY_NUMPAD9, 73);
        this.xKeyMap.put(KEYMAP_KEY_NUMLOCK, 69);
        this.xKeyMap.put(KEYMAP_KEY_SCROLL, 70);
        this.xKeyMap.put(KEYMAP_KEY_SUBTRACT, 74);
        this.xKeyMap.put(KEYMAP_KEY_ADD, 78);
        this.xKeyMap.put(KEYMAP_KEY_DECIMAL, 83);
        this.xKeyMap.put(KEYMAP_KEY_NUMPADENTER, 96);
        this.xKeyMap.put(KEYMAP_KEY_DIVIDE, 98);
        this.xKeyMap.put(KEYMAP_KEY_MULTIPLY, 55);
        this.xKeyMap.put(KEYMAP_KEY_PRINT, valueOf5);
        this.xKeyMap.put(MouseMap.MOUSEMAP_BUTTON_LEFT, valueOf6);
        this.xKeyMap.put(MouseMap.MOUSEMAP_BUTTON_RIGHT, valueOf2);
        this.xKeyMap.put(MouseMap.MOUSEMAP_BUTTON_MIDDLE, valueOf);
        this.xKeyMap.put(MouseMap.MOUSEMAP_WHEEL_UP, valueOf3);
        this.xKeyMap.put(MouseMap.MOUSEMAP_WHEEL_DOWN, valueOf4);
    }

    public Object translate(java.io.Serializable obj) {
        if (this.xKeyMap.containsKey(obj)) {
            return this.xKeyMap.get(obj);
        }
        return -1;
    }
}
