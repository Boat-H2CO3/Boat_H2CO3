package org.koishi.launcher.h2co3.ui.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;

public class HomeFragment extends H2CO3Fragment {

    H2CO3CardView home_file_check;
    H2CO3TextView home_file_check_title, home_file_check_message;
    H2CO3Button home_file_check_button;

    View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        findView();
        checkFile();
        return view;
    }

    private void checkFile() {
    }

    private void findView() {
        home_file_check_title = findViewById(view, R.id.home_file_check_title);
        home_file_check_title.setText(H2CO3Tools.getBoatValueString("auth_player_name","Player"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}