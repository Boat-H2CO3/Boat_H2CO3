package org.koishi.launcher.h2co3.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StyleRes;

import org.koishi.launcher.h2co3.resources.R;

/**
 * @since 1.6
 */
@SuppressWarnings("unused")
public class BasicDialog extends H2CO3Dialog {
    @Deprecated
    public static final String LONG_TYPE = "long";
    @Deprecated
    public static final String SHORT_TYPE = "short";

    private boolean isDeleteDialog;

    public BasicDialog() {
        twoButtons = true;
    }

    /**
     * @since 1.4
     */
    public BasicDialog Short(Context context, String Title) {
        return Short(context, Title, null);
    }

    /**
     * @since 1.0
     */
    public BasicDialog Short(Context context, String Title, String Btn1Txt, String Btn2Txt) {
        return Builder(context, Title, null, Btn1Txt, Btn2Txt);
    }

    /**
     * @since 1.0
     */
    public BasicDialog Short(Context context, String Title, String Btn2Txt) {
        return Short(context, Title, null, Btn2Txt);
    }

    /**
     * @since 1.4
     */
    public BasicDialog Long(Context context, String Title, String Message) {
        return Long(context, Title, Message, null);
    }

    /**
     * @since 1.0
     */
    public BasicDialog Long(Context context, String Title, String Text, String Btn1Txt, String Btn2Txt) {
        return Builder(context, Title, Text, Btn1Txt, Btn2Txt);
    }

    /**
     * @since 1.0
     */
    public BasicDialog Long(Context context, String Title, String Text, String Btn2Txt) {
        return Long(context, Title, Text, null, Btn2Txt);
    }

    /**
     * @since 1.0
     */
    public BasicDialog Delete(Context context, String Title) {
        return Builder(context, Title, null, null, null).deleteAttributes();
    }

    /**
     * @since 1.0
     */
    public BasicDialog Delete(Context context) {
        return Builder(context).deleteAttributes();
    }

    public BasicDialog Delete(Context context, int theme) {
        return Builder(context, theme).deleteAttributes();
    }

    public BasicDialog Delete(Context context, boolean useAppTheme) {
        return Builder(context, useAppTheme).deleteAttributes();
    }

    private BasicDialog deleteAttributes() {
        isDeleteDialog = true;
        return setTitle("Delete?")
                .setRightButtonColor(MATERIAL3_RED_BUTTON)
                .setRightButtonText("Delete");
    }

    /**
     * @deprecated Use {@link BasicDialog#Builder(Context)}
     */
    public BasicDialog DialogBuilder(Context context) {
        return Builder(context);
    }

    /**
     * @since 1.6
     * @deprecated Use {@link BasicDialog#Builder(Context, boolean)}
     */
    @Deprecated
    public BasicDialog DialogBuilder(Context context, boolean useAppTheme) {
        return Builder(context, useAppTheme);
    }

    /**
     * @deprecated Use {@link BasicDialog#Builder(Context, int)}
     */
    public BasicDialog DialogBuilder(Context context, int theme) {
        return Builder(context, theme);
    }

    /**
     * @since 1.3
     * @deprecated Dialog type is automatic. Use {@link BasicDialog#Builder(Context)}
     */
    public BasicDialog DialogBuilder(Context context, String dialogType) {
        return Builder(context);
    }

    /**
     * @since 1.0
     * @deprecated Use {@link BasicDialog#Builder(Context, String, String, String, String)}
     */
    public BasicDialog DialogBuilder(Context context, String Title, String Text, String Btn1Txt, String Btn2Txt) {
        return Builder(context, Title, Text, Btn1Txt, Btn2Txt);
    }

    public BasicDialog Builder(Context context) {
        super.Builder(context, R.layout.basic_dialog);
        return this;
    }

    public BasicDialog Builder(Context context, boolean useAppTheme) {
        super.Builder(context, R.layout.basic_dialog, useAppTheme);
        return this;
    }

    public BasicDialog Builder(Context context, @StyleRes int theme) {
        super.Builder(context, R.layout.basic_dialog, theme, false);
        return this;
    }

    public BasicDialog Builder(Context context, String Title, String Text, String Btn1Txt, String Btn2Txt) {
        super.Builder(context, R.layout.basic_dialog);
        TextView TitleTv = dialog.findViewById(R.id.titleText);
        if (Btn1Txt == null) {
            button1.setOnClickListener(v -> dialog.dismiss());
        } else button1.setText(Btn1Txt);
        if (Btn2Txt != null) {
            button2.setText(Btn2Txt);
        }
        if (Text != null) {
            setMessage(Text);
        }
        if (Title == null) {
            return this;
        }
        TitleTv.setText(Title);
        return this;
    }

    /**
     * Set a dialog type.
     *
     * @param dialogType type of a dialog. Supported types: {@link #SHORT_TYPE} and {@link #LONG_TYPE}
     * @return current class
     * @since 1.4
     * @deprecated Old method, not in use. Remove this.
     */
    @Deprecated
    public BasicDialog setDialogType(String dialogType) {
        //Nothing here!!
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public BasicDialog setOldTheme() {
        super.setOldTheme();
        if (isDeleteDialog)
            super.setRightButtonColor(RED_BUTTON);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog setTitle(String title) {
        super.setTitle(title);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog setMessage(String message) {
        super.setMessage(message);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setTitleAlignment(int textAlignment) {
        super.setTitleAlignment(textAlignment);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setMessageAlignment(int textAlignment) {
        super.setMessageAlignment(textAlignment);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setTextColor(int color) {
        super.setTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setTitleTextColor(int color) {
        super.setTitleTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setMessageTextColor(int color) {
        super.setMessageTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setLeftButtonText(String text) {
        super.setLeftButtonText(text);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setRightButtonText(String text) {
        super.setRightButtonText(text);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setButtonsTextColor(@ColorInt int color) {
        super.setButtonsTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setLeftButtonTextColor(@ColorInt int color) {
        super.setLeftButtonTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setRightButtonTextColor(@ColorInt int color) {
        super.setRightButtonTextColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setButtonsColor(@ColorInt int color) {
        super.setButtonsColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setLeftButtonColor(@ColorInt int color) {
        super.setLeftButtonColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.4
     */
    @Override
    public BasicDialog setRightButtonColor(@ColorInt int color) {
        super.setRightButtonColor(color);
        return this;
    }

    @Override
    public BasicDialog setButtonsColor(@ButtonColor String color) {
        super.setButtonsColor(color);
        return this;
    }

    @Override
    public BasicDialog setLeftButtonColor(@ButtonColor String color) {
        super.setLeftButtonColor(color);
        return this;
    }

    @Override
    public BasicDialog setRightButtonColor(@ButtonColor String color) {
        super.setRightButtonColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setDialogBackgroundColor(int color) {
        super.setDialogBackgroundColor(color);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog setDialogBackgroundResource(@DrawableRes int drawable) {
        super.setDialogBackgroundResource(drawable);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog setButtonsBackgroundResource(@DrawableRes int drawable) {
        super.setButtonsBackgroundResource(drawable);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog setLeftButtonBackgroundResource(@DrawableRes int drawable) {
        super.setLeftButtonBackgroundResource(drawable);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog setRightButtonBackgroundResource(@DrawableRes int drawable) {
        super.setRightButtonBackgroundResource(drawable);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0
     */
    @Override
    public BasicDialog onButtonClick(DialogButtonEvent dialogButtonEvent) {
        super.onButtonClick(dialogButtonEvent);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0
     */
    @Override
    public BasicDialog onButtonClick(DialogButtonEvents dialogButtonEvents) {
        super.onButtonClick(dialogButtonEvents);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.3
     */
    @Override
    public BasicDialog show() {
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
     * @since 1.4
     */
    @Override
    public BasicDialog setMaxDialogWidth(int maxDialogWidth) {
        super.setMaxDialogWidth(maxDialogWidth);
        return this;

    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog setOnTouchListener(View.OnTouchListener onTouchListener) {
        super.setOnTouchListener(onTouchListener);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6
     */
    @Override
    public BasicDialog swipeToDismiss(boolean isSwipeToDismiss) {
        super.swipeToDismiss(isSwipeToDismiss);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.5
     */
    @Override
    public BasicDialog setDialogAnimations(int styleRes) {
        super.setDialogAnimations(styleRes);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6.1
     */
    @Override
    public BasicDialog applyInsets(boolean applyInsets) {
        super.applyInsets(applyInsets);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.6.1
     */
    @Override
    public BasicDialog applyInsets(int insets) {
        super.applyInsets(insets);
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
}

