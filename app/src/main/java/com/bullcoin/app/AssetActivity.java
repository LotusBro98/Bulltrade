package com.bullcoin.app;

import android.app.Activity;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.navigation.chat.ChatDialogueActivity;

public class AssetActivity extends AppCompatActivity implements TextWatcher{
    Asset asset;
    int quantity = 0;

    TextView assetName;
    TextView assetPrice;
    TextView totalCost;
    Button buttonBuy;
    Button buttonSell;
    EditText editQuantity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_asset);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            int assetID = args.getInt("assetID");
            asset = DataModel.get().getAssets().get(assetID);
        }

        assetName = findViewById(R.id.asset_name);
        assetPrice = findViewById(R.id.asset_price);
        totalCost = findViewById(R.id.asset_total_cost);
        buttonBuy = findViewById(R.id.button_buy);
        buttonSell = findViewById(R.id.button_sell);
        editQuantity = findViewById(R.id.asset_quantity);

        assetName.setText(asset.getName());
        assetPrice.setText("$" + String.format("%.2f", asset.getPrice()));

        buttonBuy.setOnClickListener(v -> DataModel.get().buyAsset(this, asset, quantity));
        buttonSell.setOnClickListener(v -> DataModel.get().sellAsset(this, asset, quantity));

        editQuantity.addTextChangedListener(this);

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(v -> finish());
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        return false;
    }

    public static void navigateHere(Activity activity, Asset asset) {
        Intent intent = new Intent(activity, AssetActivity.class);
        Bundle args = new Bundle();
        args.putInt("assetID", asset.getId());
        intent.putExtras(args);
        activity.startActivity(intent);
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
