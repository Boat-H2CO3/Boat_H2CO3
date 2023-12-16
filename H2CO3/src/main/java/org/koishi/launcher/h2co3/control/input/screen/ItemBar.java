package org.koishi.launcher.h2co3.control.input.screen;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.controller.Controller;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.OnscreenInput;
import org.koishi.launcher.h2co3.control.input.screen.button.BaseButton;
import org.koishi.launcher.h2co3.control.input.screen.button.ItemButton;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

public class ItemBar implements OnscreenInput, View.OnTouchListener {

    private final static String TAG = "ItemBar";
    private final static int TYPE = KEYBOARD_BUTTON;
    private final static int DEFAULT_ALPHA_PROGRESS = 60;
    private final static int DEFAULT_SIZE_PROGRESS = 50;
    private final static int DEFAULT_MOVE_DISTANCE = 5;
    private final static int MAX_ALPHA_PROGRESS = 100;
    private final static int MIN_ALPHA_PROGRESS = 0;
    private final static int MAX_SIZE_PROGRESS = 100;
    private final static int MIN_SIZE_PROGRESS = -50;
    private final static int MARK_MOVE_UP = 1;
    private final static int MARK_MOVE_DOWN = 2;
    private final static int MARK_MOVE_LEFT = 3;
    private final static int MARK_MOVE_RIGHT = 4;
    private final static String SP_FILE_NAME = "input_itembar_config";
    private final static int SP_MODE = Context.MODE_PRIVATE;
    private final static String SP_ALPHA_NAME = "alpha";
    private final static String SP_SIZE_NAME = "size";
    private final static String SP_POS_X_NAME = "pos_x";
    private final static String SP_POS_Y_NAME = "pos_y";

    private Controller mController;
    private LinearLayout itemBar;
    private boolean enable;
    private ItembarConfigDialog configDialog;
    private int posX;
    private int posY;

    private ItemButton[] itemButtons;

    @SuppressLint("InflateParams")
    @Override
    public boolean load(Context context, Controller controller) {
        this.mController = controller;
        int screenWidth = mController.getConfig().getScreenWidth();
        int screenHeight = mController.getConfig().getScreenHeight();
        itemBar = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.virtual_itembar, null);
        mController.addContentView(itemBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        itemButtons = new ItemButton[9];
        itemButtons[0] = itemBar.findViewById(R.id.itembutton_1);
        itemButtons[1] = itemBar.findViewById(R.id.itembutton_2);
        itemButtons[2] = itemBar.findViewById(R.id.itembutton_3);
        itemButtons[3] = itemBar.findViewById(R.id.itembutton_4);
        itemButtons[4] = itemBar.findViewById(R.id.itembutton_5);
        itemButtons[5] = itemBar.findViewById(R.id.itembutton_6);
        itemButtons[6] = itemBar.findViewById(R.id.itembutton_7);
        itemButtons[7] = itemBar.findViewById(R.id.itembutton_8);
        itemButtons[8] = itemBar.findViewById(R.id.itembutton_9);

        for (View v : itemButtons) {
            v.setOnTouchListener(this);
        }

        int scale = calculateScale(screenWidth, screenHeight);
        ViewGroup.LayoutParams lp = itemBar.getLayoutParams();
        lp.height = 20 * scale;
        lp.width = 20 * scale * 9;
        itemBar.setLayoutParams(lp);

        setUiVisibility(View.INVISIBLE);

        configDialog = new ItembarConfigDialog(context, this);

        return true;
    }

    private int calculateScale(int screenWidth, int screenHeight) {
        int scale = 1;
        while (screenWidth / (scale + 1) >= 320 && screenHeight / (scale + 1) >= 240) {
            scale++;
        }
        return scale;
    }

    @Override
    public void setUiMoveable(boolean moveable) {
        // do nothing
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enable = enabled;
        updateUI();
    }

    @Override
    public void setUiVisibility(int visibility) {
        switch (visibility) {
            case View.VISIBLE -> {
                enable = true;
                if (mController.isGrabbed()) {
                    itemBar.setVisibility(visibility);
                }
            }
            case View.GONE, View.INVISIBLE -> {
                itemBar.setVisibility(visibility);
                enable = false;
            }
            default -> {
            }
        }
    }

    @Override
    public float[] getPos() {
        return new float[]{posX, posY};
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = itemBar.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        itemBar.setLayoutParams(p);
        this.posX = left;
        this.posY = top;
    }

    @Override
    public int[] getSize() {
        return new int[]{itemBar.getLayoutParams().width, itemBar.getLayoutParams().height};
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        if (isGrabbed) {
            if (enable) {
                itemBar.setVisibility(View.VISIBLE);
            }
        } else {
            itemBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void runConfigure() {
        configDialog.show();
    }

    @Override
    public void saveConfig() {
        configDialog.saveConfigToFile();
    }

    private void updateUI() {
        if (enable) {
            setUiVisibility(View.VISIBLE);
        } else {
            setUiVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ItemButton) {
            sendKeyEvent(v, event);
        }
        return false;
    }

    @Override
    public boolean unload() {
        itemBar.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) itemBar.getParent();
        vg.removeView(itemBar);
        return true;
    }

    @Override
    public View[] getViews() {
        return new View[]{this.itemBar};
    }

    private void sendKeyEvent(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mController.sendKey(new BaseKeyEvent(TAG, ((BaseButton) v).getButtonName(), true, TYPE, null));
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mController.sendKey(new BaseKeyEvent(TAG, ((BaseButton) v).getButtonName(), false, TYPE, null));
        }
    }

    public void setAlpha(float a) {
        itemBar.setAlpha(a);
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams p = itemBar.getLayoutParams();
        p.width = width;
        p.height = height;
        itemBar.requestLayout();
        itemBar.invalidate();
    }

    public int getUiVisiability() {
        return itemBar.getVisibility();
    }

    @Override
    public void onPaused() {
        // do nothing
    }

    @Override
    public void onResumed() {
        // do nothing
    }

    @Override
    public Controller getController() {
        return this.mController;
    }

    private static class ItembarConfigDialog implements View.OnClickListener, DialogInterface.OnCancelListener, SeekBar.OnSeekBarChangeListener {

        private final Context mContext;
        private final OnscreenInput mInput;
        private AlertDialog dialog;
        private Button buttonOK;
        private Button buttonCancel;
        private Button buttonRestore;
        private Button buttonMoveLeft;
        private Button buttonMoveRight;
        private Button buttonMoveUp;
        private Button buttonMoveDown;
        private SeekBar seekbarAlpha;
        private SeekBar seekbarSize;
        private TextView textAlpha;
        private TextView textSize;
        private int originalAlphaProgress;
        private int originalSizeProgress;
        private int originalMarginLeft;
        private int originalMarginTop;
        private int originalInputWidth;
        private int originalInputHeight;
        private int screenWidth;
        private int screenHeight;

        public ItembarConfigDialog(@NonNull Context context, OnscreenInput input) {
            mContext = context;
            mInput = input;
            init();
        }

        private void init() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_itembar_config, null);
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
            builder.setView(view);
            builder.setCancelable(true);
            dialog = builder.create();
            dialog.setOnCancelListener(this);

            buttonOK = view.findViewById(R.id.input_itembar_dialog_button_ok);
            buttonCancel = view.findViewById(R.id.input_itembar_dialog_button_cancel);
            buttonRestore = view.findViewById(R.id.input_itembar_dialog_button_restore);
            buttonMoveLeft = view.findViewById(R.id.input_itembar_dialog_button_move_left);
            buttonMoveRight = view.findViewById(R.id.input_itembar_dialog_button_move_right);
            buttonMoveUp = view.findViewById(R.id.input_itembar_dialog_button_move_up);
            buttonMoveDown = view.findViewById(R.id.input_itembar_dialog_button_move_down);
            seekbarAlpha = view.findViewById(R.id.input_itembar_dialog_seekbar_alpha);
            seekbarSize = view.findViewById(R.id.input_itembar_dialog_seekbar_size);
            textAlpha = view.findViewById(R.id.input_itembar_dialog_text_alpha);
            textSize = view.findViewById(R.id.input_itembar_dialog_text_size);

            buttonOK.setOnClickListener(this);
            buttonCancel.setOnClickListener(this);
            buttonRestore.setOnClickListener(this);
            buttonMoveUp.setOnClickListener(this);
            buttonMoveDown.setOnClickListener(this);
            buttonMoveLeft.setOnClickListener(this);
            buttonMoveRight.setOnClickListener(this);
            seekbarAlpha.setOnSeekBarChangeListener(this);
            seekbarSize.setOnSeekBarChangeListener(this);

            originalInputWidth = mInput.getSize()[0];
            originalInputHeight = mInput.getSize()[1];
            screenWidth = mInput.getController().getConfig().getScreenWidth();
            screenHeight = mInput.getController().getConfig().getScreenHeight();

            seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
            seekbarSize.setMax(MAX_SIZE_PROGRESS);

            loadConfigFromFile();
        }

        public void show() {
            dialog.show();
            originalAlphaProgress = seekbarAlpha.getProgress();
            originalSizeProgress = seekbarSize.getProgress();
            originalMarginLeft = (int) mInput.getPos()[0];
            originalMarginTop = (int) mInput.getPos()[1];
        }

        @Override
        public void onClick(View v) {
            if (v == buttonCancel) {
                dialog.cancel();
            } else if (v == buttonOK) {
                dialog.dismiss();
            } else if (v == buttonRestore) {
                DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_are_you_sure_to_restore_setting), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        restoreConfig();
                    }
                });
            } else if (v == buttonMoveUp) {
                moveItembarByButton(MARK_MOVE_UP);
            } else if (v == buttonMoveDown) {
                moveItembarByButton(MARK_MOVE_DOWN);
            } else if (v == buttonMoveLeft) {
                moveItembarByButton(MARK_MOVE_LEFT);
            } else if (v == buttonMoveRight) {
                moveItembarByButton(MARK_MOVE_RIGHT);
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (dialog == this) {
                seekbarAlpha.setProgress(originalAlphaProgress);
                seekbarSize.setProgress(originalSizeProgress);
                mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
            }
        }

        private void restoreConfig() {
            seekbarAlpha.setProgress(DEFAULT_ALPHA_PROGRESS);
            seekbarSize.setProgress(DEFAULT_SIZE_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekbarAlpha) {
                int p = progress + MIN_ALPHA_PROGRESS;
                String str = p + "%";
                textAlpha.setText(str);
                float alpha = 1 - p * 0.01f;
                ((ItemBar) mInput).setAlpha(alpha);
            } else if (seekBar == seekbarSize) {
                int p = progress + MIN_SIZE_PROGRESS;
                textSize.setText(String.valueOf(p));
                int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
                int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
                int tmpWidth = (int) ((1 + p * 0.01f) * originalInputWidth);
                int tmpHeight = (int) ((1 + p * 0.01f) * originalInputHeight);
                ((ItemBar) mInput).setSize(tmpWidth, tmpHeight);
                adjustPos(centerX, centerY);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public void onStop() {
            saveConfigToFile();
        }

        private void adjustPos(int originalCenterX, int originalCenterY) {
            int viewWidth = mInput.getSize()[0];
            int viewHeight = mInput.getSize()[1];
            int marginLeft = originalCenterX - viewWidth / 2;
            int marginTop = originalCenterY - viewHeight / 2;

            if (marginTop < 0) {
                marginTop = 0;
            }
            if (marginTop + viewHeight > screenHeight) {
                marginTop = screenHeight - viewHeight;
            }
            if (marginLeft < 0) {
                marginLeft = 0;
            }
            if (marginLeft + viewWidth > screenWidth) {
                marginLeft = screenWidth - viewWidth;
            }

            mInput.setMargins(marginLeft, marginTop, 0, 0);
        }

        private void moveItembarByButton(int mark) {
            float posX = mInput.getPos()[0];
            float posY = mInput.getPos()[1];

            int marginLeft = (int) posX;
            int marginTop = (int) posY;

            int viewWidth = mInput.getSize()[0];
            int viewHeight = mInput.getSize()[1];

            switch (mark) {
                case MARK_MOVE_UP -> marginTop -= DEFAULT_MOVE_DISTANCE;
                case MARK_MOVE_DOWN -> marginTop += DEFAULT_MOVE_DISTANCE;
                case MARK_MOVE_LEFT -> marginLeft -= DEFAULT_MOVE_DISTANCE;
                case MARK_MOVE_RIGHT -> marginLeft += DEFAULT_MOVE_DISTANCE;
                default -> {
                }
            }

            if (marginTop < 0) {
                marginTop = 0;
            }
            if (marginTop + viewHeight > screenHeight) {
                marginTop = screenHeight - viewHeight;
            }
            if (marginLeft < 0) {
                marginLeft = 0;
            }
            if (marginLeft + viewWidth > screenWidth) {
                marginLeft = screenWidth - viewWidth;
            }

            mInput.setMargins(marginLeft, marginTop, 0, 0);
        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(SP_FILE_NAME, SP_MODE);
            seekbarAlpha.setProgress(sp.getInt(SP_ALPHA_NAME, DEFAULT_ALPHA_PROGRESS));
            seekbarSize.setProgress(sp.getInt(SP_SIZE_NAME, DEFAULT_SIZE_PROGRESS));
            mInput.setMargins(sp.getInt(SP_POS_X_NAME, 0), sp.getInt(SP_POS_Y_NAME, 0), 0, 0);
        }

        public void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(SP_FILE_NAME, SP_MODE).edit();
            editor.putInt(SP_ALPHA_NAME, seekbarAlpha.getProgress());
            editor.putInt(SP_SIZE_NAME, seekbarSize.getProgress());
            editor.putInt(SP_POS_X_NAME, (int) mInput.getPos()[0]);
            editor.putInt(SP_POS_Y_NAME, (int) mInput.getPos()[1]);
            editor.apply();
        }

    }

}