package com.bullcoin.app;

import android.os.Bundle;

import com.bullcoin.app.datamodel.DataModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

//        if (savedInstanceState != null) {
//            //Restore the fragment's instance
////            mMyFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
//        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        //Save the fragment's instance
////        getSupportFragmentManager().putFragment(outState, "myFragmentName", mMyFragment);
//        navController.saveState();
//    }
}