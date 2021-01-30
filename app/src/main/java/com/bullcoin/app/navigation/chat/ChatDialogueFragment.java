package com.bullcoin.app.navigation.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.Message;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatDialogueFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_messages, container, false);

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new Message(Message.FROM_FRIEND, "fdssdfkllk lklk lklklksdlkflksdlkflk lkslkldkflk lklk lk lkslkdflklk s;;'lA'WSD[QOWFD;L KLK;LK;A ;LK;LK"));
        messages.add(new Message(Message.FROM_FRIEND, "Ok"));
        messages.add(new Message(Message.FROM_ME, "falafsdfklj;afasjklfasjklasfljkflasjkjkflsadljkfasljkfsaljkfasdljkfasd lajsfljka ljkaljksd fljkaljks ljkfljk ljkljkljkljkasljkd ljkjkl ljkaljkljk  jklljkaljksldjkljkfljk ljkl jkljkalkjsdljk "));
        messages.add(new Message(Message.FROM_FRIEND, "Ok"));
        messages.add(new Message(Message.FROM_ME, "Why did I even throw this to you?"));
        messages.add(new Message(Message.FROM_FRIEND, "What did you send me?"));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        ChatMessagesRecyclerViewAdapter adapter = new ChatMessagesRecyclerViewAdapter(getActivity(), messages);

        recyclerView.setAdapter(adapter);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                retutnBack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ImageButton backButton = root.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retutnBack();
            }
        });

        return root;
    }

    public void retutnBack() {
        BottomNavigationView nav_view = getActivity().findViewById(R.id.nav_view);
        nav_view.setVisibility(View.VISIBLE);
        Navigation.findNavController(getView()).navigateUp();
    }


    public static class ChatMessagesRecyclerViewAdapter extends RecyclerView.Adapter {

        private List<Message> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        ChatMessagesRecyclerViewAdapter(Context context, List<Message> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;

            switch (viewType) {
                case Message.FROM_ME:
                    view = mInflater.inflate(R.layout.recycler_chat_row_me, parent, false);
                    return new FromMeViewHolder(view);
                case Message.FROM_FRIEND:
                    view = mInflater.inflate(R.layout.recycler_chat_row_friend, parent, false);
                    return new FromFriendViewHolder(view);
            }
            return null;
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Message message = mData.get(position);
            switch (message.source) {
                case Message.FROM_ME:
                    ((FromMeViewHolder) holder).message.setText(message.text);
                    break;
                case Message.FROM_FRIEND:
                    ((FromFriendViewHolder) holder).message.setText(message.text);
                    break;
            }
        }

        // total number of rows
        @Override
        public int getItemCount() {
            if (mData == null)
                return 0;
            return mData.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
                Message message = mData.get(position);
                if (message != null) {
                    return message.source;
                }
            }
            return 0;
        }

        public class FromMeViewHolder extends RecyclerView.ViewHolder {
            TextView message;

            FromMeViewHolder(View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.message);
            }
        }

        public class FromFriendViewHolder extends RecyclerView.ViewHolder {
            TextView message;
            ImageView friend_avatar;

            FromFriendViewHolder(View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.message);
                friend_avatar = itemView.findViewById(R.id.friend_avatar);
            }
        }
    }
}
