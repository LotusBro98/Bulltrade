package com.bullcoin.app.navigation.home.cards;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CardFragmentBroker extends Fragment {
    public final static int TYPE_BROKER = 0;
    public final static int TYPE_BULLCOIN = 1;
    public final static int TYPE_BULLBANK = 2;

    private int type;
    TextView balance;

    public CardFragmentBroker(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_broker, container, false);

        setRetainInstance(true);

        ImageView icon = view.findViewById(R.id.card_icon);
        TextView accountType = view.findViewById(R.id.card_account_type);
        TextView yourBalance = view.findViewById(R.id.card_your_balance);
        balance = view.findViewById(R.id.card_balance);
        TextView cardholderName = view.findViewById(R.id.cardholder_name);

        switch (type) {
            case TYPE_BROKER:
                icon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_bull_small));
                accountType.setText(R.string.broker_account);
                yourBalance.setText(R.string.your_balance);
                balance.setText("$" + String.format("%.2f", DataModel.get().getBrokerBalance()));
                break;
            case TYPE_BULLCOIN:
                icon.setImageDrawable(view.getResources().getDrawable(R.drawable.asset_bullcoin));
                accountType.setText(R.string.bullcoin);
                yourBalance.setText(R.string.your_bcn_balance);
                balance.setText(String.valueOf(DataModel.get().getBullcoinBalance()));
                break;
            case TYPE_BULLBANK:
                icon.setImageDrawable(view.getResources().getDrawable(R.drawable.ic_bull_small));
                accountType.setText(R.string.bullbank_card);
                yourBalance.setText(R.string.your_bank_account_balance);
                balance.setText("$" +  String.format("%.2f", DataModel.get().getBankBalance()));
                break;
        }

        cardholderName.setText(DataModel.get().getUserFirstName() + " " + DataModel.get().getUserLastName());

        ImageButton button = view.findViewById(R.id.card_opt_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_card, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindowCards(popupView, width, height, focusable, view);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (type) {
            case TYPE_BROKER:
                balance.setText("$" + String.format("%.2f", DataModel.get().getBrokerBalance()));
                break;
            case TYPE_BULLCOIN:
                balance.setText(String.valueOf(DataModel.get().getBullcoinBalance()));
                break;
            case TYPE_BULLBANK:
                balance.setText("$" +  String.format("%.2f", DataModel.get().getBankBalance()));
                break;
        }
    }
}
