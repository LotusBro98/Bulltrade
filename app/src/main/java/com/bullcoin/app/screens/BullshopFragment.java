package com.bullcoin.app.screens;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.datamodel.News;
import com.bullcoin.app.navigation.home.maintabs.BookmarksFragment;
import com.bullcoin.app.navigation.home.maintabs.NewsFragment;

import java.util.Arrays;
import java.util.List;

public class BullshopFragment extends Fragment {

    public class ShopItem {
        public String name;
        public String oldPrice;
        public String newPrice;
        public int iconResourceID;

        public ShopItem(String name, String oldPrice, String newPrice, int iconResourceID) {
            this.name = name;
            this.oldPrice = oldPrice;
            this.newPrice = newPrice;
            this.iconResourceID = iconResourceID;
        }
    }

    private ShopItem[] shopItems = {
//          new ShopItem("BULLTRADE Jacket", "€79.90", "€69.90", R.drawable.bulltrade_jacket),
            new ShopItem("BULLTRADE Cap", "€24.90", "€19.90", R.drawable.bulltrade_cap),
            new ShopItem("BULLTRADE Polo Shirt", "29.90", "€24.90", R.drawable.otstoinaya_futbolka),
            new ShopItem("BULLTRADE Hoody black", "€79.90", "€69.90", R.drawable.bulltrade_hoody_black),
            new ShopItem("BULLTRADE Cup", "€20.90", "€14.90", R.drawable.bulltrade_cup),
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bullshop, container, false);

        setRetainInstance(true);

        List<News> ownedAssets = DataModel.get().getNews();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BullshopRecyclerViewAdapter adapter = new BullshopRecyclerViewAdapter(getActivity(), Arrays.asList(shopItems), this);

        recyclerView.setAdapter(adapter);

        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());

        TextView bulltradeLink = view.findViewById(R.id.bulltrade_link);
        bulltradeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bull-trade.net"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

    public static class BullshopRecyclerViewAdapter extends RecyclerView.Adapter<BullshopRecyclerViewAdapter.ViewHolder> {

        private List<ShopItem> mData;
        private LayoutInflater mInflater;
        private NewsFragment.NewsRecyclerViewAdapter.ItemClickListener mClickListener;
        Context context;
        Fragment fragment;

        // data is passed into the constructor
        BullshopRecyclerViewAdapter(Context context, List<ShopItem> data, Fragment fragment) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.fragment = fragment;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_row_bullshop, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ShopItem item = mData.get(position);
            holder.name.setText(item.name);
            holder.oldPrice.setText(item.oldPrice);
            holder.newPrice.setText(item.newPrice);
            Drawable drawable = context.getResources().getDrawable(item.iconResourceID);
            holder.image.setImageDrawable(drawable);
            holder.image.requestLayout();
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView oldPrice;
            TextView newPrice;
            ImageView image;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.item_name);
                oldPrice = itemView.findViewById(R.id.old_price);
                newPrice = itemView.findViewById(R.id.new_price);
                image = itemView.findViewById(R.id.item_image);
            }
        }
    }
}
