package com.bullcoin.app.cards;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;

import java.util.ArrayList;
import java.util.List;

public class AddCardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_card, container, false);

        ImageButton backButton = root.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());

        CardView bulltrade_card = root.findViewById(R.id.card_bulltrade_card);
        bulltrade_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_addCardFragment_to_bullcardFragment);
            }
        });

        return root;
    }
}
