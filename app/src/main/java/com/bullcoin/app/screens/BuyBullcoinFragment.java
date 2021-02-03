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
import com.bullcoin.app.asset.AssetActivity;
import com.bullcoin.app.datamodel.DataModel;

public class BuyBullcoinFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.where_does_money_come_from2, container, false);

        ImageView backButton = root.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigateUp();
            }
        });

        ImageView buyBullcoinButton = root.findViewById(R.id.buy_bullcoin_button);
        buyBullcoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetActivity.navigateHere(getActivity(), DataModel.get().getAsset("Bullcoin"));
            }
        });

        return root;
    }
}
