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

public class SelectCardFragment extends Fragment {

    public class Card {
        public static final int CARD_NEW = 0;
        public static final int CARD_BULLTRADE = 1;
        public static final int CARD_BULLBANK = 2;

        public int type;

        public Card(int type) {
            this.type = type;
        }
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_select_card, container, false);

        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Card.CARD_BULLTRADE));
        cards.add(new Card(Card.CARD_NEW));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_cards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        SelectCardRecyclerViewAdapter adapter = new SelectCardRecyclerViewAdapter(getActivity(), cards);

        recyclerView.setAdapter(adapter);

        ImageButton backButton = root.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigateUp();
            }
        });

        return root;
    }



    public static class SelectCardRecyclerViewAdapter extends RecyclerView.Adapter {

        private List<Card> mData;
        private LayoutInflater mInflater;

        Drawable card_new;
        Drawable card_bulltrade;
        Drawable card_bullbank;
        Context context;

        // data is passed into the constructor
        SelectCardRecyclerViewAdapter(Context context, List<Card> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.context = context;

            card_new = context.getResources().getDrawable(R.drawable.ic_card_new);
            card_bulltrade = context.getResources().getDrawable(R.drawable.card_bull1);
            card_bullbank = context.getResources().getDrawable(R.drawable.card_bull2);
        }

        // inflates the row layout from xml when needed
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            view = mInflater.inflate(R.layout.recycler_row_card, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Card card = mData.get(position);
            ViewHolder viewHolder = ((ViewHolder) holder);
            switch (card.type) {
                case Card.CARD_NEW:
                    viewHolder.image.setImageDrawable(card_new);
                    viewHolder.cardView.setCardElevation(0);
                    viewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Navigation.findNavController(v).navigate(R.id.action_selectCardFragment_to_addCardFragment);
                        }
                    });
                    break;
                case Card.CARD_BULLTRADE:
                    viewHolder.image.setImageDrawable(card_bulltrade);
                    viewHolder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Navigation.findNavController(v).navigate(R.id.action_selectCardFragment_to_cardMenuFragment);
                        }
                    });
                    break;
                case Card.CARD_BULLBANK:
                    viewHolder.image.setImageDrawable(card_bullbank);
                    break;
            }
        }

        // total number of rows
        @Override
        public int getItemCount() {
            if (mData == null)
                return 0;
            return mData.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
                Card card = mData.get(position);
                if (card != null) {
                    return card.type;
                }
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            CardView cardView;

            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.card_image);
                cardView = itemView.findViewById(R.id.card_card_view);
            }
        }
    }
}
