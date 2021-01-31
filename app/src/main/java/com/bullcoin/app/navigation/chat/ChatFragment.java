package com.bullcoin.app.navigation.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.MainActivity;
import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.datamodel.Dialogue;
import com.bullcoin.app.login.PinLoginActivity;
import com.bullcoin.app.login.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        List<Dialogue> dialogues = DataModel.get().getDialogues();

        RecyclerView recyclerView = root.findViewById(R.id.recycler_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChatFriendsRecyclerViewAdapter adapter = new ChatFriendsRecyclerViewAdapter(getActivity(), dialogues);
        adapter.setClickListener(new ChatFriendsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, Dialogue dialogue) {
                Intent intent = new Intent(getActivity(), ChatDialogueActivity.class);
                Bundle args = new Bundle();
                args.putInt("dialogueID", dialogue.getId());
                intent.putExtras(args);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }

    public static class ChatFriendsRecyclerViewAdapter extends RecyclerView.Adapter<ChatFriendsRecyclerViewAdapter.ViewHolder> {

        Context context;
        private List<Dialogue> mData;
        private LayoutInflater mInflater;
        private ChatFriendsRecyclerViewAdapter.ItemClickListener mClickListener;

        // data is passed into the constructor
        ChatFriendsRecyclerViewAdapter(Context context, List<Dialogue> data) {
            this.context = context;
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
            Dialogue dialogue = mData.get(position);
            holder.name.setText(dialogue.getName());
            String lastMsg = "";
            if (!dialogue.getMessages().isEmpty()) {
                lastMsg = dialogue.getMessages().get(dialogue.getMessages().size() - 1).text;
            }
            holder.lastMsg.setText(lastMsg);
            holder.avatar.setImageDrawable(context.getResources().getDrawable(dialogue.getIconResourceID()));
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name;
            TextView lastMsg;
            ImageView avatar;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.friend_name);
                lastMsg = itemView.findViewById(R.id.friend_last_msg);
                avatar = itemView.findViewById(R.id.friend_avatar);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, mData.get(getAdapterPosition()));
            }
        }

        // allows clicks events to be caught
        void setClickListener(ChatFriendsRecyclerViewAdapter.ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, Dialogue dialogue);
        }
    }
}