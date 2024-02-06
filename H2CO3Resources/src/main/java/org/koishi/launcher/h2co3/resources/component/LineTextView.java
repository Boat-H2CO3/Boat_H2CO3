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

    public LineTextView(Context context) {
        super(context);
        setFocusable(true);
        line = new Paint();
        line.setColor(Color.BLUE);
        line.setStrokeWidth(2);
        paddingStart = dpToPx(context, 95);
        setPadding(paddingStart, 0, 0, 0);
        setGravity(Gravity.TOP);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(getTextSize());
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (getText().length() != 0) {
            int lineHeight = getLineHeight();
            int lineCount = getLineCount();
            for (int l = 0; l < lineCount; l++) {
                float y = (l + 1) * lineHeight - lineHeight / 4;
                canvas.drawText(String.valueOf(l + 1), 0, y, textPaint);
            }
        }
        int lineHeight = getLineHeight();
        int lineCount = getLineCount();
        int y = (getLayout().getLineForOffset(getSelectionStart()) + 1) * lineHeight;
        canvas.drawLine(paddingStart - 5, 0, paddingStart - 5, getHeight() + (lineCount * lineHeight), line);
        super.onDraw(canvas);
    }

    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}