package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;

public class LineTextView extends H2CO3TextView {
    private final Paint line;
    private final Paint textPaint;
    private final int paddingStart;
    private static final int LINE_INDICATOR_OFFSET = 5;
    private static final float LINE_HEIGHT_FRACTION = 0.25f;

    public LineTextView(Context context) {
        super(context);
        setFocusable(true);
        line = new Paint();
        line.setColor(Color.BLUE);
        line.setStrokeWidth(2);
        paddingStart = dpToPx(context, 50);
        setPadding(paddingStart, 0, 0, 0);
        setGravity(Gravity.TOP);
        textPaint = new Paint();
        textPaint.setTextSize(getTextSize());
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (getLayout() != null && getText().length() != 0) {
            int lineHeight = getLineHeight();
            int lineCount = getLineCount();
            float lineIndicatorYOffset = lineHeight * LINE_HEIGHT_FRACTION;
            for (int l = 0; l < lineCount; l++) {
                float y = (l + 1) * lineHeight - lineIndicatorYOffset;
                canvas.drawText(String.valueOf(l + 1), 0, y, textPaint);
            }
            int y = (getLayout().getLineForOffset(getSelectionStart()) + 1) * lineHeight;
            canvas.drawLine(paddingStart - LINE_INDICATOR_OFFSET, 0, paddingStart - LINE_INDICATOR_OFFSET, getHeight(), line);
        }
    }

    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}