package org.koishi.launcher.h2co3.resources.component;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import org.koishi.launcher.h2co3.resources.R;


public class BackButtonPreference extends Preference {
    public BackButtonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressWarnings("unused") public BackButtonPreference(Context context) {
        this(context, null);
    }

    private void init(){
        if(getTitle() == null){
            setTitle(R.string.title_action);
        }
        if(getIcon() == null){
            setIcon(rikka.material.R.drawable.ic_expand_less_24dp);
        }
    }


    @Override
    protected void onClick() {
        // It is caught by an ExtraListener in the LauncherActivity
       // ExtraCore.setValue(ExtraConstants.BACK_PREFERENCE, "true");
    }
}