package com.bullcoin.app.asset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bullcoin.app.LocalizedActivity;
import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;
import com.google.android.material.tabs.TabLayout;

public class AssetActivity extends LocalizedActivity {
    Asset asset;

    TextView assetName;
    TextView assetPrice;
    AssetTabAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_asset);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            int assetID = args.getInt("assetID");
            asset = DataModel.get().getAssets().get(assetID);
        }

        adapter = new AssetTabAdapter(getSupportFragmentManager(), this);

        ViewPager assetPager = findViewById(R.id.asset_pager);
        assetPager.setAdapter(adapter);
        assetPager.setCurrentItem(0);

        TabLayout tabLayout = findViewById(R.id.asset_tabs);
        tabLayout.setupWithViewPager(assetPager);

        assetName = findViewById(R.id.asset_name);
        assetPrice = findViewById(R.id.asset_price);

        assetName.setText(asset.getName());
        assetPrice.setText("$" + String.format("%.2f", asset.getPrice()));

        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(v -> finish());
    }

    public static void navigateHere(Activity activity, Asset asset) {
        Intent intent = new Intent(activity, AssetActivity.class);
        Bundle args = new Bundle();
        args.putInt("assetID", asset.getId());
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public static class AssetTabAdapter extends FragmentPagerAdapter {
        private String[] tabTitles;
        private AssetActivity assetActivity;

        public AssetTabAdapter(@NonNull FragmentManager fragmentManager, AssetActivity assetActivity) {
            super(fragmentManager);
            this.assetActivity = assetActivity;
            tabTitles = new String[] {
                    assetActivity.getString(R.string.overview),
                    assetActivity.getString(R.string.bidding),
                    assetActivity.getString(R.string.forecasts)
            };
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ReviewFragment(assetActivity.asset);
                case 1:
                    return new BiddingFragment(assetActivity.asset);
                case 2:
                    return new ForecastsFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
