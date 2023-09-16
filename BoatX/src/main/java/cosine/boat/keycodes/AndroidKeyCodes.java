package cosine.boat.keycodes;

import android.annotation.SuppressLint;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;


@SuppressLint("SuspiciousIndentation")
public class AndroidKeyCodes {
    public static final Map<Integer, Integer> keyCodeMap = new HashMap<>();

    static {

        keyCodeMap.put(KeyEvent.KEYCODE_MINUS, BoatKeycodes.KEY_MINUS);
        keyCodeMap.put(KeyEvent.KEYCODE_EQUALS, BoatKeycodes.KEY_EQUAL);
        keyCodeMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, BoatKeycodes.KEY_LEFTBRACE);
        keyCodeMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, BoatKeycodes.KEY_RIGHTBRACE);
        keyCodeMap.put(KeyEvent.KEYCODE_SEMICOLON, BoatKeycodes.KEY_SEMICOLON);
        keyCodeMap.put(KeyEvent.KEYCODE_APOSTROPHE, BoatKeycodes.KEY_APOSTROPHE);
        keyCodeMap.put(KeyEvent.KEYCODE_GRAVE, BoatKeycodes.KEY_GRAVE);
        keyCodeMap.put(KeyEvent.KEYCODE_BACKSLASH, BoatKeycodes.KEY_BACKSLASH);
        keyCodeMap.put(KeyEvent.KEYCODE_COMMA, BoatKeycodes.KEY_COMMA);
        keyCodeMap.put(KeyEvent.KEYCODE_SLASH, BoatKeycodes.KEY_SLASH);
        keyCodeMap.put(KeyEvent.KEYCODE_ESCAPE, BoatKeycodes.KEY_ESC);
        keyCodeMap.put(KeyEvent.KEYCODE_TAB, BoatKeycodes.KEY_TAB);
        keyCodeMap.put(KeyEvent.KEYCODE_BACK, BoatKeycodes.KEY_BACKSPACE);
        keyCodeMap.put(KeyEvent.KEYCODE_SPACE, BoatKeycodes.KEY_SPACE);
        keyCodeMap.put(KeyEvent.KEYCODE_CAPS_LOCK, BoatKeycodes.KEY_CAPSLOCK);
        keyCodeMap.put(KeyEvent.KEYCODE_ENTER, BoatKeycodes.KEY_KPENTER);
        keyCodeMap.put(KeyEvent.KEYCODE_SHIFT_LEFT, BoatKeycodes.KEY_LEFTSHIFT);
        keyCodeMap.put(KeyEvent.KEYCODE_CTRL_LEFT, BoatKeycodes.KEY_LEFTCTRL);
        keyCodeMap.put(KeyEvent.KEYCODE_ALT_LEFT, BoatKeycodes.KEY_LEFTALT);
        keyCodeMap.put(KeyEvent.KEYCODE_SHIFT_RIGHT, BoatKeycodes.KEY_RIGHTSHIFT);
        keyCodeMap.put(KeyEvent.KEYCODE_CTRL_RIGHT, BoatKeycodes.KEY_RIGHTCTRL);
        keyCodeMap.put(KeyEvent.KEYCODE_ALT_RIGHT, BoatKeycodes.KEY_RIGHTALT);
        keyCodeMap.put(KeyEvent.KEYCODE_DPAD_UP, BoatKeycodes.KEY_UP);
        keyCodeMap.put(KeyEvent.KEYCODE_DPAD_DOWN, BoatKeycodes.KEY_DOWN);
        keyCodeMap.put(KeyEvent.KEYCODE_DPAD_LEFT, BoatKeycodes.KEY_LEFT);
        keyCodeMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, BoatKeycodes.KEY_RIGHT);
        keyCodeMap.put(KeyEvent.KEYCODE_PAGE_UP, BoatKeycodes.KEY_PAGEUP);
        keyCodeMap.put(KeyEvent.KEYCODE_PAGE_DOWN, BoatKeycodes.KEY_PAGEDOWN);
        keyCodeMap.put(KeyEvent.KEYCODE_HOME, BoatKeycodes.KEY_HOME);
        keyCodeMap.put(KeyEvent.KEYCODE_MOVE_END, BoatKeycodes.KEY_END);
        keyCodeMap.put(KeyEvent.KEYCODE_INSERT, BoatKeycodes.KEY_INSERT);
        keyCodeMap.put(KeyEvent.KEYCODE_DEL, BoatKeycodes.KEY_DELETE);
        keyCodeMap.put(KeyEvent.KEYCODE_BREAK, BoatKeycodes.KEY_PAUSE);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_0, BoatKeycodes.KEY_KP0);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_1, BoatKeycodes.KEY_KP1);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_2, BoatKeycodes.KEY_KP2);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_3, BoatKeycodes.KEY_KP3);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_4, BoatKeycodes.KEY_KP4);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_5, BoatKeycodes.KEY_KP5);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_6, BoatKeycodes.KEY_KP6);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_7, BoatKeycodes.KEY_KP7);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_8, BoatKeycodes.KEY_KP8);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_9, BoatKeycodes.KEY_KP9);
        keyCodeMap.put(KeyEvent.KEYCODE_NUM_LOCK, BoatKeycodes.KEY_NUMLOCK);
        keyCodeMap.put(KeyEvent.KEYCODE_SCROLL_LOCK, BoatKeycodes.KEY_SCROLLLOCK);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, BoatKeycodes.KEY_KPPLUS);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_DOT, BoatKeycodes.KEY_KPDOT);
        keyCodeMap.put(KeyEvent.KEYCODE_NUMPAD_ENTER, BoatKeycodes.KEY_KPENTER);
        keyCodeMap.put(KeyEvent.KEYCODE_0, BoatKeycodes.KEY_0);
        keyCodeMap.put(KeyEvent.KEYCODE_1, BoatKeycodes.KEY_1);
        keyCodeMap.put(KeyEvent.KEYCODE_2, BoatKeycodes.KEY_2);
        keyCodeMap.put(KeyEvent.KEYCODE_3, BoatKeycodes.KEY_3);
        keyCodeMap.put(KeyEvent.KEYCODE_4, BoatKeycodes.KEY_4);
        keyCodeMap.put(KeyEvent.KEYCODE_5, BoatKeycodes.KEY_5);
        keyCodeMap.put(KeyEvent.KEYCODE_6, BoatKeycodes.KEY_6);
        keyCodeMap.put(KeyEvent.KEYCODE_7, BoatKeycodes.KEY_7);
        keyCodeMap.put(KeyEvent.KEYCODE_8, BoatKeycodes.KEY_8);
        keyCodeMap.put(KeyEvent.KEYCODE_9, BoatKeycodes.KEY_9);
        //q-p
        keyCodeMap.put(KeyEvent.KEYCODE_Q, BoatKeycodes.KEY_Q);
        keyCodeMap.put(KeyEvent.KEYCODE_W, BoatKeycodes.KEY_W);
        keyCodeMap.put(KeyEvent.KEYCODE_E, BoatKeycodes.KEY_E);
        keyCodeMap.put(KeyEvent.KEYCODE_R, BoatKeycodes.KEY_R);
        keyCodeMap.put(KeyEvent.KEYCODE_T, BoatKeycodes.KEY_T);
        keyCodeMap.put(KeyEvent.KEYCODE_Y, BoatKeycodes.KEY_Y);
        keyCodeMap.put(KeyEvent.KEYCODE_U, BoatKeycodes.KEY_U);
        keyCodeMap.put(KeyEvent.KEYCODE_I, BoatKeycodes.KEY_I);
        keyCodeMap.put(KeyEvent.KEYCODE_O, BoatKeycodes.KEY_O);
        keyCodeMap.put(KeyEvent.KEYCODE_P, BoatKeycodes.KEY_P);
        //a-l
        keyCodeMap.put(KeyEvent.KEYCODE_A, BoatKeycodes.KEY_A);
        keyCodeMap.put(KeyEvent.KEYCODE_S, BoatKeycodes.KEY_S);
        keyCodeMap.put(KeyEvent.KEYCODE_D, BoatKeycodes.KEY_D);
        keyCodeMap.put(KeyEvent.KEYCODE_F, BoatKeycodes.KEY_F);
        keyCodeMap.put(KeyEvent.KEYCODE_G, BoatKeycodes.KEY_G);
        keyCodeMap.put(KeyEvent.KEYCODE_H, BoatKeycodes.KEY_H);
        keyCodeMap.put(KeyEvent.KEYCODE_J, BoatKeycodes.KEY_J);
        keyCodeMap.put(KeyEvent.KEYCODE_K, BoatKeycodes.KEY_K);
        keyCodeMap.put(KeyEvent.KEYCODE_L, BoatKeycodes.KEY_L);
        //z-m
        keyCodeMap.put(KeyEvent.KEYCODE_Z, BoatKeycodes.KEY_Z);
        keyCodeMap.put(KeyEvent.KEYCODE_X, BoatKeycodes.KEY_X);
        keyCodeMap.put(KeyEvent.KEYCODE_C, BoatKeycodes.KEY_C);
        keyCodeMap.put(KeyEvent.KEYCODE_V, BoatKeycodes.KEY_V);
        keyCodeMap.put(KeyEvent.KEYCODE_B, BoatKeycodes.KEY_B);
        keyCodeMap.put(KeyEvent.KEYCODE_N, BoatKeycodes.KEY_N);
        keyCodeMap.put(KeyEvent.KEYCODE_M, BoatKeycodes.KEY_M);
        //f1-f12
        keyCodeMap.put(KeyEvent.KEYCODE_F1, BoatKeycodes.KEY_F1);
        keyCodeMap.put(KeyEvent.KEYCODE_F2, BoatKeycodes.KEY_F2);
        keyCodeMap.put(KeyEvent.KEYCODE_F3, BoatKeycodes.KEY_F3);
        keyCodeMap.put(KeyEvent.KEYCODE_F4, BoatKeycodes.KEY_F4);
        keyCodeMap.put(KeyEvent.KEYCODE_F5, BoatKeycodes.KEY_F5);
        keyCodeMap.put(KeyEvent.KEYCODE_F6, BoatKeycodes.KEY_F6);
        keyCodeMap.put(KeyEvent.KEYCODE_F7, BoatKeycodes.KEY_F7);
        keyCodeMap.put(KeyEvent.KEYCODE_F8, BoatKeycodes.KEY_F8);
        keyCodeMap.put(KeyEvent.KEYCODE_F9, BoatKeycodes.KEY_F9);
        keyCodeMap.put(KeyEvent.KEYCODE_F10, BoatKeycodes.KEY_F10);
        keyCodeMap.put(KeyEvent.KEYCODE_F11, BoatKeycodes.KEY_F11);
        keyCodeMap.put(KeyEvent.KEYCODE_F12, BoatKeycodes.KEY_F12);


    }
}
