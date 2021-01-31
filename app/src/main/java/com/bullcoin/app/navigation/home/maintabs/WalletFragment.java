package com.bullcoin.app.navigation.home.maintabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.navigation.AssetFragment;

import java.util.List;

public class WalletFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        setRetainInstance(true);

        List<Asset> ownedAssets = DataModel.get().getOwnedAssets();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_wallet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        WalletRecyclerViewAdapter adapter = new WalletRecyclerViewAdapter(getActivity(), ownedAssets);
        adapter.setClickListener(new WalletRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, Asset asset) {
                AssetFragment.navigateHere(view, R.id.action_navigation_home_to_assetFragment, asset);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }


    public static class WalletRecyclerViewAdapter extends RecyclerView.Adapter<WalletRecyclerViewAdapter.ViewHolder> {

        private List<Asset> mData;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;
        Context context;

        // data is passed into the constructor
        WalletRecyclerViewAdapter(Context context, List<Asset> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_row_wallet, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Asset asset = mData.get(position);
            holder.name.setText(asset.getName());
            holder.image.setImageDrawable(context.getResources().getDrawable(asset.getIconResourceID()));
            holder.price.setText("$" + String.valueOf(asset.getPrice()));
            holder.quantity.setText("quantity x" + String.valueOf(asset.getOwned()));
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView price;
            TextView quantity;
            ImageView image;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.stock_name);
                price = itemView.findViewById(R.id.wallet_price);
                quantity = itemView.findViewById(R.id.wallet_quantity);
                image = itemView.findViewById(R.id.wallet_image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, mData.get(getAdapterPosition()));
            }
        }

        // convenience method for getting data at click position
        Asset getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, Asset asset);
        }
    }
}
