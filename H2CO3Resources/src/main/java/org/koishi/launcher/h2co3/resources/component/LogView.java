package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import org.koishi.launcher.h2co3.resources.R;

public class LogView extends NestedScrollView {

    private static final int RADIUS_SIZE = 0;
    private static final int MAIN_COLOR = R.color.transparent;

    private final LineTextView mTextView;

    public LogView(@NonNull Context context) {
        super(context);
        this.setBackground(getViewBackground(context));

        mTextView = new LineTextView(context);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mTextView);

        setFillViewport(true);
        setNestedScrollingEnabled(true);

        mTextView.setTextIsSelectable(true);
        mTextView.setLineSpacing(0, 1f);
    }

    public void appendLog(String str) {
        this.post(() -> {
            if (mTextView != null) {
                mTextView.append(str);
            }
        });
    }

    public void setLog(String str) {
        this.post(() -> {
            if (mTextView != null) {
                mTextView.setText(str);
                postDelayed(this::toBottom, 0);
            }
        });
    }

    private void toBottom() {
        scrollTo(0, mTextView.getHeight());
    }

    private Drawable getViewBackground(Context context) {
        float[] outerR = new float[]{RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE};
        RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(context.getResources().getColor(MAIN_COLOR, null));

        Drawable[] layers = new Drawable[]{shapeDrawable};
        return new LayerDrawable(layers);
    }
}