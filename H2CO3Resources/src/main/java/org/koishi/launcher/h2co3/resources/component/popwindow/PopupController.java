package org.koishi.launcher.h2co3.resources.component.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 自定义弹出控制器
 *
 * @author iwen大大怪
 * @create 2021/11/09 0:08
 */
public class PopupController {

    /**
     * 上下文
     */
    private final Context context;
    /**
     * 弹窗布局View
     */
    View mPopupView;
    /**
     * 布局id
     */
    private int layoutResId;
    /**
     * PopupWindow
     */
    private PopupWindow popupWindow;
    /**
     * View
     */
    private View mView;

    /**
     * Window
     */
    private Window mWindow;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public PopupController(Context context) {
        this.context = context;
    }

    /**
     * 构造方法
     *
     * @param context     上下文
     * @param popupWindow PopupWindow
     */
    public PopupController(Context context, PopupWindow popupWindow) {
        this.context = context;
        this.popupWindow = popupWindow;
    }

    /**
     * 设置布局
     *
     * @param layoutResId 布局id
     */
    public void setView(int layoutResId) {
        mView = null;
        this.layoutResId = layoutResId;
        installContent();
    }

    /**
     * 设置布局
     *
     * @param view View
     */
    public void setView(View view) {
        mView = view;
        this.layoutResId = 0;
        installContent();
    }

    /**
     * 设置自定义视图
     */
    private void installContent() {
        if (layoutResId != 0) {
            mPopupView = LayoutInflater.from(context).inflate(layoutResId, null);
        } else if (mView != null) {
            mPopupView = mView;
        }
        popupWindow.setContentView(mPopupView);
    }

    /**
     * 设置宽度
     *
     * @param width  宽
     * @param height 高
     */
    private void setWidthAndHeight(int width, int height) {
        if (width == 0 || height == 0) {
            // 如果没设置宽高，默认是WRAP_CONTENT
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            popupWindow.setWidth(width);
            popupWindow.setHeight(height);
        }
    }

    /**
     * 设置背景灰色程度
     *
     * @param level 0.0f-1.0f
     */
    void setBackGroundLevel(float level) {
        mWindow = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = level;
        ((Activity) context).getWindow().setAttributes(lp);
    }


    /**
     * 设置动画
     */
    private void setAnimationStyle(int animationStyle) {
        popupWindow.setAnimationStyle(animationStyle);
    }

    /**
     * 设置Outside是否可点击
     *
     * @param touchable 是否可点击
     */
    private void setOutsideTouchable(boolean touchable) {
        // 设置透明背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置outside可点击
        popupWindow.setOutsideTouchable(touchable);
        popupWindow.setFocusable(touchable);
    }


    static class PopupParams {
        // 布局id
        public int layoutResId;
        // 上下文
        public Context mContext;
        // 弹窗的宽和高
        public int mWidth, mHeight;
        // 是否显示背景、动画
        public boolean isShowBg, isShowAnim;
        // 屏幕背景灰色程度
        public float bg_level;
        // 动画Id
        public int animationStyle;
        public View mView;
        public boolean isTouchable = true;

        public PopupParams(Context mContext) {
            this.mContext = mContext;
        }

        public void apply(PopupController controller) {
            if (mView != null) {
                controller.setView(mView);
            } else if (layoutResId != 0) {
                controller.setView(layoutResId);
            } else {
                throw new IllegalArgumentException("PopupView's contentView is null");
            }
            controller.setWidthAndHeight(mWidth, mHeight);
            controller.setOutsideTouchable(isTouchable);//设置outside可点击
            if (isShowBg) {
                // 设置背景
                controller.setBackGroundLevel(bg_level);
            }
            if (isShowAnim) {
                // 设置动画
                controller.setAnimationStyle(animationStyle);
            }
        }
    }
}
