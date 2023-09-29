package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;

import org.koishi.launcher.h2co3.resources.R;

/**
 * @since 1.5
 */
@SuppressWarnings("unused")
public class CustomViewDialog extends H2CO3Dialog {

    private LinearLayout rootView;

    public CustomViewDialog() {

    }

    public CustomViewDialog Builder(Context context) {
        return Builder(context, false);
    }

    public CustomViewDialog Builder(Context context, @StyleRes int theme) {
        super.Builder(context, R.layout.custom_view_dialog, theme, false);
        rootView = dialog.findViewById(R.id.customViewRoot);
        onLeftButtonClick(dialog::dismiss);
        return this;
    }

    public CustomViewDialog Builder(Context context, boolean useAppTheme) {
        super.Builder(context, R.layout.custom_view_dialog, useAppTheme);
        rootView = dialog.findViewById(R.id.customViewRoot);
        onLeftButtonClick(dialog::dismiss);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setOldTheme() {
        super.setOldTheme();
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setTitle(String title) {
        super.setTitle(title);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setMessage(String message) {
        super.setMessage(message);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog setTitleAlignment(int textAlignment) {
        super.setTitleAlignment(textAlignment);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog setMessageAlignment(int textAlignment) {
        super.setMessageAlignment(textAlignment);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setTextColor(int color) {
        super.setTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog setTitleTextColor(int color) {
        super.setTitleTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog setMessageTextColor(int color) {
        super.setMessageTextColor(color);
        return this;
    }

    /**
     * Set a button text.
     *
     * @param text button text
     * @return current class
     * @since 1.5
     */
    public CustomViewDialog setButtonText(String text) {
        return setLeftButtonText(text);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setLeftButtonText(String text) {
        super.setLeftButtonText(text);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setRightButtonText(String text) {
        super.setRightButtonText(text);
        return this;
    }

    /**
     * Set button text color.
     *
     * @param color Color to use for tinting this drawable
     * @return current class
     * @since 1.5
     */
    public CustomViewDialog setButtonTextColor(@ColorInt int color) {
        return setLeftButtonTextColor(color);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setButtonsTextColor(@ColorInt int color) {
        super.setButtonsTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setLeftButtonTextColor(@ColorInt int color) {
        super.setLeftButtonTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setRightButtonTextColor(@ColorInt int color) {
        super.setRightButtonTextColor(color);
        return this;
    }

    /**
     * Set button color.
     *
     * @param color Color to use for tinting this drawable
     * @return current class
     * @since 1.5
     */
    public CustomViewDialog setButtonColor(@ColorInt int color) {
        return setLeftButtonColor(color);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setButtonsColor(@ColorInt int color) {
        super.setButtonsColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setLeftButtonColor(@ColorInt int color) {
        super.setLeftButtonColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setRightButtonColor(@ColorInt int color) {
        super.setRightButtonColor(color);
        return this;
    }

    @Override
    public CustomViewDialog setButtonColor(@ButtonColor String color) {
        super.setButtonColor(color);
        return this;
    }

    @Override
    public CustomViewDialog setButtonsColor(@ButtonColor String color) {
        super.setButtonsColor(color);
        return this;
    }

    @Override
    public CustomViewDialog setLeftButtonColor(@ButtonColor String color) {
        super.setLeftButtonColor(color);
        return this;
    }

    @Override
    public CustomViewDialog setRightButtonColor(@ButtonColor String color) {
        super.setRightButtonColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog setDialogBackgroundColor(int color) {
        super.setDialogBackgroundColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setDialogBackgroundResource(@DrawableRes int drawable) {
        super.setDialogBackgroundResource(drawable);
        return this;
    }

    /**
     * Set background resource for button.
     *
     * @param drawable resource id
     * @return current class
     * @since 1.5
     */
    public CustomViewDialog setButtonBackgroundResource(@DrawableRes int drawable) {
        return setLeftButtonBackgroundResource(drawable);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setButtonsBackgroundResource(@DrawableRes int drawable) {
        super.setButtonsBackgroundResource(drawable);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setLeftButtonBackgroundResource(@DrawableRes int drawable) {
        super.setLeftButtonBackgroundResource(drawable);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setRightButtonBackgroundResource(@DrawableRes int drawable) {
        super.setRightButtonBackgroundResource(drawable);
        return this;
    }

    /**
     * Set onClick listener for a button
     *
     * @param dialogButtonEvent dialog button event
     * @return current class
     * @since 1.5
     */
    @Override
    public CustomViewDialog onButtonClick(DialogButtonEvent dialogButtonEvent) {
        if (twoButtons)
            super.onButtonClick(dialogButtonEvent);
        else
            super.onLeftButtonClick(dialogButtonEvent);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog onButtonClick(DialogButtonEvents dialogButtonEvents) {
        super.onButtonClick(dialogButtonEvents);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog show() {
        super.show();
        return this;
    }

    @Override
    protected int setButtonsRootLayoutID() {
        return R.id.buttons;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setMaxDialogWidth(int maxDialogWidth) {
        super.setMaxDialogWidth(maxDialogWidth);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog setDialogAnimations(int styleRes) {
        super.setDialogAnimations(styleRes);
        return this;
    }

    @Override
    protected void setButtons() {
        setButton1(R.id.btn1);
        setButton2(R.id.btn2);
    }

    @Override
    public Button getLeftButton() {
        return super.getLeftButton();
    }

    @Override
    public Button getRightButton() {
        return super.getRightButton();
    }

    /**
     * @since 1.6
     */
    @Override
    public TextView getTitleTextView() {
        return super.getTitleTextView();
    }

    /**
     * @since 1.6
     */
    @Override
    public TextView getMessageTextView() {
        return super.getMessageTextView();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public CustomViewDialog dialogWithTwoButtons() {
        super.dialogWithTwoButtons();
        setButton2Visibility(View.VISIBLE);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog setOnTouchListener(View.OnTouchListener onTouchListener) {
        super.setOnTouchListener(onTouchListener);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public CustomViewDialog swipeToDismiss(boolean isSwipeToDismiss) {
        super.swipeToDismiss(isSwipeToDismiss);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6.1
     */
    @Override
    public CustomViewDialog applyInsets(boolean applyInsets) {
        super.applyInsets(applyInsets);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6.1
     */
    @Override
    public CustomViewDialog applyInsets(int insets) {
        super.applyInsets(insets);
        return this;
    }

    @Override
    public void setButton2Visibility(int visibility) {
        super.setButton2Visibility(visibility);
    }

    public CustomViewDialog addCustomView(View view) {
        rootView.addView(view);
        return this;
    }
}
