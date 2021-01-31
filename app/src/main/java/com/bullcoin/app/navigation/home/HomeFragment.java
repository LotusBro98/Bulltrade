package com.bullcoin.app.navigation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.bullcoin.app.R;
import com.bullcoin.app.navigation.home.cards.CardFragmentBroker;
import com.bullcoin.app.navigation.home.maintabs.BookmarksFragment;
import com.bullcoin.app.navigation.home.maintabs.NewsFragment;
import com.bullcoin.app.navigation.home.maintabs.WalletFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        CardAdapter adapter = new CardAdapter(getChildFragmentManager());

        ViewPager cardPager = root.findViewById(R.id.card_pager);
        cardPager.setAdapter(adapter);
        cardPager.setCurrentItem(0);

        TabLayout cardTabs = root.findViewById(R.id.card_tabs);
        cardTabs.setupWithViewPager(cardPager);

        MainTabAdapter mainTabAdapter = new MainTabAdapter(getChildFragmentManager());

        ViewPager mainTabPager = root.findViewById(R.id.main_tab_pager);
        mainTabPager.setAdapter(mainTabAdapter);
        mainTabPager.setCurrentItem(0);

        TabLayout tabLayout = root.findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mainTabPager);

        Button buttonEarnMoney = root.findViewById(R.id.button_earn_money);
        buttonEarnMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_earnMoneyFragment);
            }
        });

        return root;
    }


    public static class CardAdapter extends FragmentPagerAdapter {
        public CardAdapter(@NonNull FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CardFragmentBroker(CardFragmentBroker.TYPE_BROKER);
                case 1:
                    return new CardFragmentBroker(CardFragmentBroker.TYPE_BULLCOIN);
                case 2:
                    return new CardFragmentBroker(CardFragmentBroker.TYPE_BULLBANK);

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class MainTabAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Wallet", "News", "Bookmarks" };

        public MainTabAdapter(@NonNull FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new WalletFragment();
                case 1:
                    return new NewsFragment();
                case 2:
                    return new BookmarksFragment();

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