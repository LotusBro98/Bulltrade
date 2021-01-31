package com.bullcoin.app.navigation.home.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Card;

public class CardMenuFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_card_menu, container, false);

        setRetainInstance(true);

        int type = getArguments().getInt("cardID");

        ImageButton backButton = root.findViewById(R.id.back_button);
        ImageView cardImage = root.findViewById(R.id.card_image);

        if (type == Card.CARD_BULLTRADE) {
            cardImage.setImageDrawable(root.getResources().getDrawable(R.drawable.card_bull1));
        } else if (type == Card.CARD_BULLBANK) {
            cardImage.setImageDrawable(root.getResources().getDrawable(R.drawable.card_bull2));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigateUp();
            }
        });

        return root;
    }
}
