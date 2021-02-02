package com.bullcoin.app.screens;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BullbankFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bullbank, container, false);

        ImageView backButton = root.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());

        return root;
    }
}
