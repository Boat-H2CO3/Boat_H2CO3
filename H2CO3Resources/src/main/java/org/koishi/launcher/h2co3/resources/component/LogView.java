package org.koishi.launcher.h2co3.resources.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.core.widget.NestedScrollView;

import org.koishi.launcher.h2co3.core.login.utils.DisplayUtils;
import org.koishi.launcher.h2co3.resources.R;
import org.koishi.launcher.h2co3.resources.component.LineTextView;

public class LogView extends NestedScrollView {

    private static final int RADIUS_SIZE = 0;
    private static final int MAIN_COLOR = R.color.transparent;

    private final LineTextView mTextView;
    private final ScaleGestureDetector mScaleGestureDetector;
    private final Matrix mMatrix;
    private float mScaleFactor = 1f;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastRawX;
    private float mLastRawY;

    @SuppressLint("ClickableViewAccessibility")
    public LogView(@NonNull Context context) {
        super(context);
        this.setBackground(getViewBackground(context));

        mTextView = new LineTextView(context);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mTextView);

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        mMatrix = new Matrix();

        setFillViewport(true);
        setNestedScrollingEnabled(true);

        Resources resources = context.getResources();
        mTextView.setTextColor(resources.getColor(android.R.color.white));
        mTextView.setTextIsSelectable(true);
        mTextView.setTextSize(DisplayUtils.getPxFromSp(context, 3.2F));
        mTextView.setLineSpacing(0, 1f);

        setOnTouchListener((v, event) -> {
            mScaleGestureDetector.onTouchEvent(event);

            int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                    mLastRawX = event.getRawX();
                    mLastRawY = event.getRawY();
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final float x = event.getX();
                    final float y = event.getY();
                    final float rawX = event.getRawX();
                    final float rawY = event.getRawY();

                    final float dx = rawX - mLastRawX;
                    final float dy = rawY - mLastRawY;

                    mMatrix.postTranslate(dx, dy);

                    mLastTouchX = x;
                    mLastTouchY = y;
                    mLastRawX = rawX;
                    mLastRawY = rawY;

                    float[] matrixValues = new float[9];
                    mMatrix.getValues(matrixValues);
                    float translateX = matrixValues[Matrix.MTRANS_X];
                    float translateY = matrixValues[Matrix.MTRANS_Y];

                    mTextView.setTranslationX(translateX);
                    mTextView.setTranslationY(translateY);
                    break;
                }
            }

            return true;
        });
    }

    public void appendLog(String str) {
        this.post(() -> {
            if (mTextView != null) {
                mTextView.append(str);
                new Handler(Looper.getMainLooper()).postDelayed(() -> toBottom(this, mTextView), 50);
            }
        });
    }

    public void setLog(String str) {
        this.post(() -> {
            if (mTextView != null) {
                mTextView.setText(str);
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

    private Drawable getViewBackground(Context context) {
        float[] outerR = new float[]{RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE, RADIUS_SIZE};
        RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(rectShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(context.getResources().getColor(MAIN_COLOR));

        Drawable[] layers = new Drawable[]{shapeDrawable};

        return new LayerDrawable(layers);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            mMatrix.setScale(mScaleFactor, mScaleFactor);
            mTextView.setPivotX(0);
            mTextView.setPivotY(0);
            mTextView.setScaleX(mScaleFactor);
            mTextView.setScaleY(mScaleFactor);

            return true;
        }
    }
}