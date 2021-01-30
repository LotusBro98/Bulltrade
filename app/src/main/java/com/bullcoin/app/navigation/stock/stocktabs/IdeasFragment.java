package com.bullcoin.app.navigation.stock.stocktabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.navigation.AssetFragment;

public class IdeasFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ideas, container, false);
        return root;


    }

    public void goToApple(View view) {
        AssetFragment.navigateHere(view, R.id.action_navigation_stock_to_assetFragment, DataModel.get().getAsset("Apple Inc."));
    }
}
