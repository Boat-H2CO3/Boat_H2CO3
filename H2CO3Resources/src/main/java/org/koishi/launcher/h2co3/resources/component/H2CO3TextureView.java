package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.koishi.launcher.h2co3.resources.R;

import java.util.Locale;

public class H2CO3TextureView extends FrameLayout {
    private TextureView mTextureView;
    private TextView mFpsTextView;
    private long mFrameCount = 0; // 帧数计数器
    private long mLastFpsTime = 0; // 上次更新帧数的时间

    public H2CO3TextureView(Context context) {
        super(context);
        init();
    }

    public H2CO3TextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public H2CO3TextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_fps_texture_view, this, true);
        mTextureView = findViewById(R.id.texture_view);
        mFpsTextView = findViewById(R.id.fps_text_view);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // SurfaceTexture可用时开始绘制
                startDrawing();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // SurfaceTexture尺寸变化时更新布局
                updateLayout(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                // SurfaceTexture销毁时停止绘制
                stopDrawing();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // SurfaceTexture更新时计算帧数
                calculateFps();
            }
        });
    }

    private void startDrawing() {
        // 开始绘制
        // TODO: 在这里实现绘制逻辑
    }

    private void stopDrawing() {
        // 停止绘制
        // TODO: 在这里实现停止绘制的逻辑
    }

    private void calculateFps() {
        // 计算帧数
        long currentTime = System.currentTimeMillis();
        if (mLastFpsTime == 0) {
            mLastFpsTime = currentTime;
        }
        mFrameCount++;
        if (currentTime - mLastFpsTime >= 1000) {
            float fps = mFrameCount * 1000f / (currentTime - mLastFpsTime);
            mFpsTextView.setText(String.format(Locale.getDefault(), "FPS: %.2f", fps));
            mFrameCount = 0;
            mLastFpsTime = currentTime;
        }
    }

    private void updateLayout(int width, int height) {
        // 更新布局
        // TODO: 在这里更新布局的逻辑
    }
}