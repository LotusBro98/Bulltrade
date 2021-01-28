package com.bullcoin.app.navigation.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ArrayList<String> walletItems = new ArrayList<>();
        walletItems.add("Today good day to invest in some company.");
        walletItems.add("Russia resumes international flights from three more cities.");
        walletItems.add("Sample Text");

        RecyclerView recyclerView = root.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PostsRecyclerViewAdapter adapter = new PostsRecyclerViewAdapter(getActivity(), walletItems);
        adapter.setClickListener(new PostsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), ("You clicked on row number " + position), Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton settingsButton = root.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_navigation_profile_to_settingsFragment);
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }


    public static class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private PostsRecyclerViewAdapter.ItemClickListener mClickListener;

        // data is passed into the constructor
        PostsRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public PostsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_row_post, parent, false);
            return new PostsRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(PostsRecyclerViewAdapter.ViewHolder holder, int position) {
            String text = mData.get(position);
            holder.text.setText(text);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView text;
            TextView time;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.post_name);
                time = itemView.findViewById(R.id.post_time);
                text = itemView.findViewById(R.id.post_text);
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
        void setClickListener(PostsRecyclerViewAdapter.ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}
