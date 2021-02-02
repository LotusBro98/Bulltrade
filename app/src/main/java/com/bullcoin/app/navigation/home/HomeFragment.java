package com.bullcoin.app.navigation.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

    MainTabAdapter mainTabAdapter;
    CardAdapter cardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        cardAdapter = new CardAdapter(getChildFragmentManager());

        ViewPager cardPager = root.findViewById(R.id.card_pager);
        cardPager.setAdapter(cardAdapter);
        cardPager.setCurrentItem(0);

        TabLayout cardTabs = root.findViewById(R.id.card_tabs);
        cardTabs.setupWithViewPager(cardPager);

        mainTabAdapter = new MainTabAdapter(getChildFragmentManager(), getContext());

        ViewPager mainTabPager = root.findViewById(R.id.main_tab_pager);
        mainTabPager.setAdapter(mainTabAdapter);
        mainTabPager.setCurrentItem(0);

        TabLayout tabLayout = root.findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mainTabPager);

        Button buttonEarnMoney = root.findViewById(R.id.button_earn_money);
        buttonEarnMoney.setOnClickListener(
            v -> Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_earnMoneyFragment)
        );

        root.findViewById(R.id.button_collect_friends).setOnClickListener(
            v -> Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_friendsFragment)
        );

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
        private String[] tabTitles;

        public MainTabAdapter(@NonNull FragmentManager fragmentManager, Context context) {
            super(fragmentManager);
            tabTitles = new String[] {
                    context.getString(R.string.wallet),
                    context.getString(R.string.news),
                    context.getString(R.string.bookmarks)
            };
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