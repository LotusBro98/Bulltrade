package com.bullcoin.app.navigation.home.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Card;
import com.bullcoin.app.datamodel.DataModel;

public class BullcardFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bulltrade_card, container, false);

        setRetainInstance(true);

        int type = getArguments().getInt("cardID");

        ImageButton backButton = root.findViewById(R.id.back_button);
        ImageView cardImage = root.findViewById(R.id.card_image);
        Button orderButton = root.findViewById(R.id.order_button);

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

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataModel.get().addCard(root.getContext(), type);
                Navigation.findNavController(root).navigate(R.id.action_bullcardFragment_to_selectCardFragment2);
            }
        });

        return root;
    }
}
