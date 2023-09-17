package cosine.boat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import org.koishi.launcher.h2co3.core.utils.cainiaohh.CHTools;

import java.io.File;

public class BoatInput {

    public static final int KeyPress = 2;
    public static final int KeyRelease = 3;
    public static final int ButtonPress = 4;
    public static final int ButtonRelease = 5;
    public static final int MotionNotify = 6;
    public static final int ConfigureNotify = 22;

    public static final int Button1 = 1;
    public static final int Button2 = 2;
    public static final int Button3 = 3;
    public static final int Button4 = 4;
    public static final int Button5 = 5;
    public static final int Button6 = 6;
    public static final int Button7 = 7;

    public static final int CursorEnabled = 1;
    public static final int CursorDisabled = 0;

    static {
        System.loadLibrary("boat");
    }

    public static void setMouseButton(int button, boolean press) {
        pushEventMouseButton(button, press);
    }

    public static void setPointer(int x, int y) {
        pushEventPointer(x, y);
    }

    public static void setKey(int keyCode, int keyChar, boolean press) {
        pushEventKey(keyCode, keyChar, press);
    }

    public static native void setEventPipe();

    public static native void pushEvent(long time, int type, int p1, int p2);

    public static void pushEventMouseButton(int button, boolean press) {
        BoatInput.pushEvent(System.nanoTime(), press ? ButtonPress : ButtonRelease, button, 0);
    }

    public static void pushEventPointer(int x, int y) {
        BoatInput.pushEvent(System.nanoTime(), MotionNotify, x, y);
    }

    public static void pushEventKey(int keyCode, int keyChar, boolean press) {
        BoatInput.pushEvent(System.nanoTime(), press ? KeyPress : KeyRelease, keyCode, keyChar);
    }

    public static void pushEventWindow(int width, int height) {
        BoatInput.pushEvent(System.nanoTime(), ConfigureNotify, width, height);
    }

    public static String getPrimaryClipString() {
        return null;
    }

    public static void setPrimaryClipString(String string) {
    }

    public static void openLink(final String link) {
        Context context = CHTools.CONTEXT;
        ((Activity) context).runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String targetLink = link;
                if (targetLink.startsWith("file://")) {
                    targetLink = targetLink.replace("file://", "");
                } else if (targetLink.startsWith("file:")) {
                    targetLink = targetLink.replace("file:", "");
                }
                Uri uri;
                if (targetLink.startsWith("http")) {
                    uri = Uri.parse(targetLink);
                } else {
                    //can`t get authority by R.string.file_browser_provider
                    uri = FileProvider.getUriForFile(context, "com.tungsten.fcl.provider", new File(targetLink));
                }
                intent.setDataAndType(uri, "*/*");
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("openLink error", e.toString());
            }
        });
    }

    public static native int[] getPointer();

}
