package com.bullcoin.app.stocktabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.maintabs.WalletFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StockStockFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_stock_stock, container, false);

        ArrayList<String> walletItems = new ArrayList<>();
        walletItems.add("Apple Inc.");
        walletItems.add("Bitcoin");
        walletItems.add("Apple Inc.");
        walletItems.add("Apple Inc.");
        walletItems.add("Apple Inc.");

        RecyclerView recyclerView = root.findViewById(R.id.recycler_stock);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        StockStockRecyclerViewAdapter adapter = new StockStockRecyclerViewAdapter(getActivity(), walletItems);
        adapter.setClickListener(new WalletFragment.WalletRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), ("You clicked on row number " + position), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }


    public static class StockStockRecyclerViewAdapter extends RecyclerView.Adapter<StockStockRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private WalletFragment.WalletRecyclerViewAdapter.ItemClickListener mClickListener;

        // data is passed into the constructor
        StockStockRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public StockStockRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.stock_recycler_row, parent, false);
            return new StockStockRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(StockStockRecyclerViewAdapter.ViewHolder holder, int position) {
            String name = mData.get(position);
            holder.name.setText(name);
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

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.stock_name);
                price = itemView.findViewById(R.id.stock_price);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        String getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        void setClickListener(WalletFragment.WalletRecyclerViewAdapter.ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}
