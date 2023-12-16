package org.koishi.launcher.h2co3.resources.component.dialog.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.resources.R;


public class TaskDialog extends MaterialAlertDialogBuilder {

    private final TextView textTotalTaskName;
    private final TextView textCurrentTaskName;

    public TaskDialog(@NonNull Context context, boolean cancelable) {
        super(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task, null);
        setView(dialogView);
        textTotalTaskName = dialogView.findViewById(R.id.dialog_task_text_total_task_name);
        textCurrentTaskName = dialogView.findViewById(R.id.dialog_task_text_current_task_name);
        setCancelable(cancelable);
    }

    public TaskDialog setTotalTaskName(String taskname) {
        textTotalTaskName.setText(taskname);
        return this;
    }

    public TaskDialog setCurrentTaskName(String taskname) {
        textCurrentTaskName.setText(taskname);
        return this;
    }

    public TextView getTextTotalTaskName() {
        return textTotalTaskName;
    }

    public TextView getTextCurrentTaskName() {
        return textCurrentTaskName;
    }
}

