package com.bullcoin.app.asset;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;

public class BiddingFragment extends Fragment implements TextWatcher {

    protected TextView totalCost;
    protected Button buttonBuy;
    protected Button buttonSell;
    protected EditText editQuantity;

    Asset asset;
    int quantity = 0;

    public BiddingFragment(Asset asset) {
        this.asset = asset;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_bidding, container, false);

        setRetainInstance(true);

        totalCost = root.findViewById(R.id.asset_total_cost);
        buttonBuy = root.findViewById(R.id.button_buy);
        buttonSell = root.findViewById(R.id.button_sell);
        editQuantity = root.findViewById(R.id.asset_quantity);

        buttonBuy.setOnClickListener(v -> DataModel.get().buyAsset(getContext(), asset, quantity));
        buttonSell.setOnClickListener(v -> DataModel.get().sellAsset(getContext(), asset, quantity));

        editQuantity.addTextChangedListener(this);

        editQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        quantity = Integer.parseInt("0" + s.toString());
        double cost = asset.getPrice() * quantity;
        totalCost.setText("$" + String.format("%.2f", cost));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
}
