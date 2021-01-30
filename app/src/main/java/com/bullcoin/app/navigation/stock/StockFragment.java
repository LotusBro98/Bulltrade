package com.bullcoin.app.navigation.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bullcoin.app.R;
import com.bullcoin.app.navigation.stock.stocktabs.CryptFragment;
import com.bullcoin.app.navigation.stock.stocktabs.IdeasFragment;
import com.bullcoin.app.navigation.stock.stocktabs.ShareFragment;
import com.bullcoin.app.navigation.stock.stocktabs.StockStockFragment;
import com.google.android.material.tabs.TabLayout;

public class StockFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stock, container, false);

        TabAdapter adapter = new TabAdapter(getChildFragmentManager());

        ViewPager tabPager = root.findViewById(R.id.stock_tabs_pager);
        tabPager.setAdapter(adapter);
        tabPager.setCurrentItem(0);

        TabLayout cardTabs = root.findViewById(R.id.stock_tabs);
        cardTabs.setupWithViewPager(tabPager);

        return root;
    }

    public static class TabAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Ideas", "Stock", "Share", "Crypt" };

        public TabAdapter(@NonNull FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new IdeasFragment();
                case 1:
                    return new StockStockFragment();
                case 2:
                    return new ShareFragment();
                case 3:
                    return new CryptFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}