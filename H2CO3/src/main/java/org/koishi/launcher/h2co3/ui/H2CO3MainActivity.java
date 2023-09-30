package org.koishi.launcher.h2co3.ui;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.resources.component.H2CO3ToolBar;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;

public class H2CO3MainActivity extends H2CO3Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        H2CO3ToolBar toolbar = findViewById(R.id.toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        setSupportActionBar(toolbar);
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.navigation_home,
                        R.id.navigation_directory,
                        R.id.navigation_manage)
                        .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}