package org.koishi.launcher.h2co3.ui.fragment.manage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3Fragment;
import org.koishi.launcher.h2co3.ui.H2CO3BoatClientActivity;

public class ManageFragment extends H2CO3Fragment {

    View view;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage, container, false);
        startActivity(new Intent(requireActivity(), H2CO3BoatClientActivity.class));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}