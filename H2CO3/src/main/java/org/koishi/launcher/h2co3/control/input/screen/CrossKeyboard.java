package org.koishi.launcher.h2co3.control.input.screen;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT_STRING;

import android.annotation.SuppressLint;
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
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.OnscreenInput;
import org.koishi.launcher.h2co3.control.input.screen.button.CrossButton;
import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

import java.util.Arrays;

public class CrossKeyboard implements OnscreenInput, KeyMap {
    public final static int SHOW_ALL = 0;
    public final static int SHOW_IN_GAME = 1;
    public final static int SHOW_OUT_GAME = 2;
    private final static int type = KEYBOARD_BUTTON;
    private final static String TAG = "CrossKey";
    private final static String spFileName = "input_crosskeyboard_config";
    private final static int spMode = Context.MODE_PRIVATE;
    private final static int widthDp = 160;
    private final static int heightDp = 160;
    private final int[] viewPos = new int[2];
    private Context mContext;
    private Controller mController;
    private LinearLayout crossKeyboard;
    private CrossButton[] crosskeyChildren;
    private boolean moveable = false;
    private boolean enable;
    private int show;
    private int posX;
    private int posY;
    private int screenWidth;
    private int screenHeight;
    private CrossKeyboardConfigDialog configDialog;
    private CrossButton[] visiableButtons = new CrossButton[]{};
    private String lastKeyName = "";
    private boolean shift = false;

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    @Override
    public boolean load(Context context, Controller controller) {
        mContext = context;
        mController = controller;
        screenWidth = mController.getConfig().getScreenWidth();
        screenHeight = mController.getConfig().getScreenHeight();

        crossKeyboard = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.virtual_crosskey, null);
        controller.addContentView(crossKeyboard, new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, widthDp), DisplayUtils.getPxFromDp(mContext, heightDp)));

        crosskeyChildren = new CrossButton[]{crossKeyboard.findViewById(R.id.crosskey_up_left), crossKeyboard.findViewById(R.id.crosskey_up), crossKeyboard.findViewById(R.id.crosskey_up_right), crossKeyboard.findViewById(R.id.crosskey_left), crossKeyboard.findViewById(R.id.crosskey_shift), crossKeyboard.findViewById(R.id.crosskey_right), crossKeyboard.findViewById(R.id.crosskey_down_left), crossKeyboard.findViewById(R.id.crosskey_down), crossKeyboard.findViewById(R.id.crosskey_down_right)};

        //设定监听器
        for (View v : crosskeyChildren) {
            v.setOnTouchListener(this);
        }
        crossKeyboard.setOnTouchListener(this);

        //设定配置器
        this.configDialog = new CrossKeyboardConfigDialog(mContext, this);

        return true;
    }

    @Override
    public boolean unload() {
        crossKeyboard.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) crossKeyboard.getParent();
        vg.removeView(crossKeyboard);
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {

        if (v instanceof CrossButton) {
            applyCrossKey(v, e);
            return true;
        }
        if ((v == crossKeyboard) && moveable) {
            moveViewByTouch(v, e);
            return true;
        }
        return false;
    }

    @Override
    public void setUiMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    @Override
    public void setUiVisibility(int visiablity) {

        SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);
        crossKeyboard.setVisibility(visiablity);
    }

    @Override
    public float[] getPos() {
        return (new float[]{posX, posY});
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = crossKeyboard.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        crossKeyboard.setLayoutParams(p);
        this.posX = left;
        this.posY = top;
    }

    @Override
    public int[] getSize() {
        return new int[]{crossKeyboard.getLayoutParams().width, crossKeyboard.getLayoutParams().height};
    }

    public void setSize(int s) {
        ViewGroup.LayoutParams p = crossKeyboard.getLayoutParams();
        p.height = s;
        p.width = s;
        //控件重绘
        crossKeyboard.requestLayout();
        crossKeyboard.invalidate();
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        updateUI();
    }

    @Override
    public void runConfigure() {
        configDialog.show();
    }

    @Override
    public View[] getViews() {
        return new View[]{this.crossKeyboard};
    }

    @Override
    public int getUiVisiability() {
        return crossKeyboard.getVisibility();
    }

    private void applyCrossKey(View v, MotionEvent e) {
        int[] boardPos = new int[2];  //CrossKey的左上角坐标
        int[] touchPos = new int[2];  //触摸位置的坐标
        int[] shiftPos = new int[2];  //触摸位置相对于CrossKey左上角的偏移量
        int buttonWidth = v.getWidth();  //按键宽度
        int buttonheight = v.getHeight();  //按键高度

        crossKeyboard.getLocationOnScreen(boardPos);
        touchPos[0] = (int) e.getRawX();
        touchPos[1] = (int) e.getRawY();
        shiftPos[0] = touchPos[0] - boardPos[0];
        shiftPos[1] = touchPos[1] - boardPos[1];

        /* 九宫格坐标
         * ________
         *  |1|2|3|
         * ---------
         *  |4|5|6|
         * ---------
         *  |7|8|9|
         * --------
         */
        int location; //九宫格位置标志 失去焦点时为 0
        //自左向右，第一列
        boolean b = shiftPos[1] <= buttonheight * 2 && shiftPos[1] >= buttonheight;
        boolean b1 = shiftPos[1] > buttonheight * 2 && shiftPos[1] <= buttonheight * 3;
        if (shiftPos[0] < buttonWidth && shiftPos[0] >= 0) {


            if (shiftPos[1] < buttonheight && shiftPos[1] >= 0) {

                location = 1;
                //左上按钮被按下

            } else if (b) {

                location = 4;
                //左中按钮被按下

            } else if (b1) {

                location = 7;
                //左下按钮被按下

            } else {
                //这种情况下触摸焦点已经不在CrossKey上
                location = 0;

            }

            //第二列
        } else if (shiftPos[0] <= buttonWidth * 2 && shiftPos[0] >= buttonWidth) {

            if (shiftPos[1] < buttonheight && shiftPos[1] >= 0) {

                location = 2;
                //上按钮被按下

            } else if (b) {

                location = 5;
                //中按钮被按下

            } else if (b1) {

                location = 8;
                //下按钮被按下

            } else {
                //这种情况下触摸焦点已经不在CrossKey上
                location = 0;
            }

            //第三列
        } else if (shiftPos[0] > buttonWidth * 2 && shiftPos[0] <= buttonWidth * 3) {

            if (shiftPos[1] < buttonheight && shiftPos[1] >= 0) {

                location = 3;
                //右下按钮被按下

            } else if (b) {

                location = 6;
                //右中按钮被按下

            } else if (b1) {

                location = 9;
                //右下按钮被按下

            } else {
                //这种情况下触摸焦点已经不在CrossKey上
                location = 0;

            }

        } else {
            location = 0;
        }

        uiUpdate(location, e);
        makeKeyEvent(location, e);


    }

    private void uiUpdate(int location, MotionEvent e) {

        CrossButton[] group;
        switch (location) {
            case 2:
                group = new CrossButton[]{crosskeyChildren[0], crosskeyChildren[2]};
                break;
            case 4:
                group = new CrossButton[]{crosskeyChildren[0], crosskeyChildren[6]};
                break;
            case 6:
                group = new CrossButton[]{crosskeyChildren[2], crosskeyChildren[8]};
                break;
            case 8:
                group = new CrossButton[]{crosskeyChildren[6], crosskeyChildren[8]};
                break;
            case 5:
            case 0:
                group = new CrossButton[]{};
                break;
            default:
                group = visiableButtons;
                break;
        }

        if (!Arrays.equals(visiableButtons, group)) {
            // to do nothing.
            for (View v : visiableButtons) {
                v.setVisibility(View.INVISIBLE);
            }
            for (View v : group) {
                v.setVisibility(View.VISIBLE);
            }
            visiableButtons = group;
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            for (View v : visiableButtons) {
                v.setVisibility(View.INVISIBLE);
            }
            visiableButtons = new CrossButton[]{};
        }

    }

    private void makeKeyEvent(int location, MotionEvent e) {

        String keyName;

        switch (location) {
            case 1:
                keyName = KEYMAP_KEY_W + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_A;
                break;
            case 2:
                keyName = KEYMAP_KEY_W;
                break;
            case 3:
                keyName = KEYMAP_KEY_W + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_D;
                break;
            case 4:
                keyName = KEYMAP_KEY_A;
                break;
            case 5:
                keyName = KEYMAP_KEY_LSHIFT;
                if (lastKeyName.equals("") && e.getAction() == MotionEvent.ACTION_DOWN) {
                    if (shift) {
                        sendKeyEvent(keyName, false);
                        shift = false;
                    } else {
                        sendKeyEvent(keyName, true);
                        shift = true;
                    }
                } else if (!lastKeyName.equals("") && !lastKeyName.equals(keyName)) {
                    sendKeyEvent(lastKeyName, false);
                    lastKeyName = "";
                }
                return;
            case 6:
                keyName = KEYMAP_KEY_D;
                break;
            case 7:
                keyName = KEYMAP_KEY_S + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_A;
                break;
            case 8:
                keyName = KEYMAP_KEY_S;
                break;
            case 9:
                keyName = KEYMAP_KEY_S + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_D;
                break;
            case 0:
            default:
                keyName = "";
                break;
        }

        switch (e.getAction()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                if (!keyName.equals(lastKeyName)) {
                    if (!lastKeyName.equals("")) {
                        sendKeyEvent(lastKeyName, false);
                    }
                    sendKeyEvent(keyName, true);
                    lastKeyName = keyName;
                }
                break;

            case MotionEvent.ACTION_UP:

                sendKeyEvent(lastKeyName, false);
                lastKeyName = "";
                break;
            default:
                break;
        }

    }

    private void moveViewByTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewPos[0] = (int) e.getRawX();
                viewPos[1] = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) e.getRawX() - viewPos[0];
                int dy = (int) e.getRawY() - viewPos[1];
                int l = v.getLeft() + dx;
                int b = v.getBottom() + dy;
                int r = v.getRight() + dx;
                int t = v.getTop() + dy;
                //下面判断移动是否超出屏幕
                if (l < 0) {
                    l = 0;
                    r = l + v.getWidth();
                }
                if (t < 0) {
                    t = 0;
                    b = t + v.getHeight();
                }
                if (r > screenWidth) {
                    r = screenWidth;
                    l = r - v.getWidth();
                }
                if (b > screenHeight) {
                    b = screenHeight;
                    t = b - v.getHeight();
                }
                v.layout(l, t, r, b);
                viewPos[0] = (int) e.getRawX();
                viewPos[1] = (int) e.getRawY();
                v.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                setMargins(v.getLeft(), v.getTop(), 0, 0);
                break;
            default:
                break;
        }
    }

    private void sendKeyEvent(String keyName, boolean pressed) {
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, type, null));
    }

    public void setAlpha(float a) {
        crossKeyboard.setAlpha(a);
    }

    @Override
    public void saveConfig() {
        configDialog.saveConfigToFile();
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
            if (mController.isGrabbed()) {
                if (show == SHOW_ALL || show == SHOW_IN_GAME) {
                    this.setUiVisibility(View.VISIBLE);
                } else {
                    this.setUiVisibility(View.GONE);
                }
            } else {
                if (show == SHOW_ALL || show == SHOW_OUT_GAME) {
                    this.setUiVisibility(View.VISIBLE);
                } else {
                    this.setUiVisibility(View.GONE);
                }
            }
        } else {
            setUiVisibility(View.GONE);
        }
    }

    public int getShowStat() {
        return this.show;
    }

    public void setShowStat(int s) {
        this.show = s;
        updateUI();
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

    private static class CrossKeyboardConfigDialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, DialogInterface.OnCancelListener, CompoundButton.OnCheckedChangeListener {
        private final static String TAG = "CrossKeyboardConfigDialog";
        private final static int DEFAULT_ALPHA_PROCESS = 40;
        private final static int DEFAULT_SIZE_PROGRESS = 0;
        private final static int MIN_SIZE_PROGRESS = -0;
        private final static int MAX_SIZE_PROGRESS = 150;
        private final static int MAX_ALPHA_PROGRESS = 100;
        private final static int MIN_ALPHA_PROGRESS = 0;
        private final static String spFileName = "input_crosskeyboard_config";
        private final static int spMode = Context.MODE_PRIVATE;
        private final static String sp_alpha_name = "alpha";
        private final static String sp_size_name = "size";
        private final static String sp_pos_x_name = "pos_x";
        private final static String sp_pos_y_name = "pos_y";
        private final static String sp_show_name = "show";
        private final OnscreenInput mInput;
        private final Context mContext;
        private AlertDialog dialog;
        private SeekBar seekbarAlpha;
        private SeekBar seekbarSize;
        private TextView textAlpha;
        private TextView textSize;
        private RadioButton rbtAll;
        private RadioButton rbtInGame;
        private RadioButton rbtOutGame;
        private int originalAlphaProgress;
        private int originalSizeProgress;
        private boolean originalBounceChecked;
        private int originalShow;
        private int originalMarginLeft;
        private int originalMarginTop;
        private int originalInputSize;
        private int screenWidth;
        private int screenHeight;

        public CrossKeyboardConfigDialog(@NonNull Context context, OnscreenInput i) {
            mInput = i;
            mContext = context;
            init();
        }

        private void init() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_onscreen_crosskeyboard_config, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
            builder.setView(view);
            builder.setCancelable(true);
            dialog = builder.create();
            dialog.setOnCancelListener(this);

            Button buttonOK = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_button_ok);
            Button buttonCancel = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_button_cancel);
            Button buttonRestore = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_button_restore);
            seekbarAlpha = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_seekbar_alpha);
            seekbarSize = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_seekbar_size);
            textAlpha = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_text_alpha);
            textSize = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_text_size);
            rbtAll = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_rbt_all);
            rbtInGame = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_rbt_in_game);
            rbtOutGame = view.findViewById(R.id.input_onscreen_crosskeyboard_dialog_rbt_out_game);

            buttonOK.setOnClickListener(this);
            buttonCancel.setOnClickListener(this);
            buttonRestore.setOnClickListener(this);
            seekbarAlpha.setOnSeekBarChangeListener(this);
            seekbarSize.setOnSeekBarChangeListener(this);
            rbtAll.setOnCheckedChangeListener(this);
            rbtInGame.setOnCheckedChangeListener(this);
            rbtOutGame.setOnCheckedChangeListener(this);

            originalInputSize = mInput.getSize()[0];
            screenWidth = mInput.getController().getConfig().getScreenWidth();
            screenHeight = mInput.getController().getConfig().getScreenHeight();

            //初始化控件属性
            seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
            seekbarSize.setMax(MAX_SIZE_PROGRESS);

            loadConfigFromFile();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.input_onscreen_crosskeyboard_dialog_button_ok) {
                dialog.dismiss();
            }
            if (v.getId() == R.id.input_onscreen_crosskeyboard_dialog_button_cancel) {
                dialog.cancel();
            }
            if (v.getId() == R.id.input_onscreen_crosskeyboard_dialog_button_restore) {
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
            if (seekBar == seekbarAlpha) {
                int p = progress - MIN_ALPHA_PROGRESS;
                String str = p + "%";
                textAlpha.setText(str);
                //调整透明度
                float alpha = 1 - p * 0.01f;
                ((CrossKeyboard) mInput).setAlpha(alpha);
            }
            if (seekBar == seekbarSize) {
                int p = progress - MIN_SIZE_PROGRESS;
                textSize.setText(String.valueOf(p));
                //调整大小
                int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
                int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
                int size = (int) ((1 + p * 0.01f) * originalInputSize);
                ((CrossKeyboard) mInput).setSize(size);
                //调整位置
                adjustPos(centerX, centerY);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (dialog == this.dialog) {
                seekbarAlpha.setProgress(originalAlphaProgress);
                seekbarSize.setProgress(originalSizeProgress);
                mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
                switch (originalShow) {
                    case CrossKeyboard.SHOW_ALL:
                        rbtAll.setChecked(true);
                        break;
                    case CrossKeyboard.SHOW_IN_GAME:
                        rbtInGame.setChecked(true);
                        break;
                    case CrossKeyboard.SHOW_OUT_GAME:
                        rbtOutGame.setChecked(true);
                        break;
                }
            }
        }

        public void show() {
            dialog.show();
            originalAlphaProgress = seekbarAlpha.getProgress();
            originalSizeProgress = seekbarSize.getProgress();
            originalMarginLeft = (int) mInput.getPos()[0];
            originalMarginTop = (int) mInput.getPos()[1];
            originalShow = ((CrossKeyboard) mInput).getShowStat();
        }

        public void onStop() {
            saveConfigToFile();
        }

        private void adjustPos(int originalCenterX, int originalCenterY) {
            int Width = mInput.getSize()[0];
            int viewHeight = mInput.getSize()[1];
            int marginLeft = originalCenterX - Width / 2;
            int margeinTop = originalCenterY - viewHeight / 2;

            //左边界检测
            if (marginLeft < 0) {
                marginLeft = 0;
            }
            //上边界检测
            if (margeinTop < 0) {
                margeinTop = 0;
            }
            //右边界检测
            if (marginLeft + Width > screenWidth) {
                marginLeft = screenWidth - Width;
            }
            //下边界检测
            if (margeinTop + viewHeight > screenHeight) {
                margeinTop = screenHeight - viewHeight;
            }

            mInput.setMargins(marginLeft, margeinTop, 0, 0);
        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarAlpha.setProgress(MAX_ALPHA_PROGRESS);
            seekbarSize.setProgress(MAX_SIZE_PROGRESS);
            //设定存储的数据
            seekbarAlpha.setProgress(sp.getInt(sp_alpha_name, DEFAULT_ALPHA_PROCESS));
            seekbarSize.setProgress(sp.getInt(sp_size_name, DEFAULT_SIZE_PROGRESS));
            mInput.setMargins(sp.getInt(sp_pos_x_name, 0), sp.getInt(sp_pos_y_name, 0), 0, 0);
            switch (sp.getInt(sp_show_name, CrossKeyboard.SHOW_ALL)) {
                case CrossKeyboard.SHOW_ALL:
                    rbtAll.setChecked(true);
                    break;
                case CrossKeyboard.SHOW_IN_GAME:
                    rbtInGame.setChecked(true);
                    break;
                case CrossKeyboard.SHOW_OUT_GAME:
                    rbtOutGame.setChecked(true);
                    break;
            }
        }

        public void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName, spMode).edit();
            editor.putInt(sp_alpha_name, seekbarAlpha.getProgress());
            editor.putInt(sp_size_name, seekbarSize.getProgress());
            editor.putInt(sp_pos_x_name, (int) mInput.getPos()[0]);
            editor.putInt(sp_pos_y_name, (int) mInput.getPos()[1]);
            editor.putInt(sp_show_name, ((CrossKeyboard) mInput).getShowStat());
            editor.apply();
        }

        private void restoreConfig() {
            seekbarAlpha.setProgress(DEFAULT_ALPHA_PROCESS);
            seekbarSize.setProgress(DEFAULT_SIZE_PROGRESS);
            rbtAll.setChecked(true);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (buttonView == rbtAll) {
                if (isChecked) {
                    ((CrossKeyboard) mInput).setShowStat(CrossKeyboard.SHOW_ALL);
                }
            }

            if (buttonView == rbtInGame) {
                if (isChecked) {
                    ((CrossKeyboard) mInput).setShowStat(CrossKeyboard.SHOW_IN_GAME);
                }
            }

            if (buttonView == rbtOutGame) {
                if (isChecked) {
                    ((CrossKeyboard) mInput).setShowStat(CrossKeyboard.SHOW_OUT_GAME);
                }
            }

        }

    }

}
