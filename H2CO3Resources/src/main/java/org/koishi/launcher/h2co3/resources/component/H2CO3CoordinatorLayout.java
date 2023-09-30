package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class H2CO3CoordinatorLayout extends CoordinatorLayout {
    public H2CO3CoordinatorLayout(Context context) {
        super(context);
        setSystemUiFlags();
    }

    public H2CO3CoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSystemUiFlags();
    }

    public H2CO3CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSystemUiFlags();
    }


    private void setSystemUiFlags() {
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(this);
            if (windowInsets != null) {
                Insets systemInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                layoutParams.leftMargin = systemInsets.left;
                layoutParams.rightMargin = systemInsets.right;
                setLayoutParams(layoutParams);
            }
        }
    }
}