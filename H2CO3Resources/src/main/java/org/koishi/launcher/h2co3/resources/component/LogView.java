package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;

public class LogView extends NestedScrollView {

    private static final int RADIUS_SIZE = 0;
    private static final int MAIN_COLOR = Color.parseColor("#7f5B5B5B");

    public final TextView mTextView;

    public LogView(@NonNull Context context) {
        super(context);
        this.setBackground(getViewBackground());

        mTextView = new LineTextView(context);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mTextView);

        Resources resources = context.getResources();
        mTextView.setTextColor(resources.getColor(android.R.color.white));
        mTextView.setTextIsSelectable(true);
        mTextView.setTextSize(DisplayUtils.getPxFromSp(context, 3.2F));
        mTextView.setLineSpacing(0, 1f);
    }

    public void appendLog(String str) {
        this.post(() -> {
            if (mTextView != null) {
                mTextView.append(str);
                new Handler(Looper.getMainLooper()).postDelayed(() -> toBottom(this, mTextView), 50);
            }
        });
    }

    private void toBottom(final NestedScrollView scrollView, final View view) {
        int offset = view.getHeight() - scrollView.getHeight();
        if (offset < 0) {
            offset = 0;
        }
        scrollView.scrollTo(0, offset);
    }

    private Drawable getViewBackground() {
        float[] outerR = new float[]{RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE};
        RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(rectShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(MAIN_COLOR);

        Drawable[] layers = new Drawable[]{shapeDrawable};

        return new LayerDrawable(layers);
    }
}