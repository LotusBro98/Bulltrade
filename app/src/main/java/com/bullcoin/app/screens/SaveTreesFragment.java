package com.bullcoin.app.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;

public class SaveTreesFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.save_the_trees, container, false);

        ImageView backButton = root.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());

        return root;
    }
}
