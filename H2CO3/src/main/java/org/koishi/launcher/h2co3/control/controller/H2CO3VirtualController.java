package org.koishi.launcher.h2co3.control.controller;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.TYPE_WORDS;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.control.codes.Translation;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.Input;
import org.koishi.launcher.h2co3.control.input.OnscreenInput;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;

import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;


public class H2CO3VirtualController extends BaseController implements View.OnClickListener, MaterialSwitch.OnCheckedChangeListener {

    private final Translation mTranslation;
    private final int screenWidth;
    private final int screenHeight;
    DragFloatActionButton dButton;
    private VirtualControllerSetting settingDialog;
    //绑定
    private HashMap<View, Input> bindingViews;

    public H2CO3VirtualController(H2CO3ControlClient h2CO3ControlClient, int transType) {
        super(h2CO3ControlClient, true);

        //初始化键值翻译器
        this.mTranslation = new Translation(transType);

        screenWidth = this.getConfig().getScreenWidth();
        screenHeight = this.getConfig().getScreenHeight();

        //初始化
        init();

    }

    @Override
    public void saveConfig() {
        super.saveConfig();
    }

    public void init() {
        //初始化Setting对话框
        settingDialog = new VirtualControllerSetting(context);

        //全部隐藏
        for (Input i : inputs) {
            i.setEnabled(false);
        }
        dButton = new DragFloatActionButton(context);
        dButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(context, 30), DisplayUtils.getPxFromDp(context, 30)));
        dButton.setBackground(ContextCompat.getDrawable(context, org.koishi.launcher.h2co3.resources.R.drawable.background_floatbutton));

        double x = H2CO3Tools.getH2CO3Value("controller_float_position_x", (double) screenWidth / 2, Double.class).doubleValue();
        double y = H2CO3Tools.getH2CO3Value("controller_float_position_y", (double) screenHeight / 2, Double.class).doubleValue();
        if (x != -1 && y != -1) {
            dButton.setX((float) x);
            dButton.setY((float) y);
        } else {
            // 如果没有保存的位置，则将其移动到屏幕中间
            dButton.setX((float) screenWidth / 2);
            dButton.setY((float) screenHeight / 2);
        }
        dButton.setTodo(new ArrangeRule() {
            @Override
            public void run() {
                settingDialog.show();
            }
        });
        h2CO3ControlClient.addContentView(dButton, dButton.getLayoutParams());

        //绑定
        bindViewWithInput();
    }

    public void bindViewWithInput() {
        //绑定Input对象与ImageButton和Switch
        bindingViews = new HashMap<>();
    }

    @Override
    public void sendKey(BaseKeyEvent e) {
        //日志输出
        toLog(e);
        //事件分配
        switch (e.getType()) {
            case KEYBOARD_BUTTON, MOUSE_BUTTON -> {
                String KeyName = e.getKeyName();
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    //Log.e(e.getTag(),"切分: " + str + " 总大小: " + strs.length );
                    sendKeyEvent(new BaseKeyEvent(e.getTag(), str, e.isPressed(), e.getType(), e.getPointer()));
                }
            }
            case MOUSE_POINTER, MOUSE_POINTER_INC, TYPE_WORDS -> sendKeyEvent(e);
            default -> {
            }
        }

    }

    private void toLog(BaseKeyEvent event) {
        String info = switch (event.getType()) {
            case KEYBOARD_BUTTON ->
                    "Type: " + event.getType() + " KeyName: " + event.getKeyName() + " Pressed: " + event.isPressed();
            case MOUSE_BUTTON ->
                    "Type: " + event.getType() + " MouseName " + event.getKeyName() + " Pressed: " + event.isPressed();
            case MOUSE_POINTER ->
                    "Type: " + event.getType() + " PointerX: " + event.getPointer()[0] + " PointerY: " + event.getPointer()[1];
            case TYPE_WORDS -> "Type: " + event.getType() + " Char: " + event.getChars();
            case MOUSE_POINTER_INC ->
                    "Type: " + event.getType() + " IncX: " + event.getPointer()[0] + " IncY: " + event.getPointer()[1];
            default -> "Unknown: " + event;
        };
        Timber.tag(event.getTag()).e(info);
    }

    //事件发送
    private void sendKeyEvent(BaseKeyEvent e) {
        switch (e.getType()) {
            case KEYBOARD_BUTTON:
                h2CO3ControlClient.setKey(mTranslation.trans(e.getKeyName()), e.isPressed());
                break;
            case MOUSE_BUTTON:
                h2CO3ControlClient.setMouseButton(mTranslation.trans(e.getKeyName()), e.isPressed());
                break;
            case MOUSE_POINTER:
                if (e.getPointer() != null) {
                    h2CO3ControlClient.setPointer(e.getPointer()[0], e.getPointer()[1]);
                }
                break;
            case TYPE_WORDS:
                typeWords(e.getChars());
                break;
            case MOUSE_POINTER_INC:
                if (e.getPointer() != null) {
                    h2CO3ControlClient.setPointerInc(e.getPointer()[0], e.getPointer()[1]);
                }
            default:
        }
    }

    @Override
    public void onClick(View v) {

        if (v instanceof ImageButton && bindingViews.containsKey(v)) {
            Objects.requireNonNull(bindingViews.get(v)).runConfigure();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // 保存位置
        dButton.savePosition();
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    private static class VirtualControllerSetting extends MaterialAlertDialogBuilder {
        public VirtualControllerSetting(@NonNull Context context) {
            super(context);
            setView(R.layout.dialog_controller_functions);
        }
    }

    private static class DragFloatActionButton extends LinearLayoutCompat implements View.OnTouchListener {

        private int parentHeight;
        private int parentWidth;

        private int lastX;
        private int lastY;

        private boolean isDrag;

        private ArrangeRule aRule;

        private float initialX;
        private float initialY;


        public DragFloatActionButton(Context context) {
            super(context);
            this.setOnTouchListener(this);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return false;
        }

        public void behave(MotionEvent event) {
            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    this.setAlpha(0.9f);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastX = rawX;
                    lastY = rawY;
                    initialX = getX();
                    initialY = getY();
                    if (getParent() != null) {
                        ViewGroup parent = (ViewGroup) getParent();
                        parentHeight = parent.getHeight();
                        parentWidth = parent.getWidth();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.setAlpha(0.9f);
                    int dx = rawX - lastX;
                    int dy = rawY - lastY;
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    if (distance > 2 && !isDrag) {
                        isDrag = true;
                    }
                    float x = getX() + dx;
                    float y = getY() + dy;
                    //检测是否到达边缘 左上右下
                    x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                    y = getY() < 0 ? 0 : getY() + getHeight() > parentHeight ? parentHeight - getHeight() : y;
                    setX(x);
                    setY(y);
                    lastX = rawX;
                    lastY = rawY;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isDrag) {
                        //恢复按压效果
                        setPressed(false);
                        savePosition();
                    } else {
                        //执行点击操作
                        startTodo();
                    }
                    break;
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == this) {
                this.behave(event);
                return true;
            }
            return false;
        }

        public void setTodo(ArrangeRule ar) {
            this.aRule = ar;
        }

        public void startTodo() {
            if (aRule != null) {
                aRule.run();
            }
        }

        public void savePosition() {
            H2CO3Tools.setH2CO3Value("controller_float_po sition_x", getX());
            H2CO3Tools.setH2CO3Value("controller_float_position_y", getY());
        }
    }

    private static class ArrangeRule {
        public void run() {
            // Override this method.
        }
    }
}