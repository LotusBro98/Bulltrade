package com.bullcoin.app.navigation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;

public class AssetFragment extends Fragment implements TextWatcher{

    Asset asset;
    int quantity = 0;

    TextView assetName;
    TextView assetPrice;
    TextView totalCost;
    Button buttonBuy;
    Button buttonSell;
    EditText editQuantity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_asset, container, false);

        setRetainInstance(true);

        int assetID = getArguments().getInt("assetID");
        asset = DataModel.get().getAssets().get(assetID);


        assetName = root.findViewById(R.id.asset_name);
        assetPrice = root.findViewById(R.id.asset_price);
        totalCost = root.findViewById(R.id.asset_total_cost);
        buttonBuy = root.findViewById(R.id.button_buy);
        buttonSell = root.findViewById(R.id.button_sell);
        editQuantity = root.findViewById(R.id.asset_quantity);

        assetName.setText(asset.getName());
        assetPrice.setText("$" + String.format("%.2f", asset.getPrice()));

        buttonBuy.setOnClickListener(v -> DataModel.get().buyAsset(root.getContext(), asset, quantity));
        buttonSell.setOnClickListener(v -> DataModel.get().sellAsset(root.getContext(), asset, quantity));

        editQuantity.addTextChangedListener(this);

        ImageButton back = root.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigateUp();
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("assetID", asset.getId());
        //Save the fragment's state here
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }

    public static void navigateHere(View view, int actionID, Asset asset) {
        Bundle bundle = new Bundle();
        bundle.putInt("assetID", asset.getId());
        Navigation.findNavController(view).navigate(actionID, bundle);
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
