package org.koishi.launcher.h2co3.ui.fragment.home;

import static org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity.attachControllerInterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Button;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextView;
import org.koishi.launcher.h2co3.ui.H2CO3LauncherClientActivity;

public class HomeFragment extends H2CO3Fragment implements View.OnClickListener{

    H2CO3CardView home_file_check;
    H2CO3TextView home_file_check_title, home_file_check_message;
    H2CO3Button home_game_play_button;

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
        home_game_play_button = findViewById(view,R.id.home_game_play_button);
        home_game_play_button.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v == home_game_play_button){
            startActivity(new Intent(requireActivity(), H2CO3LauncherClientActivity.class));
            attachControllerInterface();
        }
    }
}