package com.bullcoin.app.navigation.stock.stocktabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.navigation.AssetFragment;

public class IdeasFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ideas, container, false);

        (root.findViewById(R.id.imageView_apple1)).setOnClickListener(v -> goToAsset(v, "Apple Inc."));
        (root.findViewById(R.id.imageView121)).setOnClickListener(v -> goToAsset(v, "Apple Inc."));
        (root.findViewById(R.id.textView_apple1)).setOnClickListener(v -> goToAsset(v, "Apple Inc."));
        (root.findViewById(R.id.textView231)).setOnClickListener(v -> goToAsset(v, "Apple Inc."));

        (root.findViewById(R.id.imageView13)).setOnClickListener(v -> goToAsset(v, "Sohu.com"));
        (root.findViewById(R.id.imageView131)).setOnClickListener(v -> goToAsset(v, "Sohu.com"));
        (root.findViewById(R.id.textView24)).setOnClickListener(v -> goToAsset(v, "Sohu.com"));
        (root.findViewById(R.id.textView241)).setOnClickListener(v -> goToAsset(v, "Sohu.com"));

        (root.findViewById(R.id.imageView14)).setOnClickListener(v -> goToAsset(v, "JD.com"));
        (root.findViewById(R.id.imageView141)).setOnClickListener(v -> goToAsset(v, "JD.com"));
        (root.findViewById(R.id.textView25)).setOnClickListener(v -> goToAsset(v, "JD.com"));
        (root.findViewById(R.id.textView251)).setOnClickListener(v -> goToAsset(v, "JD.com"));

        (root.findViewById(R.id.imageView15)).setOnClickListener(v -> goToAsset(v, "Bilibili Inc."));
        (root.findViewById(R.id.imageView151)).setOnClickListener(v -> goToAsset(v, "Bilibili Inc."));
        (root.findViewById(R.id.textView26)).setOnClickListener(v -> goToAsset(v, "Bilibili Inc."));
        (root.findViewById(R.id.textView261)).setOnClickListener(v -> goToAsset(v, "Bilibili Inc."));

        (root.findViewById(R.id.imageView16)).setOnClickListener(v -> goToAsset(v, "Gazprom"));
        (root.findViewById(R.id.imageView161)).setOnClickListener(v -> goToAsset(v, "Gazprom"));
        (root.findViewById(R.id.textView27)).setOnClickListener(v -> goToAsset(v, "Gazprom"));
        (root.findViewById(R.id.textView271)).setOnClickListener(v -> goToAsset(v, "Gazprom"));

        (root.findViewById(R.id.imageView17)).setOnClickListener(v -> goToAsset(v, "VTB Perp"));
        (root.findViewById(R.id.imageView171)).setOnClickListener(v -> goToAsset(v, "VTB Perp"));
        (root.findViewById(R.id.textView28)).setOnClickListener(v -> goToAsset(v, "VTB Perp"));
        (root.findViewById(R.id.textView281)).setOnClickListener(v -> goToAsset(v, "VTB Perp"));

        (root.findViewById(R.id.imageView18)).setOnClickListener(v -> goToAsset(v, "Lukoil"));
        (root.findViewById(R.id.imageView181)).setOnClickListener(v -> goToAsset(v, "Lukoil"));
        (root.findViewById(R.id.textView29)).setOnClickListener(v -> goToAsset(v, "Lukoil"));
        (root.findViewById(R.id.textView291)).setOnClickListener(v -> goToAsset(v, "Lukoil"));

        (root.findViewById(R.id.imageView19)).setOnClickListener(v -> goToAsset(v, "Evras"));
        (root.findViewById(R.id.imageView191)).setOnClickListener(v -> goToAsset(v, "Evras"));
        (root.findViewById(R.id.textView30)).setOnClickListener(v -> goToAsset(v, "Evras"));
        (root.findViewById(R.id.textView301)).setOnClickListener(v -> goToAsset(v, "Evras"));

        return root;
    }

    public void goToAsset(View view, String assetName) {
        Asset asset = DataModel.get().getAsset(assetName);
        AssetFragment.navigateHere(view, R.id.action_navigation_stock_to_assetFragment, asset);
    }
}
