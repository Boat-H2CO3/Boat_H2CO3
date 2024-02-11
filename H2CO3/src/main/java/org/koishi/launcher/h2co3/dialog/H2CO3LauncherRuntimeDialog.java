package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.game.H2CO3GameHelper;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.dialog.H2CO3CustomViewDialog;

public class H2CO3LauncherRuntimeDialog extends H2CO3CustomViewDialog implements View.OnClickListener {

    private final Context context;

    private H2CO3CardView[] buttons;
    private String[] javaPaths;

    public H2CO3LauncherRuntimeDialog(Context context) {
        super(context);
        this.context = context;
        setTitle(org.koishi.launcher.h2co3.resources.R.string.title_runtime);
        this.setCustomView(R.layout.custom_dialog_runtime);
        initViews();
    }

    public void initViews() {
        buttons = new H2CO3CardView[4];
        buttons[0] = findViewById(R.id.button_jre_8);
        buttons[1] = findViewById(R.id.button_jre_11);
        buttons[2] = findViewById(R.id.button_jre_17);
        buttons[3] = findViewById(R.id.button_jre_21);

        javaPaths = new String[4];
        javaPaths[0] = H2CO3Tools.JAVA_8_PATH;
        javaPaths[1] = H2CO3Tools.JAVA_11_PATH;
        javaPaths[2] = H2CO3Tools.JAVA_17_PATH;
        javaPaths[3] = H2CO3Tools.JAVA_21_PATH;

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(this);
            updateButtonStrokeWidth(i);
        }
    }

    private void updateButtonStrokeWidth(int index) {
        buttons[index].setStrokeWidth(H2CO3GameHelper.getJavaPath().equals(javaPaths[index]) ? 13 : 0);
    }

    @Override
    public AlertDialog show() {
        return super.show();
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < buttons.length; i++) {
            if (v == buttons[i]) {
                H2CO3GameHelper.setJavaPath(javaPaths[i]);
                updateButtonStrokeWidth(i);
            } else {
                buttons[i].setStrokeWidth(0);
            }
        }
    }
}