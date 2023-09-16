package org.koishi.launcher.h2co3.resources.component;

import android.view.View;

import androidx.fragment.app.Fragment;

public class H2CO3Fragment extends Fragment {

    public final <T extends View> T findViewById(View view, int id) {
        return view.findViewById(id);
    }

}