package org.koishi.launcher.h2co3.resources.component.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;

import org.koishi.launcher.h2co3.core.view.ViewFinder;

public class H2CO3CustomViewDialog extends H2CO3MaterialDialog {
    private View customView;
    private ViewFinder viewFinder;

    public H2CO3CustomViewDialog(Context context) {
        super(context);
    }

    /**
     * 获取自定义视图
     *
     * @return 自定义视图
     */
    public View getCustomView() {
        return customView;
    }

    /**
     * 设置自定义视图
     *
     * @param layoutResId 自定义视图的布局资源ID
     */
    public void setCustomView(int layoutResId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        customView = inflater.inflate(layoutResId, null);

        NestedScrollView customViewContainer = new NestedScrollView(getContext());
        customViewContainer.addView(customView);

        setView(customViewContainer);
    }

    @NonNull
    @Override
    public AlertDialog create() {
        if (customView != null) {
            viewFinder = new ViewFinder(customView);
        }
        AlertDialog dialog = super.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return dialog;
    }

    /**
     * 根据ID查找视图
     *
     * @param viewId 视图ID
     * @param <T>    视图类型
     * @return 查找到的视图，如果未找到则返回null
     */
    public <T extends View> T findViewById(int viewId) {
        if (viewFinder != null) {
            return viewFinder.findViewById(viewId);
        }
        return null;
    }
}