package com.bullcoin.app.asset;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;

public class ReviewFragment extends Fragment {
    Asset asset;

    public ReviewFragment(Asset asset) {
        super();

        this.asset = asset;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_review, container, false);

        setRetainInstance(true);

        TextView assetText = root.findViewById(R.id.asset_text);
        assetText.setText(asset.getDescription(getContext()));

        TextView countryText = root.findViewById(R.id.asset_country_text);
        countryText.setText(asset.getCountryNameID());

        ImageView countryIcon = root.findViewById(R.id.asset_country_icon);
        countryIcon.setImageDrawable(getResources().getDrawable(asset.getCountryIconID()));

        return root;
    }
}
