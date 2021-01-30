package com.bullcoin.app.navigation.stock.stocktabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Asset;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.navigation.AssetFragment;

import java.util.List;

public class ShareFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stock_stock, container, false);

        List<Asset> assets = DataModel.get().getAssets(Asset.TYPE_SHARE);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_stock);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ShareRecyclerViewAdapter adapter = new ShareRecyclerViewAdapter(getActivity(), assets);
        adapter.setClickListener(new ShareRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, Asset asset) {
                AssetFragment.navigateHere(view, R.id.action_navigation_stock_to_assetFragment, asset);
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }

    public static class ShareRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecyclerViewAdapter.ViewHolder> {

        private List<Asset> mData;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;
        private Context context;

        // data is passed into the constructor
        ShareRecyclerViewAdapter(Context context, List<Asset> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ShareRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_row_share, parent, false);
            return new ShareRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ShareRecyclerViewAdapter.ViewHolder holder, int position) {
            Asset asset = mData.get(position);
            holder.name.setText(asset.getName());
            holder.price.setText("$" + String.valueOf(asset.getPrice()));
            holder.percent.setText(String.valueOf(asset.getPercent()) +  "%");
            holder.icon.setImageDrawable(context.getResources().getDrawable(asset.getIconResourceID()));
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
            TextView percent;
            ImageView icon;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.share_name);
                price = itemView.findViewById(R.id.share_price);
                percent = itemView.findViewById(R.id.share_percent);
                icon = itemView.findViewById(R.id.share_image);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, mData.get(getAdapterPosition()));
            }
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
