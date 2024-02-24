package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.resources.R;

public class MenuView extends View {

    private static final int STROKE_WIDTH = 2;
    private static final int ICON_SIZE_RATIO = 2;
    private static final int CLICK_DISTANCE_THRESHOLD = 10;
    private static final long CLICK_TIME_THRESHOLD = 400;
    private static final String POSITION_X_KEY = "controller_float_position_x";
    private static final String POSITION_Y_KEY = "controller_float_position_y";

    private boolean pressed = false;
    private Bitmap icon;

    private Paint strokePaint;
    private Paint areaPaint;
    private Paint iconPaint;

    private Rect srcRect;
    private Rect destRect;

    private float downX;
    private float downY;
    private long downTime;

    private int parentHeight;
    private int parentWidth;

    private int lastX;
    private int lastY;

    private boolean isDrag;

    private Runnable todo;

    private float initialX;
    private float initialY;

    public MenuView(Context context) {
        super(context);
        init();
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.DKGRAY);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(STROKE_WIDTH);

        areaPaint = new Paint();
        areaPaint.setAntiAlias(true);

        iconPaint = new Paint();
        iconPaint.setAntiAlias(true);

        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_boat);

        srcRect = new Rect(0, 0, icon.getWidth(), icon.getHeight());
        destRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMenuView(canvas);
    }

    private void drawMenuView(Canvas canvas) {
        if (pressed) {
            areaPaint.setColor(Color.GRAY);
        } else {
            areaPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, (getMeasuredWidth() >> 1) - 1, strokePaint);
        canvas.drawCircle(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1, (getMeasuredWidth() >> 1) - 2, areaPaint);

        int iconSize = (int) Math.min(getMeasuredWidth(), getMeasuredHeight() / ICON_SIZE_RATIO);
        int iconLeft = (getMeasuredWidth() - iconSize) / 2;
        int iconTop = (getMeasuredHeight() - iconSize) / 2;
        int iconRight = iconLeft + iconSize;
        int iconBottom = iconTop + iconSize;

        destRect.set(iconLeft, iconTop, iconRight, iconBottom);

        canvas.drawBitmap(icon, srcRect, destRect, iconPaint);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
        }
        handleTouchEvent(event);
        return true;
    }

    private void handleTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(rawX, rawY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handleActionUp(event);
                break;
        }
    }

    private void handleActionDown(MotionEvent event) {
        downX = event.getX();
        downY = event.getY();
        downTime = System.currentTimeMillis();
        pressed = true;
        invalidate();
        isDrag = false;
        this.setAlpha(0.9f);
        getParent().requestDisallowInterceptTouchEvent(true);
        lastX = (int) event.getRawX();
        lastY = (int) event.getRawY();
        initialX = getX();
        initialY = getY();
        if (getParent() != null) {
            ViewGroup parent = (ViewGroup) getParent();
            parentHeight = parent.getHeight();
            parentWidth = parent.getWidth();
        }
    }

    private void handleActionMove(int rawX, int rawY) {
        this.setAlpha(0.9f);
        int dx = rawX - lastX;
        int dy = rawY - lastY;
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        if (distance > 2 && !isDrag) {
            isDrag = true;
        }
        float x = getX() + dx;
        float y = getY() + dy;
        x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
        y = y < 0 ? 0 : y + getHeight() > parentHeight ? parentHeight - getHeight() : y;
        setX(x);
        setY(y);
        lastX = rawX;
        lastY = rawY;
    }

    private void handleActionUp(MotionEvent event) {
        if (Math.abs(event.getX() - downX) <= CLICK_DISTANCE_THRESHOLD
                && Math.abs(event.getY() - downY) <= CLICK_DISTANCE_THRESHOLD
                && System.currentTimeMillis() - downTime <= CLICK_TIME_THRESHOLD) {
            handleClickEvent(event);
        }
        pressed = false;
        invalidate();

        savePosition();
    }

    private void handleClickEvent(MotionEvent event) {
        if (todo != null) {
            todo.run();
        }
    }

    public void setTodo(Runnable todo) {
        this.todo = todo;
    }

    public void savePosition() {
        H2CO3Tools.setH2CO3Value(POSITION_X_KEY, getX());
        H2CO3Tools.setH2CO3Value(POSITION_Y_KEY, getY());
    }
}