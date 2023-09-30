package org.koishi.launcher.h2co3.core.view;

import android.view.View;

public class ViewFinder {
    private View rootView;

    public ViewFinder(View rootView) {
        this.rootView = rootView;
    }

    public <T extends View> T findViewById(int viewId) {
        return rootView.findViewById(viewId);
    }
}