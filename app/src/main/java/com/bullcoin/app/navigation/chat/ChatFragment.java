package com.bullcoin.app.navigation.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.MainActivity;
import com.bullcoin.app.R;
import com.bullcoin.app.login.PinLoginActivity;
import com.bullcoin.app.login.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        ArrayList<String> walletItems = new ArrayList<>();
        walletItems.add("Max Spenser");
        walletItems.add("Max Spenser");
        walletItems.add("Max Spenser");
        walletItems.add("Max Spenser");
        walletItems.add("Max Spenser");

        RecyclerView recyclerView = root.findViewById(R.id.recycler_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChatFriendsRecyclerViewAdapter adapter = new ChatFriendsRecyclerViewAdapter(getActivity(), walletItems);
        adapter.setClickListener(new ChatFriendsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ChatDialogueActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }

    public static class ChatFriendsRecyclerViewAdapter extends RecyclerView.Adapter<ChatFriendsRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private ChatFriendsRecyclerViewAdapter.ItemClickListener mClickListener;

        // data is passed into the constructor
        ChatFriendsRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ChatFriendsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_chat_friend_row, parent, false);
            return new ChatFriendsRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ChatFriendsRecyclerViewAdapter.ViewHolder holder, int position) {
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

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.friend_name);
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
        void setClickListener(ChatFriendsRecyclerViewAdapter.ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}