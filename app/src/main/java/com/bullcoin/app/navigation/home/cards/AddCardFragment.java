package com.bullcoin.app.navigation.home.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Card;
import com.bullcoin.app.datamodel.DataModel;

public class AddCardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_card, container, false);

        ImageButton backButton = root.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());

        CardView bulltrade_card = root.findViewById(R.id.card_bulltrade_card);
        CardView bullbank_card = root.findViewById(R.id.card_bullbank_card);

        boolean has1 = false;
        boolean has2 = false;
        for (Card card : DataModel.get().getCards()) {
            if (card.type == Card.CARD_BULLTRADE) {
                has1 = true;
            } else if (card.type == Card.CARD_BULLBANK) {
                has2 = true;
            }
        }

        if (has1) {
            bulltrade_card.setVisibility(View.GONE);
        }

        if (has2) {
            bullbank_card.setVisibility(View.GONE);
        }

        bulltrade_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("cardID", Card.CARD_BULLTRADE);
                Navigation.findNavController(root).navigate(R.id.action_addCardFragment_to_bullcardFragment, args);
            }
        });

        bullbank_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("cardID", Card.CARD_BULLBANK);
                Navigation.findNavController(root).navigate(R.id.action_addCardFragment_to_bullcardFragment, args);
            }
        });

        return root;
    }
}
