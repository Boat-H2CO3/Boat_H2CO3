package cosine.boat;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.annotation.NonNull;

import java.util.Objects;

public class TextInputProxyEditTextbox extends androidx.appcompat.widget.AppCompatEditText {
    public final int allowedLength;
    public final boolean limitInput;
    /* access modifiers changed from: private */
    public MCPEKeyWatcher _mcpeKeyWatcher;

    public TextInputProxyEditTextbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._mcpeKeyWatcher = null;
        this.allowedLength = 160;
        this.limitInput = false;
    }

    public TextInputProxyEditTextbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._mcpeKeyWatcher = null;
        this.allowedLength = 160;
        this.limitInput = false;
    }

    public TextInputProxyEditTextbox(Context context, int allowedLength2, boolean limitInput2) {
        super(context);
        this._mcpeKeyWatcher = null;
        this.allowedLength = allowedLength2;
        this.limitInput = limitInput2;
        setFilters(limitInput2 ? new InputFilter[]{new InputFilter.LengthFilter(this.allowedLength), (source, start, end, dest, dstart, dend) -> {
            if (source.equals("")) {
            }
            return source;
        }} : new InputFilter[]{new InputFilter.LengthFilter(this.allowedLength)});
    }

    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        return new MCPEInputConnection(super.onCreateInputConnection(outAttrs), true, this);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 1) {
            return super.onKeyPreIme(keyCode, event);
        }
        if (this._mcpeKeyWatcher != null) {
            this._mcpeKeyWatcher.onBackKeyPressed();
        }
        return false;
    }

    public void setOnMCPEKeyWatcher(MCPEKeyWatcher mcpeKeyWatcher) {
        this._mcpeKeyWatcher = mcpeKeyWatcher;
    }

    public interface MCPEKeyWatcher {
        void onBackKeyPressed();

        void onDeleteKeyPressed();
    }

    private class MCPEInputConnection extends InputConnectionWrapper {
        final TextInputProxyEditTextbox textbox;

        public MCPEInputConnection(InputConnection target, boolean mutable, TextInputProxyEditTextbox textbox2) {
            super(target, mutable);
            this.textbox = textbox2;
        }

        public boolean sendKeyEvent(KeyEvent event) {
            if (Objects.requireNonNull(this.textbox.getText()).length() != 0 || event.getAction() != 0 || event.getKeyCode() != 67) {
                return super.sendKeyEvent(event);
            }
            if (TextInputProxyEditTextbox.this._mcpeKeyWatcher != null) {
                TextInputProxyEditTextbox.this._mcpeKeyWatcher.onDeleteKeyPressed();
            }
            return false;
        }
    }
}
