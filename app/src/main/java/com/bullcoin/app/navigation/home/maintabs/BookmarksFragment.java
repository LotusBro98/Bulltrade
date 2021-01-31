package com.bullcoin.app.navigation.home.maintabs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.datamodel.News;

import java.util.List;

public class BookmarksFragment extends Fragment {

    RecyclerView recyclerView;
    NewsRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        setRetainInstance(true);

        List<News> ownedAssets = DataModel.get().getSelectedNews();

        recyclerView = view.findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NewsRecyclerViewAdapter(getActivity(), ownedAssets, this);

        recyclerView.setAdapter(adapter);

        return view;
    }

    public void reload() {
        adapter.mData = DataModel.get().getSelectedNews();
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

        private List<News> mData;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;
        Context context;
        Fragment fragment;

        // data is passed into the constructor
        NewsRecyclerViewAdapter(Context context, List<News> data, Fragment fragment) {
            this.context = context;
            this.fragment = fragment;
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_row_news, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            News news = mData.get(position);
            holder.source.setText(news.getSource());
            holder.text.setText(news.getText());
            holder.icon.setImageDrawable(context.getResources().getDrawable(news.getIconResourceID()));
            holder.checked.setOnCheckedChangeListener(null);
            holder.checked.setChecked(news.isSelected());

            holder.checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e("CLICKED2", String.valueOf(isChecked));
                    news.setSelected(context, isChecked);
                    for (Fragment fragment : fragment.getParentFragmentManager().getFragments()) {
                        if (fragment instanceof BookmarksFragment) {
                            ((BookmarksFragment) fragment).reload();
                        } else if (fragment instanceof NewsFragment) {
                            ((NewsFragment) fragment).reload();
                        }
                    }
                }
            });
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView source;
            TextView time;
            TextView text;
            ImageView icon;
            ToggleButton checked;

            ViewHolder(View itemView) {
                super(itemView);
                source = itemView.findViewById(R.id.news_who);
                time = itemView.findViewById(R.id.news_when);
                text = itemView.findViewById(R.id.news_text);
                icon = itemView.findViewById(R.id.news_icon);
                checked = itemView.findViewById(R.id.news_bookmark);
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
            void onItemClick(View view, News news);
        }
    }
}
