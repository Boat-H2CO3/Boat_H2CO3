package org.koishi.launcher.h2co3.control.input.screen;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.controller.Controller;
import org.koishi.launcher.h2co3.control.definitions.map.KeyMap;
import org.koishi.launcher.h2co3.control.definitions.map.MouseMap;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.OnscreenInput;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

public class OnscreenTouchpad implements OnscreenInput, KeyMap, MouseMap {

    public final static int TOUCHPAD_MODE_SLIDE = 1;
    public final static int TOUCHPAD_MODE_POINT = 2;
    public final static int DEFAULT_HOLDING_DELAY = 500;
    private final static String TAG = "OnscreenTouchpad";
    private final static int type_1 = MOUSE_BUTTON;
    private final static int type_2 = MOUSE_POINTER;
    private final static int type_3 = MOUSE_POINTER_INC;
    private final static int MAX_MOVE_DISTANCE = 5;
    private final static long MIN_SHLDING_TIME = 100;
    private Controller mController;
    private LinearLayout onscreenTouchpad;
    private Button touchpad;
    private int touchpadMode = TOUCHPAD_MODE_POINT;
    private int inputSpeedLevel = 0; //-5 ~ 10 || 减少50% ~  增加100%
    private OnscreenTouchpadConfigDialog configDialog;
    private boolean enable;
    private int cursorDownPosX;
    private int cursorDownPosY;
    private long MIN_HOLDING_TIME = 500;
    private boolean performClick;
    private boolean hasPerformLeftClick;
    private long cursorDownTime;
    private int posX;
    private int posY;
    private int initX = 0;
    private int initY = 0;

    @Override
    public boolean load(Context context, Controller controller) {

        this.mController = controller;

        onscreenTouchpad = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.virtual_touchpad, null);
        mController.addContentView(onscreenTouchpad, new ViewGroup.LayoutParams(mController.getConfig().getScreenWidth(), mController.getConfig().getScreenHeight()));
        touchpad = onscreenTouchpad.findViewById(R.id.touchpad_button);

        touchpad.setOnTouchListener(this);

        //设定配置器
        configDialog = new OnscreenTouchpadConfigDialog(context, this);

        return true;
    }

    @Override
    public void setUiMoveable(boolean moveable) {
        // to do nothing.
    }

    @Override
    public void setUiVisibility(int visiablity) {
        onscreenTouchpad.setVisibility(visiablity);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == touchpad) {
            locateCursor(event);
            performMouseClick(event);
            return true;
        }
        return false;
    }

    private void locateCursor(MotionEvent event) {
        if (mController.isGrabbed()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    sendPointer((int) ((event.getX() - initX) * (1 + inputSpeedLevel * 0.1f)), (int) ((event.getY() - initY) * (1 + inputSpeedLevel * 0.1f)), type_3);
                    break;
                default:
                    break;
            }
            initX = (int) event.getX();
            initY = (int) event.getY();
        } else {
            switch (touchpadMode) {
                case TOUCHPAD_MODE_POINT:
                    sendPointer((int) event.getX(), (int) event.getY(), type_2);
                    break;
                case TOUCHPAD_MODE_SLIDE:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_MOVE:
                            sendPointer((int) ((event.getX() - initX) * (1 + inputSpeedLevel * 0.1f)), (int) ((event.getY() - initY) * (1 + inputSpeedLevel * 0.1f)), type_3);
                            break;
                        default:
                            break;
                    }
                    initX = (int) event.getX();
                    initY = (int) event.getY();
                    break;
                default:
                    break;
            }
        }
    }

    public void performMouseClick(MotionEvent event) {
        if (mController.isGrabbed()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cursorDownTime = System.currentTimeMillis();
                    cursorDownPosX = (int) event.getX();
                    cursorDownPosY = (int) event.getY();
                    performClick = true;
                    hasPerformLeftClick = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    long currentTime = System.currentTimeMillis();
                    if (Math.abs(cursorDownPosX - event.getX()) > MAX_MOVE_DISTANCE || Math.abs(cursorDownPosY - event.getY()) > MAX_MOVE_DISTANCE) {
                        performClick = false;
                    }
                    if (currentTime - cursorDownTime >= MIN_HOLDING_TIME && !hasPerformLeftClick && performClick) {
                        hasPerformLeftClick = true;
                        sendMouseEvent(MOUSEMAP_BUTTON_LEFT, true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!hasPerformLeftClick && performClick && System.currentTimeMillis() - cursorDownTime <= MIN_SHLDING_TIME) {
                        sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, true);
                        sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, false);
                    }
                    if (hasPerformLeftClick) {
                        sendMouseEvent(MOUSEMAP_BUTTON_LEFT, false);
                        hasPerformLeftClick = false;
                    }
                    break;
            }
        } else {
            switch (this.touchpadMode) {
                case TOUCHPAD_MODE_POINT:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            sendMouseEvent(MOUSEMAP_BUTTON_LEFT, true);
                            break;
                        case MotionEvent.ACTION_UP:
                            sendMouseEvent(MOUSEMAP_BUTTON_LEFT, false);
                            break;
                    }
                    break;
                case TOUCHPAD_MODE_SLIDE:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            cursorDownPosX = (int) event.getRawX();
                            cursorDownPosY = (int) event.getRawY();
                            cursorDownTime = event.getDownTime();
                            performClick = true;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (Math.abs(cursorDownPosX - event.getRawX()) > MAX_MOVE_DISTANCE || Math.abs(cursorDownPosY - event.getRawY()) > MAX_MOVE_DISTANCE) {
                                performClick = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (performClick) {
                                if (event.getEventTime() - cursorDownTime < MIN_HOLDING_TIME) {
                                    sendMouseEvent(MOUSEMAP_BUTTON_LEFT, true);
                                    try {
                                        Thread.sleep(20);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    sendMouseEvent(MOUSEMAP_BUTTON_LEFT, false);
                                } else {
                                    sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, true);
                                    sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, false);
                                }
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public boolean unload() {
        onscreenTouchpad.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) onscreenTouchpad.getParent();
        vg.removeView(onscreenTouchpad);
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
    }

    @Override
    public void runConfigure() {
        this.configDialog.show();
    }

    @Override
    public float[] getPos() {
        return (new float[]{posX, posY});
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = touchpad.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        touchpad.setLayoutParams(p);
        this.posX = left;
        this.posY = top;
    }

    @Override
    public int[] getSize() {
        return new int[]{touchpad.getWidth(), touchpad.getHeight()};
    }

    @Override
    public View[] getViews() {
        return new View[]{this.onscreenTouchpad};
    }

    @Override
    public int getUiVisiability() {
        return onscreenTouchpad.getVisibility();
    }

    public void setInputSpeedLevel(int level) {
        this.inputSpeedLevel = level;
    }

    public int getTouchpadMode() {
        return this.touchpadMode;
    }

    public void setTouchpadMode(int mode) {
        this.touchpadMode = mode;
    }

    private void sendPointer(int x, int y, int type) {
        mController.sendKey(new BaseKeyEvent(TAG, null, false, type, new int[]{x, y}));
    }

    private void sendMouseEvent(String name, boolean pressed) {
        mController.sendKey(new BaseKeyEvent(TAG, name, pressed, type_1, null));
    }

    @Override
    public void saveConfig() {
        //to do nothing.
    }

    public void setHoldingDelay(long delay) {
        this.MIN_HOLDING_TIME = delay;
    }

    @Override
    public boolean isEnabled() {
        return this.enable;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enable = enabled;
        updateUI();
    }

    private void updateUI() {
        if (enable) {
            setUiVisibility(View.VISIBLE);
        } else {
            setUiVisibility(View.GONE);
        }
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public Controller getController() {
        return this.mController;
    }

    private static class OnscreenTouchpadConfigDialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Dialog.OnCancelListener, RadioButton.OnCheckedChangeListener {


        private final static String TAG = "OnscreenTouchConfigDialog";
        private final static int DEFAULT_SPEED_PROGRESS = 5;
        private final static int MAX_SPEED_PROGRESS = 15;
        private final static int MIN_SPEED_PROGRESS = -5;
        private final static int MAX_DELAY_PROGRESS = 900;
        private final static int MIN_DELAY_PROGRESS = 100;
        private final static int DEFAULT_DELAY_PROGRESS = OnscreenTouchpad.DEFAULT_HOLDING_DELAY - MIN_DELAY_PROGRESS;
        private final static String spFileName = "input_onscreentouchpad_config";
        private final static int spMode = Context.MODE_PRIVATE;
        private final static String sp_speed_name = "speed";
        private final static String sp_touchpad_mode = "touchpad_mode";
        private final static String sp_delay_name = "delay";
        private final Context mContext;
        private final OnscreenInput mInput;
        AlertDialog dialog;
        private SeekBar seekbarSpeed;
        private SeekBar seekbarDelay;
        private TextView textSpeed;
        private TextView textDelay;
        private RadioButton radioSlide;
        private RadioButton radioPoint;
        private Button buttonOK;
        private Button buttonCancel;
        private Button buttonRestore;
        private int originalSpeedProgress;
        private int originalDelayProgress;

        public OnscreenTouchpadConfigDialog(@NonNull Context context, OnscreenInput input) {
            this.mContext = context;
            this.mInput = input;
            init();
        }

        private void init() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_onscreen_touchpad_config, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
            builder.setView(view);
            builder.setCancelable(true);
            dialog = builder.create();
            dialog.setOnCancelListener(this);

            this.seekbarSpeed = view.findViewById(R.id.input_onscreen_touchpad_dialog_seekbar_speed);
            this.seekbarDelay = view.findViewById(R.id.input_onscreen_touchpad_dialog_seekbar_delay);
            this.textSpeed = view.findViewById(R.id.input_onscreen_touchpad_dialog_text_speed);
            this.textDelay = view.findViewById(R.id.input_onscreen_touchpad_dialog_text_delay);
            this.radioSlide = view.findViewById(R.id.input_onscreen_touchpad_dialog_radio_slide);
            this.radioPoint = view.findViewById(R.id.input_onscreen_touchpad_dialog_radio_point);
            this.buttonOK = view.findViewById(R.id.input_onscreen_touchpad_dialog_button_ok);
            this.buttonCancel = view.findViewById(R.id.input_onscreen_touchpad_dialog_button_cancel);
            this.buttonRestore = view.findViewById(R.id.input_onscreen_touchpad_dialog_button_restore);

            for (View v : new View[]{buttonOK, buttonCancel, buttonRestore}) {
                v.setOnClickListener(this);
            }
            for (RadioButton r : new RadioButton[]{radioPoint, radioSlide}) {
                r.setOnCheckedChangeListener(this);
            }
            for (SeekBar s : new SeekBar[]{seekbarSpeed, seekbarDelay}) {
                s.setOnSeekBarChangeListener(this);
            }
            //初始化控件属性
            this.seekbarSpeed.setMax(MAX_SPEED_PROGRESS);
            this.seekbarDelay.setMax(MAX_DELAY_PROGRESS);

            loadConfigFromFile();

        }

        @Override
        public void onCancel(DialogInterface dialog) {

            if (dialog == this) {
                seekbarSpeed.setProgress(originalSpeedProgress);
                seekbarDelay.setProgress(originalDelayProgress);
                switch (originalSpeedProgress) {
                    case OnscreenTouchpad.TOUCHPAD_MODE_POINT:
                        radioPoint.setChecked(true);
                        break;
                    case OnscreenTouchpad.TOUCHPAD_MODE_SLIDE:
                        radioSlide.setChecked(true);
                        break;
                }
            }

        }

        @Override
        public void onClick(View v) {

            if (v == buttonOK) {
                dialog.dismiss();
            }

            if (v == buttonCancel) {
                dialog.cancel();
            }

            if (v == buttonRestore) {
                DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_are_you_sure_to_restore_setting), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        restoreConfig();
                    }
                });
            }

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (seekBar == seekbarSpeed) {
                int p = progress + MIN_SPEED_PROGRESS;
                String str = String.valueOf(p);
                textSpeed.setText(str);
                //设置速度等级
                ((OnscreenTouchpad) mInput).setInputSpeedLevel(p);
            }

            if (seekBar == seekbarDelay) {
                int p = progress + MIN_DELAY_PROGRESS;
                String str = p + "ms";
                textDelay.setText(str);
                //设置延迟
                ((OnscreenTouchpad) mInput).setHoldingDelay(p);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (buttonView == radioPoint) {
                if (isChecked) {
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_POINT);
                }
            }

            if (buttonView == radioSlide) {
                if (isChecked) {
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_SLIDE);
                }
            }

        }

        public void onStop() {
            saveConfigToFile();
        }

        public void show() {
            dialog.show();
            originalSpeedProgress = seekbarSpeed.getProgress();
            originalDelayProgress = seekbarDelay.getProgress();
            int originalTouchpadMode;
            if (radioSlide.isChecked()) {
                originalTouchpadMode = OnscreenTouchpad.TOUCHPAD_MODE_SLIDE;
            } else if (radioPoint.isChecked()) {
                originalTouchpadMode = OnscreenTouchpad.TOUCHPAD_MODE_POINT;
            }
        }

        private void restoreConfig() {
            seekbarSpeed.setProgress(DEFAULT_SPEED_PROGRESS);
            seekbarDelay.setProgress(DEFAULT_DELAY_PROGRESS);
            radioPoint.setChecked(true);
        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarSpeed.setProgress(MAX_SPEED_PROGRESS);
            seekbarDelay.setProgress(MAX_DELAY_PROGRESS);
            //设定存储的数据
            seekbarSpeed.setProgress(sp.getInt(sp_speed_name, DEFAULT_SPEED_PROGRESS));
            switch (sp.getInt(sp_touchpad_mode, OnscreenTouchpad.TOUCHPAD_MODE_POINT)) {
                case OnscreenTouchpad.TOUCHPAD_MODE_POINT:
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_POINT);
                    this.radioPoint.setChecked(true);
                    break;
                case OnscreenTouchpad.TOUCHPAD_MODE_SLIDE:
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_SLIDE);
                    this.radioSlide.setChecked(true);
                    break;
            }
            seekbarDelay.setProgress(sp.getInt(sp_delay_name, DEFAULT_DELAY_PROGRESS));
        }

        private void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName, spMode).edit();
            editor.putInt(sp_speed_name, seekbarSpeed.getProgress());
            if (this.radioSlide.isChecked()) {
                editor.putInt(sp_touchpad_mode, OnscreenTouchpad.TOUCHPAD_MODE_SLIDE);
            } else if (this.radioPoint.isChecked()) {
                editor.putInt(sp_touchpad_mode, OnscreenTouchpad.TOUCHPAD_MODE_POINT);
            }
            editor.putInt(sp_delay_name, seekbarDelay.getProgress());
            editor.apply();
        }
    }

}
