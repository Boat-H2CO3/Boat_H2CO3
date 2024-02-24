package org.koishi.launcher.h2co3.control.controller;

import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;
import static org.koishi.launcher.h2co3.control.definitions.id.key.KeyEvent.TYPE_WORDS;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.control.codes.Translation;
import org.koishi.launcher.h2co3.control.event.BaseKeyEvent;
import org.koishi.launcher.h2co3.control.input.Input;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.component.MenuView;

import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

public class H2CO3VirtualController extends BaseController implements View.OnClickListener, MaterialSwitch.OnCheckedChangeListener {

    private final Translation mTranslation;
    private final int screenWidth;
    private final int screenHeight;
    private MenuView dButton;
    private VirtualControllerSetting settingDialog;
    private HashMap<View, Input> bindingViews;

    public H2CO3VirtualController(H2CO3ControlClient h2CO3ControlClient, int transType) {
        super(h2CO3ControlClient, true);
        this.mTranslation = new Translation(transType);
        screenWidth = getConfig().getScreenWidth();
        screenHeight = getConfig().getScreenHeight();
        init();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
    }

    public void init() {
        settingDialog = new VirtualControllerSetting(context);
        for (Input i : inputs) {
            i.setEnabled(false);
        }
        dButton = new MenuView(context);
        dButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(context, 40), DisplayUtils.getPxFromDp(context, 40)));
        double x = 0.0;
        double y = 0.0;
        Object xObj = H2CO3Tools.getH2CO3Value("controller_float_position_x", screenWidth / 2.0, Object.class);
        Object yObj = H2CO3Tools.getH2CO3Value("controller_float_position_y", screenHeight / 2.0, Object.class);

        if (xObj instanceof Double) {
            x = (Double) xObj;
        } else if (xObj instanceof Integer) {
            x = ((Integer) xObj).doubleValue();
        }

        if (yObj instanceof Double) {
            y = (Double) yObj;
        } else if (yObj instanceof Integer) {
            y = ((Integer) yObj).doubleValue();
        }

        if (x != -1 && y != -1) {
            dButton.setX((float) x);
            dButton.setY((float) y);
        } else {
            dButton.setX(screenWidth / 2.0f);
            dButton.setY(screenHeight / 2.0f);
        }
        dButton.setTodo(this::showSettingDialog);
        h2CO3ControlClient.addContentView(dButton, dButton.getLayoutParams());
        bindViewWithInput();
    }

    public void bindViewWithInput() {
        bindingViews = new HashMap<>();
    }

    @Override
    public void sendKey(BaseKeyEvent e) {
        toLog(e);
        switch (e.getType()) {
            case KEYBOARD_BUTTON:
            case MOUSE_BUTTON:
                String KeyName = e.getKeyName();
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    Log.e(e.getTag(), "切分: " + str + " 总大小: " + strs.length);
                    sendKeyEvent(new BaseKeyEvent(e.getTag(), str, e.isPressed(), e.getType(), e.getPointer()));
                }
                break;
            case MOUSE_POINTER:
            case MOUSE_POINTER_INC:
            case TYPE_WORDS:
                sendKeyEvent(e);
                break;
            default:
                break;
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
                break;
            default:
                break;
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
        dButton.savePosition();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    private void showSettingDialog() {
        settingDialog.show();
    }

    private static class VirtualControllerSetting extends MaterialAlertDialogBuilder {
        public VirtualControllerSetting(@NonNull Context context) {
            super(context);
            setView(R.layout.dialog_controller_functions);
        }
    }
}