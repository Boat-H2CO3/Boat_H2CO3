package org.koishi.launcher.h2co3.resources.component.popwindow;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

import org.json.JSONException;

import java.io.IOException;

public class H2CO3PopupWindow extends PopupWindow {

    public final PopupController controller;

    public H2CO3PopupWindow(PopupController controller) {
        this.controller = controller;
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    private H2CO3PopupWindow(Context context) {
        controller = new PopupController(context, this);
    }

    /**
     * 获取宽
     */
    @Override
    public int getWidth() {
        return controller.mPopupView.getMeasuredWidth();
    }

    /**
     * 获取高
     */
    @Override
    public int getHeight() {
        return controller.mPopupView.getMeasuredHeight();
    }

    /**
     * 隐藏
     */
    @Override
    public void dismiss() {
        super.dismiss();
        controller.setBackGroundLevel(1.0f);
    }

    /**
     * 查看界面
     */
    public interface ViewInterface {
        void getChildView(View view, int layoutResId) throws IOException, JSONException;
    }

    /**
     * 使用建造者模式
     */
    public static class Builder {

        private final PopupController.PopupParams params;

        private ViewInterface listener;

        public Builder(Context context) {
            params = new PopupController.PopupParams(context);
        }

        /**
         * 设置PopupWindow
         *
         * @param layoutResId 布局ID
         * @return Builder
         */
        public Builder setView(int layoutResId) {
            params.mView = null;
            params.layoutResId = layoutResId;
            return this;
        }

        /**
         * 设置PopupWindow布局
         *
         * @param view View
         * @return Builder
         */
        public Builder setView(View view) {
            params.mView = view;
            params.layoutResId = 0;
            return this;
        }

        /**
         * 设置子View
         *
         * @param listener ViewInterface
         * @return Builder
         */
        public Builder setViewOnClickListener(ViewInterface listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 设置宽度和高度 如果不设置 默认是wrap_content
         *
         * @param width 宽
         * @return Builder
         */
        public Builder setWidthAndHeight(int width, int height) {
            params.mWidth = width;
            params.mHeight = height;
            return this;
        }

        /**
         * 设置背景灰色程度
         *
         * @param level 取值范围:0.0f-1.0f 值越小越暗
         * @return Builder
         */
        public Builder setBackGroundLevel(float level) {
            params.isShowBg = true;
            params.bg_level = level;
            return this;
        }

        /**
         * 是否可点击Outside消失
         *
         * @param touchable 是否可点击
         * @return Builder
         */
        public Builder setOutsideTouchable(boolean touchable) {
            params.isTouchable = touchable;
            return this;
        }

        /**
         * 设置动画
         *
         * @return Builder
         */
        public Builder setAnimationStyle(int animationStyle) {
            params.isShowAnim = true;
            params.animationStyle = animationStyle;
            return this;
        }

        public H2CO3PopupWindow builder() throws IOException, JSONException {
            final H2CO3PopupWindow popupWindow = new H2CO3PopupWindow(params.mContext);
            params.apply(popupWindow.controller);
            if (listener != null && params.layoutResId != 0) {
                listener.getChildView(popupWindow.controller.mPopupView, params.layoutResId);
            }
            return popupWindow;
        }

    }
}