package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class ControlLayout extends TextureView implements TextureView.SurfaceTextureListener, Runnable {
    private static final int CANVAS_WIDTH = 720;
    private static final int CANVAS_HEIGHT = 600;
    private static final int MAX_SIZE = 100;
    private static final double NANOS = 1000000000.0;
    private final LinkedList<Long> mTimes = new LinkedList<>();
    private final Paint mFpsPaint;
    private boolean mIsDestroyed = false;

    public ControlLayout(Context context) {
        this(context, null);
    }

    public ControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mFpsPaint = new Paint();
        mFpsPaint.setColor(Color.WHITE);
        mFpsPaint.setTextSize(20);

        setSurfaceTextureListener(this);

        post(this::refreshSize);
    }

    @Override
    public void onSurfaceTextureAvailable(@NotNull SurfaceTexture surfaceTexture, int width, int height) {
        getSurfaceTexture().setDefaultBufferSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        mIsDestroyed = false;
        new Thread(this, "MyCanvasRenderer").start();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NotNull SurfaceTexture surfaceTexture) {
        mIsDestroyed = true;
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        getSurfaceTexture().setDefaultBufferSize(CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        getSurfaceTexture().setDefaultBufferSize(CANVAS_WIDTH, CANVAS_HEIGHT);
    }

    @Override
    public void run() {
        try {
            while (!mIsDestroyed && getSurfaceTexture() != null) {
                Canvas canvas = lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawText("FPS: " + (Math.round(fps() * 10) / 10), 0, 20, mFpsPaint);
                    unlockCanvasAndPost(canvas);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - mTimes.getFirst()) / NANOS;
        mTimes.addLast(lastTime);
        int size = mTimes.size();
        if (size > MAX_SIZE) {
            mTimes.removeFirst();
        }
        return difference > 0 ? mTimes.size() / difference : 0.0;
    }

    private void refreshSize() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        if (getHeight() < getWidth()) {
            layoutParams.width = CANVAS_WIDTH * getHeight() / CANVAS_HEIGHT;
        } else {
            layoutParams.height = CANVAS_HEIGHT * getWidth() / CANVAS_WIDTH;
        }

        setLayoutParams(layoutParams);
    }
}