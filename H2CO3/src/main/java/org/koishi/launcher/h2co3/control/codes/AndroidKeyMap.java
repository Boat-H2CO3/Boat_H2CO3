package org.koishi.launcher.h2co3.control.codes;


import org.koishi.launcher.h2co3.control.definitions.map.KeyMap;
import org.koishi.launcher.h2co3.core.lwjgl.input.Keyboard;

import java.util.HashMap;

public class AndroidKeyMap implements KeyMap, CoKeyMap {
    private final HashMap<Integer, String> androidKeyMap = new HashMap<>();

    public AndroidKeyMap() {
        init();
    }

    private void init() {
        this.androidKeyMap.put(7, KEYMAP_KEY_0);
        this.androidKeyMap.put(8, KEYMAP_KEY_1);
        this.androidKeyMap.put(9, KEYMAP_KEY_2);
        this.androidKeyMap.put(10, KEYMAP_KEY_3);
        this.androidKeyMap.put(11, KEYMAP_KEY_4);
        this.androidKeyMap.put(12, KEYMAP_KEY_5);
        this.androidKeyMap.put(13, KEYMAP_KEY_6);
        this.androidKeyMap.put(14, KEYMAP_KEY_7);
        this.androidKeyMap.put(15, KEYMAP_KEY_8);
        this.androidKeyMap.put(16, KEYMAP_KEY_9);
        this.androidKeyMap.put(29, KEYMAP_KEY_A);
        this.androidKeyMap.put(30, KEYMAP_KEY_B);
        this.androidKeyMap.put(31, KEYMAP_KEY_C);
        this.androidKeyMap.put(32, KEYMAP_KEY_D);
        this.androidKeyMap.put(33, KEYMAP_KEY_E);
        this.androidKeyMap.put(34, KEYMAP_KEY_F);
        this.androidKeyMap.put(35, KEYMAP_KEY_G);
        this.androidKeyMap.put(36, KEYMAP_KEY_H);
        this.androidKeyMap.put(37, KEYMAP_KEY_I);
        this.androidKeyMap.put(38, KEYMAP_KEY_J);
        this.androidKeyMap.put(39, KEYMAP_KEY_K);
        this.androidKeyMap.put(40, KEYMAP_KEY_L);
        this.androidKeyMap.put(41, KEYMAP_KEY_M);
        this.androidKeyMap.put(42, KEYMAP_KEY_N);
        this.androidKeyMap.put(43, KEYMAP_KEY_O);
        this.androidKeyMap.put(44, KEYMAP_KEY_P);
        this.androidKeyMap.put(45, KEYMAP_KEY_Q);
        this.androidKeyMap.put(46, KEYMAP_KEY_R);
        this.androidKeyMap.put(47, KEYMAP_KEY_S);
        this.androidKeyMap.put(48, KEYMAP_KEY_T);
        this.androidKeyMap.put(49, KEYMAP_KEY_U);
        this.androidKeyMap.put(50, KEYMAP_KEY_V);
        this.androidKeyMap.put(51, KEYMAP_KEY_W);
        this.androidKeyMap.put(52, KEYMAP_KEY_X);
        this.androidKeyMap.put(53, KEYMAP_KEY_Y);
        this.androidKeyMap.put(54, KEYMAP_KEY_Z);
        this.androidKeyMap.put(69, KEYMAP_KEY_MINUS);
        this.androidKeyMap.put(70, KEYMAP_KEY_EQUALS);
        this.androidKeyMap.put(71, KEYMAP_KEY_LBRACKET);
        this.androidKeyMap.put(72, KEYMAP_KEY_RBRACKET);
        this.androidKeyMap.put(74, KEYMAP_KEY_SEMICOLON);
        this.androidKeyMap.put(75, KEYMAP_KEY_APOSTROPHE);
        this.androidKeyMap.put(68, KEYMAP_KEY_GRAVE);
        this.androidKeyMap.put(73, KEYMAP_KEY_BACKSLASH);
        this.androidKeyMap.put(55, KEYMAP_KEY_COMMA);
        this.androidKeyMap.put(56, KEYMAP_KEY_PERIOD);
        this.androidKeyMap.put(76, "/");
        this.androidKeyMap.put(111, KEYMAP_KEY_ESC);
        this.androidKeyMap.put(131, KEYMAP_KEY_F1);
        this.androidKeyMap.put(132, KEYMAP_KEY_F2);
        this.androidKeyMap.put(133, KEYMAP_KEY_F3);
        this.androidKeyMap.put(134, KEYMAP_KEY_F4);
        this.androidKeyMap.put(135, KEYMAP_KEY_F5);
        this.androidKeyMap.put(136, KEYMAP_KEY_F6);
        this.androidKeyMap.put(137, KEYMAP_KEY_F7);
        this.androidKeyMap.put(138, KEYMAP_KEY_F8);
        this.androidKeyMap.put(139, KEYMAP_KEY_F9);
        this.androidKeyMap.put(140, KEYMAP_KEY_F10);
        this.androidKeyMap.put(Keyboard.KEY_NUMPADEQUALS, KEYMAP_KEY_F11);
        this.androidKeyMap.put(142, KEYMAP_KEY_F12);
        this.androidKeyMap.put(61, KEYMAP_KEY_TAB);
        this.androidKeyMap.put(4, KEYMAP_KEY_BACKSPACE);
        this.androidKeyMap.put(62, KEYMAP_KEY_SPACE);
        this.androidKeyMap.put(115, KEYMAP_KEY_CAPITAL);
        this.androidKeyMap.put(66, KEYMAP_KEY_ENTER);
        this.androidKeyMap.put(59, KEYMAP_KEY_LSHIFT);
        this.androidKeyMap.put(113, KEYMAP_KEY_LCTRL);
        this.androidKeyMap.put(57, KEYMAP_KEY_LALT);
        this.androidKeyMap.put(60, KEYMAP_KEY_RSHIFT);
        this.androidKeyMap.put(114, KEYMAP_KEY_RCTRL);
        this.androidKeyMap.put(58, KEYMAP_KEY_RALT);
        this.androidKeyMap.put(19, KEYMAP_KEY_UP);
        this.androidKeyMap.put(20, KEYMAP_KEY_DOWN);
        this.androidKeyMap.put(21, KEYMAP_KEY_LEFT);
        this.androidKeyMap.put(22, KEYMAP_KEY_RIGHT);
        this.androidKeyMap.put(92, KEYMAP_KEY_PAGEUP);
        this.androidKeyMap.put(93, KEYMAP_KEY_PAGEDOWN);
        this.androidKeyMap.put(3, KEYMAP_KEY_HOME);
        this.androidKeyMap.put(123, KEYMAP_KEY_END);
        this.androidKeyMap.put(124, KEYMAP_KEY_INSERT);
        this.androidKeyMap.put(67, KEYMAP_KEY_DELETE);
        this.androidKeyMap.put(121, KEYMAP_KEY_PAUSE);
        this.androidKeyMap.put(Keyboard.KEY_CIRCUMFLEX, KEYMAP_KEY_NUMPAD0);
        this.androidKeyMap.put(Keyboard.KEY_AT, KEYMAP_KEY_NUMPAD1);
        this.androidKeyMap.put(Keyboard.KEY_COLON, KEYMAP_KEY_NUMPAD2);
        this.androidKeyMap.put(Keyboard.KEY_UNDERLINE, KEYMAP_KEY_NUMPAD3);
        this.androidKeyMap.put(Keyboard.KEY_KANJI, KEYMAP_KEY_NUMPAD4);
        this.androidKeyMap.put(Keyboard.KEY_STOP, KEYMAP_KEY_NUMPAD5);
        this.androidKeyMap.put(Keyboard.KEY_AX, KEYMAP_KEY_NUMPAD6);
        this.androidKeyMap.put(Keyboard.KEY_UNLABELED, KEYMAP_KEY_NUMPAD7);
        this.androidKeyMap.put(152, KEYMAP_KEY_NUMPAD8);
        this.androidKeyMap.put(153, KEYMAP_KEY_NUMPAD9);
        this.androidKeyMap.put(143, KEYMAP_KEY_NUMLOCK);
        this.androidKeyMap.put(116, KEYMAP_KEY_SCROLL);
        this.androidKeyMap.put(Keyboard.KEY_NUMPADENTER, KEYMAP_KEY_SUBTRACT);
        this.androidKeyMap.put(Keyboard.KEY_RCONTROL, KEYMAP_KEY_ADD);
        this.androidKeyMap.put(158, KEYMAP_KEY_DECIMAL);
        this.androidKeyMap.put(160, KEYMAP_KEY_NUMPADENTER);
        this.androidKeyMap.put(154, KEYMAP_KEY_DIVIDE);
        this.androidKeyMap.put(155, KEYMAP_KEY_MULTIPLY);
        this.androidKeyMap.put(120, KEYMAP_KEY_PRINT);
    }

    public Object translate(java.io.Serializable obj) {
        return this.androidKeyMap.getOrDefault(obj, null);
    }
}
