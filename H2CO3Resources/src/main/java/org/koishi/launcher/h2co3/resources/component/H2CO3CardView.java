package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import org.koishi.launcher.h2co3.resources.R;

public class H2CO3CardView extends MaterialCardView {
    private int bgColor;
    private boolean hasShadow;//是否有阴影
    private boolean hasRadius;//是否有圆角
    private final float[] radiusList = new float[8];// 矩阵四角圆角 两个一组分别为一角的x轴半径y轴半径  四组分别为 上左 上右  下右  下左
    private float shadowRadius = 0f;//阴影模糊半径
    private Paint shadowPaint;
    private float shadowOffsetY = 0f;
    private float shadowOffsetX = 0f;
    private int shadowColor;
    private RectF rectF;//内容绘制矩阵
    private Path path;//阴影绘制路径/内容裁剪路径
    private int left, top, right, bottom;//用户指定padding

    public H2CO3CardView(@NonNull Context context) {
        this(context, null);
    }

    public H2CO3CardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public H2CO3CardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.H2CO3CardView);

        shadowColor = getResources().getColor(rikka.material.R.color.primary_dark);
        bgColor = getResources().getColor(R.color.white);
        left = getPaddingLeft();
        right = getPaddingRight();
        top = getPaddingRight();
        bottom = getPaddingRight();

        float radius = a.getDimension(R.styleable.H2CO3CardView_android_radius, 0);
        float shadowOffset = a.getDimension(R.styleable.H2CO3CardView_shadowOffset, 0);
        float dx = a.getDimension(R.styleable.H2CO3CardView_android_x, shadowOffset);
        float dy =  a.getDimension(R.styleable.H2CO3CardView_android_y, shadowOffset);
        shadowColor =  a.getColor(R.styleable.H2CO3CardView_android_shadowColor, shadowColor);
        bgColor = a.getColor(R.styleable.H2CO3CardView_cardBackgroundColor, bgColor);

        if (radius > 0) {
            setRadius(radius);
        } else {
            float topLeft = a.getDimension(R.styleable.H2CO3CardView_android_topLeftRadius, 0);
            float topRight = a.getDimension(R.styleable.H2CO3CardView_android_topRightRadius, 0);
            float bottomRight = a.getDimension(R.styleable.H2CO3CardView_android_bottomRightRadius, 0);
            float bottomLeft = a.getDimension(R.styleable.H2CO3CardView_android_bottomLeftRadius, 0);
            setRadius(topLeft, topRight, bottomRight, bottomLeft);
        }
        setShadowOffSet(dx, dy);
        setShadowRadius(a.getDimension(R.styleable.H2CO3CardView_shadowRadius, 0));
        a.recycle();
    }

    public void setCardBackgroundColor(int bgColor) {
        this.bgColor = bgColor;
        if (shadowPaint != null) {
            shadowPaint.setColor(bgColor);
        }
        invalidate();
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        hasShadow = shadowRadius > 0;
        if (hasShadow) {
            if (shadowPaint == null) {
                shadowPaint = new Paint();
                shadowPaint.setStyle(Paint.Style.FILL);
                shadowPaint.setAntiAlias(true);
            }
            shadowPaint.setColor(bgColor);
            setShadowLayer();
            setPaddingRadius();
        }
        if (isLaidOut()) {
            invalidate();
        }
    }

    public void setShadowColor(int color) {
        this.shadowColor = color;
        setShadowLayer();
        invalidate();
    }

    public void setShadowDx(float dx) {
        shadowOffsetX = dx;
        setShadowOffSetImpl();
    }

    public void setShadowDy(float dy) {
        shadowOffsetY = dy;
        setShadowOffSetImpl();
    }

    public void setShadowOffSet(float offSet) {
        setShadowOffSet(offSet, offSet);
    }

    public void setShadowOffSet(float dx, float dy) {
        shadowOffsetX = dx;
        shadowOffsetY = dy;
        setShadowOffSetImpl();
    }


    private void setShadowOffSetImpl() {
        setShadowLayer();
        if (isLaidOut()) {
            invalidate();
        }
    }

    private void setShadowLayer() {
        if (shadowPaint != null) {
            shadowPaint.setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor);
        }
    }

    private void setPaddingRadius() {
        float left = this.left;
        float top = this.top;
        float right = this.right;
        float bottom = this.bottom;
        if (hasShadow) {
            left += shadowRadius;
            top += shadowRadius;
            right += shadowRadius;
            bottom += shadowRadius;
        }
        super.setPadding((int) left,
                (int) top,
                (int) right,
                (int) bottom);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        setPaddingRadius();
    }

    @Deprecated
    public void setShadowRound(float shadowRound) {
        setRadius(shadowRound);
    }

    public void setRadius(float radius) {
        setRadius(radius, radius, radius, radius);
    }

    public void setRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        radiusList[0] = topLeft;
        radiusList[1] = topLeft;
        radiusList[2] = topRight;
        radiusList[3] = topRight;
        radiusList[4] = bottomRight;
        radiusList[5] = bottomRight;
        radiusList[6] = bottomLeft;
        radiusList[7] = bottomLeft;
        hasRadius = topLeft > 0 || topRight > 0 || bottomRight > 0 || bottomLeft > 0;
        if (hasRadius) {
            rectF = new RectF();
            path = new Path();
        }
        if (isLaidOut()) {
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasShadow || hasRadius) {
            if (rectF == null) {
                rectF = new RectF();
            }
            if (path == null) {
                path = new Path();
            }
            rectF.left = shadowRadius;
            rectF.top = shadowRadius;
            rectF.right = w - shadowRadius;
            rectF.bottom = h - shadowRadius;
            if (hasRadius) {
                if (!path.isEmpty()) {
                    path.reset();
                }
                path.addRoundRect(rectF, radiusList, Path.Direction.CW);
            }
        }
    }

    //  关键代码 提前裁剪/绘制背景
    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (hasShadow || hasRadius) {
            if (rectF != null && shadowPaint != null && hasShadow) {
                // 绘制阴影
                canvas.drawPath(path, shadowPaint);
            }
            if (hasRadius && path != null) {
                // 裁剪圆角
                canvas.clipPath(path);
                // 填充底色
                canvas.drawColor(bgColor);
            }
        }
        super.dispatchDraw(canvas);
    }
}
