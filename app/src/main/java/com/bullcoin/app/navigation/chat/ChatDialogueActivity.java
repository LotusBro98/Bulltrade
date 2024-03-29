package com.bullcoin.app.navigation.chat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.LocalizedActivity;
import com.bullcoin.app.MainActivity;
import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.datamodel.Dialogue;
import com.bullcoin.app.datamodel.Message;
import com.bullcoin.app.login.PinLoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ChatDialogueActivity extends LocalizedActivity {

    EditText editMessage;
    RecyclerView recyclerView;
    ChatMessagesRecyclerViewAdapter adapter;
    boolean inserted = false;
    boolean fromNotification;

    Dialogue dialogue;

    UpdateTask updateTask;
    Runnable updateRunnable;
    Handler updateHandler;

    private static final int UPDATE_PERIOD = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat_messages);

        Bundle args = getIntent().getExtras();
        int dialogueID = args.getInt("userID");
        fromNotification = args.getBoolean("fromNotification");
        dialogue = DataModel.get().getDialogue(dialogueID);

        TextView name = findViewById(R.id.friend_name);
        ImageView avatar = findViewById(R.id.friend_avatar);

        name.setText(dialogue.getName());
        avatar.setImageDrawable(dialogue.getAvatar());

        recyclerView = findViewById(R.id.recycler_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatMessagesRecyclerViewAdapter(this, dialogue);

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

        updateHandler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                new UpdateTask().execute();

            }
        };
        updateRunnable.run();

        DataModel.get().activeDialogue = dialogue;

        dialogue.unread = false;

        ImageView background = findViewById(R.id.chat_bg);
        background.setImageDrawable(DataModel.get().getChat_bg());

        if (dialogue.isBlocked()) {
            View bottomView = findViewById(R.id.view2);
            bottomView.setVisibility(View.GONE);
            editMessage.setVisibility(View.GONE);
            buttonSend.setVisibility(View.GONE);
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, Integer>{
        @Override
        protected Integer doInBackground(java.lang.Void... voids) {
            return dialogue.updateMessages();
        }

        @Override
        protected void onPostExecute(Integer inserted) {
            if (inserted == 1) {
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } else if (inserted != 0) {
                adapter.notifyDataSetChanged();
            }

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                updateHandler.postDelayed(updateRunnable, UPDATE_PERIOD);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
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
        editMessage.getText().clear();

        dialogue.sendMessage(text, new Runnable() {
            @Override
            public void run() {
                updateHandler.post(updateRunnable);
//                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                adapter.notifyItemInserted(adapter.getItemCount() - 1);
            }
        });
    }

    private void deleteMessage(Message message)
    {
        dialogue.deleteMessage(message, new Runnable() {
            @Override
            public void run() {
                int index = adapter.mData.indexOf(message);
                adapter.mData.remove(index);
                adapter.notifyItemRemoved(index);
                recyclerView.scrollToPosition(index);
            }
        });
    }

    public void returnBack() {
        if (fromNotification) {
            Intent intent = new Intent(ChatDialogueActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if (fromNotification) {
            Intent intent = new Intent(ChatDialogueActivity.this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Message message = null;
        for (Message m : adapter.mData) {
            if (m.seq == item.getItemId()) {
                message = m;
            }
        }
        if (message == null) {
            return super.onContextItemSelected(item);
        }

        deleteMessage(message);

        return super.onContextItemSelected(item);
    }

    public static class ChatMessagesRecyclerViewAdapter extends RecyclerView.Adapter {

        Context context;
        Dialogue dialogue;
        private List<Message> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        ChatMessagesRecyclerViewAdapter(Context context, Dialogue dialogue) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mData = dialogue.getMessages();
            this.dialogue = dialogue;
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
            ((MessageViewHolder) holder).message.setText(message.text);
            ((MessageViewHolder) holder).time.setText(message.getTime());
            ((MessageViewHolder) holder).messageObj = message;
            switch (message.source) {
                case Message.FROM_ME:
                    break;
                case Message.FROM_FRIEND:
                    ((FromFriendViewHolder) holder).friend_avatar.setImageDrawable(dialogue.getAvatar());
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

        public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            TextView message;
            TextView time;
            Message messageObj;

            MessageViewHolder(View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.message);
                time = itemView.findViewById(R.id.send_time);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, messageObj.seq, 0, context.getString(R.string.message_delete));
            }
        }

        public class FromMeViewHolder extends MessageViewHolder {
            FromMeViewHolder(View itemView) {
                super(itemView);
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        public class FromFriendViewHolder extends MessageViewHolder {
            ImageView friend_avatar;

            FromFriendViewHolder(View itemView) {
                super(itemView);
                friend_avatar = itemView.findViewById(R.id.friend_avatar);
                itemView.setOnCreateContextMenuListener(this);
            }
        }
    }
}
