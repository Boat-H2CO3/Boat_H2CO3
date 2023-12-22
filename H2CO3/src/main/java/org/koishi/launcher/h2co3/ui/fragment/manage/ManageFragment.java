package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.dialog.H2CO3BoatRuntimeDialog;
import org.koishi.launcher.h2co3.resources.component.H2CO3CardView;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.ui.H2CO3BoatClientActivity;

public class ManageFragment extends H2CO3Fragment implements View.OnClickListener {

    H2CO3CardView open_1;

    View view;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage, container, false);
        open_1 = findViewById(view,R.id.open_1);
        open_1.setOnClickListener(this);
        return view;
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
        if(v==open_1){
            H2CO3BoatRuntimeDialog runtime = new H2CO3BoatRuntimeDialog(requireActivity());
            runtime.show();
        }
    }
}