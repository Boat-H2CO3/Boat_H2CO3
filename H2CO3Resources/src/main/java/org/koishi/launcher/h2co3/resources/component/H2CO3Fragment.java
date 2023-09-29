package org.koishi.launcher.h2co3.resources.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

public class H2CO3Fragment extends Fragment {

    public final <T extends View> T findViewById(View view, int id) {
        return view.findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UltimateBarX.statusBar(this)
                .fitWindow(true)
                .apply();

        return null;
    }
}