package org.koishi.launcher.h2co3.control.ckb.achieve;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.FileObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.ckb.button.GameButton;
import org.koishi.launcher.h2co3.control.ckb.support.CustomizeKeyboardMaker;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.resources.component.dialog.DialogUtils;
import org.koishi.launcher.h2co3.resources.component.dialog.support.DialogSupports;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class CkbManagerDialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Dialog.OnCancelListener {

    private final static String TAG = "CkbConfigDialog";
    private final Context mContext;
    private final CkbManager mManager;
    private AlertDialog dialog;
    private RadioButton radioEditable;
    private RadioButton radioGame;
    private TextView textButtonSum;
    private Button buttonAdd;
    private Spinner spinnerSelected;
    private EditText editFileName;
    private Button buttonLoad;
    private Button buttonExport;
    private Button buttonOK;
    private Button buttonDel;
    private Button buttonClear;
    private Button buttonDefault;
    private KeyboardFileListener fileListener;
    private ArrayList<String> data;
    private Timer mTimer;

    public CkbManagerDialog(@NonNull Context context, CkbManager manager) {
        H2CO3Tools.loadPaths(context);
        this.mContext = context;
        this.mManager = manager;
        initUI();
    }

    private void initUI() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_customize_keyboard_config, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
        builder.setView(view);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.setOnCancelListener(this);
        radioEditable = view.findViewById(R.id.input_customize_keyboard_dialog_radio_editable);
        radioGame = view.findViewById(R.id.input_customize_keyboard_dialog_radio_ingame);
        textButtonSum = view.findViewById(R.id.input_customize_keyboard_dialog_text_button_sum);
        buttonAdd = view.findViewById(R.id.input_customize_keyboard_dialog_button_add);
        spinnerSelected = view.findViewById(R.id.input_customize_keyboard_dialog_spinner_select);
        editFileName = view.findViewById(R.id.input_customize_keyboard_dialog_edit_filename);
        buttonLoad = view.findViewById(R.id.input_customize_keyboard_dialog_button_load);
        buttonExport = view.findViewById(R.id.input_customize_keyboard_dialog_button_export);
        buttonOK = view.findViewById(R.id.input_customize_keyboard_dialog_button_ok);
        buttonDel = view.findViewById(R.id.input_customize_keyboard_dialog_button_delete);
        buttonClear = view.findViewById(R.id.input_customize_keyboard_dialog_button_clear);
        buttonDefault = view.findViewById(R.id.input_customize_keyboard_dialog_button_default);

        //设定监听
        for (View v : new View[]{buttonAdd, buttonLoad, buttonExport, buttonOK, buttonDel, buttonClear, buttonDefault}) {
            v.setOnClickListener(this);
        }
        for (RadioButton r : new RadioButton[]{radioGame, radioEditable}) {
            r.setOnCheckedChangeListener(this);
        }
        dialog.setOnCancelListener(this);

        //是否显示模式选项

        //当进入游戏的时候自动设定客制化键盘模式为生效，如果是编辑界面，则不自动设置
        radioGame.setChecked(mManager.getController() != null);

    }

    public void dismiss() {
        dialog.dismiss();
        //关闭目录监听
        fileListener.stopWatching();
        //关闭按键数量刷新
        setCountsRefresh(false);
    }

    public void show() {
        dialog.show();
        //启用目录变化监听
        fileListener = new KeyboardFileListener(this);
        fileListener.startWatching();
        updateUI();
        //启用按键数量刷新
        setCountsRefresh(true);
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    private void removeSelectedFile() {
        String filePath = H2CO3Tools.H2CO3_SETTING_DIR + "/" + spinnerSelected.getSelectedItem().toString();
        FileTools.deleteFile(new File(filePath));
    }

    private void loadSelectedFile() {
        String fileName = spinnerSelected.getSelectedItem().toString();
        if (!mManager.loadKeyboard(fileName)) {
            DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_error), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_failed_to_import_keyboard_layout), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), null);
        } else {
            Toast.makeText(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_successed_to_import_keyboard_layout), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonOK) {
            this.dismiss();
        }
        if (v == buttonAdd) {
            mManager.addGameButton(null);
        }
        if (v == buttonExport) {
            if (editFileName.getText() == null) {
                Toast.makeText(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_filename_can_not_be_void), Toast.LENGTH_SHORT).show();
                return;
            }
            if (editFileName.getText().toString().equals(CkbManager.LAST_KEYBOARD_LAYOUT_NAME)) {
                Toast.makeText(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_please_change_file_name), Toast.LENGTH_SHORT).show();
                return;
            }
            final String fn = editFileName.getText().toString();
            if (fn.equals("")) {
                Toast.makeText(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_filename_can_not_be_void), Toast.LENGTH_SHORT).show();
                return;
            }

            //检查文件是否存在重复，如果重复，提示是否覆盖。
            boolean run = true;
            for (String str : FileTools.listChildFilesFromTargetDir(H2CO3Tools.H2CO3_SETTING_DIR)) {
                if (str.equals(fn + ".json")) {
                    run = false;
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_filename_has_been_used), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_over_write), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            mManager.exportKeyboard(fn);
                        }
                    });
                }
            }
            if (run) {
                mManager.exportKeyboard(fn);
            }
        }

        if (v == buttonDel) {
            if (spinnerSelected.getSelectedItem() != null) {
                String str = spinnerSelected.getSelectedItem().toString();
                if (!str.equals("")) {
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), String.format(mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_are_you_sure_to_delete_file), str), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_delete), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            removeSelectedFile();
                        }
                    });
                }
            }
        }

        if (v == buttonLoad) {
            if (spinnerSelected.getSelectedItem() != null) {
                String str = spinnerSelected.getSelectedItem().toString();
                if (!str.equals("")) {
                    DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), String.format(mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_are_you_sure_to_import_keyboard_layout), str), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_import), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                        @Override
                        public void runWhenPositive() {
                            loadSelectedFile();
                        }
                    });
                }
            }
        }

        if (v == buttonClear) {
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.tips_are_you_sure_to_clear_all_buttons), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    mManager.clearKeyboard();
                }
            });
        }

        if (v == buttonDefault) {
            DialogUtils.createBothChoicesDialog(mContext, mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_warn), "您确定要使用默认键盘布局吗？", mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_ok), mContext.getString(org.koishi.launcher.h2co3.resources.R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    super.runWhenPositive();
                    mManager.loadKeyboard(new CustomizeKeyboardMaker(mContext).createDefaultKeyboard());
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == radioEditable) {
            if (isChecked) {
                mManager.setButtonMode(GameButton.MODE_MOVEABLE_EDITABLE);
                buttonAdd.setVisibility(View.VISIBLE);
            }
        }

        if (buttonView == radioGame) {
            if (isChecked) {
                mManager.setButtonMode(GameButton.MODE_GAME);
                buttonAdd.setVisibility(View.GONE);
            }
        }
    }

    public void updateUI() {
        if (data == null) {
            data = new ArrayList<>();
            data.addAll(FileTools.listChildFilesFromTargetDir(H2CO3Tools.H2CO3_SETTING_DIR));
            spinnerSelected.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, data));
        } else {
            data.clear();
            data.addAll(FileTools.listChildFilesFromTargetDir(H2CO3Tools.H2CO3_SETTING_DIR));
            ((BaseAdapter) spinnerSelected.getAdapter()).notifyDataSetChanged();
        }
    }

    public void setButtonCounts(final int counts) {
        this.textButtonSum.post(() -> textButtonSum.setText(String.valueOf(counts)));
    }

    public void setCountsRefresh(boolean able) {
        if (able) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setButtonCounts(mManager.getButtonCount());
                }
            }, 500, 500);
        } else {
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    }

    private static class KeyboardFileListener extends FileObserver {

        private final CkbManagerDialog mDialog;

        public KeyboardFileListener(CkbManagerDialog dialog) {
            super(H2CO3Tools.H2CO3_SETTING_DIR);
            this.mDialog = dialog;
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            switch (event) {
                case FileObserver.CREATE, FileObserver.DELETE -> mDialog.updateUI();
                default -> {
                }
            }
        }
    }
}
