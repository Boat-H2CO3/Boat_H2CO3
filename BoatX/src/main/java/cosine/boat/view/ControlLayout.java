package cosine.boat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class ControlLayout extends TextureView {
    private final boolean mControlVisible = false;
    private boolean mModifiable;

    public ControlLayout(Context ctx) {
        super(ctx);
    }

    public ControlLayout(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }


    public boolean getModifiable() {
        return mModifiable;
    }
}
