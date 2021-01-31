package com.bullcoin.app.navigation.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.datamodel.Message;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ChatDialogueActivity extends AppCompatActivity {

    EditText editMessage;
    RecyclerView recyclerView;
    ChatMessagesRecyclerViewAdapter adapter;
    boolean inserted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat_messages);

        List<Message> messages = DataModel.get().getMessages();

        recyclerView = findViewById(R.id.recycler_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMessagesRecyclerViewAdapter(this, messages);

        recyclerView.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnBack();
            }
        });

        editMessage = findViewById(R.id.edit_message);

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(this::onSend);

        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            onSend(v);
            return true;
        });
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void onSend(View v) {
        String text = editMessage.getText().toString();
        if (text.equals("")) {
            return;
        }
        Message message = new Message(Message.FROM_ME, text);
        editMessage.getText().clear();
        adapter.mData.add(message);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        adapter.notifyItemInserted(adapter.getItemCount() - 1);
    }

    public void returnBack() {
        finish();
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
