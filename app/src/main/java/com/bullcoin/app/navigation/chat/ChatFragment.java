package com.bullcoin.app.navigation.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bullcoin.app.MainActivity;
import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;
import com.bullcoin.app.datamodel.Dialogue;
import com.bullcoin.app.datamodel.Updater;
import com.bullcoin.app.login.PinLoginActivity;
import com.bullcoin.app.login.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {

    ChatFriendsRecyclerViewAdapter adapter;

    private static final int PICK_FROM_GALLERY = 2;

    private ImageView background;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        List<Dialogue> dialogues = DataModel.get().getDialogues();

        RecyclerView recyclerView = root.findViewById(R.id.recycler_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ChatFriendsRecyclerViewAdapter(getActivity(), dialogues);
        adapter.setClickListener(new ChatFriendsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, Dialogue dialogue) {
                Intent intent = new Intent(getActivity(), ChatDialogueActivity.class);
                Bundle args = new Bundle();
                args.putInt("userID", dialogue.getUserID());
                intent.putExtras(args);
                startActivity(intent);
            }
        });

        EditText searchForFriends = root.findViewById(R.id.search_for_friends);
        searchForFriends.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//        searchForFriends.addTextChangedListener(this);
        searchForFriends.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String search = searchForFriends.getText().toString();
                DataModel.get().loadDialogues(getContext(), search, new Runnable() {
                    @Override
                    public void run() {
                        adapter.mData = DataModel.get().getDialogues();
                        adapter.notifyDataSetChanged();
                    }
                });
                return false;
            }
        });

        recyclerView.setAdapter(adapter);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.cancelAll();

        root.findViewById(R.id.button_change_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                        photoPickerIntent.putExtra("return-data", true);
//                        photoPickerIntent.putExtra("crop", "true");
//                        photoPickerIntent.putExtra("scale", true);
//                        photoPickerIntent.putExtra("aspectX", root.getWidth());
//                        photoPickerIntent.putExtra("aspectY", root.getHeight());
//                        float scale = 0.5f;
//
//                        photoPickerIntent.putExtra("outputX", (int)(scale * root.getWidth()));
//                        photoPickerIntent.putExtra("outputY", (int)(scale * root.getHeight()));

                        startActivityForResult(photoPickerIntent, PICK_FROM_GALLERY);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        background = root.findViewById(R.id.chat_bg);
        background.setImageDrawable(DataModel.get().getChat_bg());

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            try {
                Bitmap photo = extras.getParcelable("data");

                DataModel.get().setChatBG(getContext(), photo);
                background.setImageDrawable(DataModel.get().getChat_bg());
            } catch (Exception e0) {
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);

                    DataModel.get().setChatBG(getContext(), photo);
                    background.setImageDrawable(DataModel.get().getChat_bg());
                } catch (Exception e) {
                    e0.printStackTrace();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!DataModel.get().lastSearch.equals("")) {
            Updater.instance.stop();
            DataModel.get().loadDialogues(getContext(), "", new Runnable() {
                @Override
                public void run() {
                    adapter.mData = DataModel.get().getDialogues();
                    adapter.notifyDataSetChanged();
                    Updater.instance.start();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        DataModel.get().loadDialogues(getContext(), "", new Runnable() {
//            @Override
//            public void run() {
                adapter.mData = DataModel.get().getDialogues();
                adapter.notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Dialogue dialogue = DataModel.get().getDialogue(item.getItemId());
        if (dialogue == null) {
            return super.onContextItemSelected(item);
        }

        if (item.getGroupId() == 0) {
            dialogue.unblockUser();
        } else if (item.getGroupId() == 1) {
            dialogue.blockUser();
        } else {
            dialogue.hideChat();
            adapter.notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);
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
            holder.avatar.setImageDrawable(dialogue.getAvatar());
            holder.dialogue = dialogue;
            if (dialogue.unread) {
                holder.unread.setVisibility(View.VISIBLE);
            } else {
                holder.unread.setVisibility(View.INVISIBLE);
            }
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
            TextView name;
            TextView lastMsg;
            ImageView avatar;
            ImageButton unread;
            Dialogue dialogue;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.friend_name);
                lastMsg = itemView.findViewById(R.id.friend_last_msg);
                avatar = itemView.findViewById(R.id.friend_avatar);
                unread = itemView.findViewById(R.id.image_unread);
                itemView.setOnClickListener(this);
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (dialogue.canUnblock()) {
                    menu.add(0, dialogue.getUserID(), 0, context.getString(R.string.dialogue_unblock));
                } else if (dialogue.canBlock()) {
                    menu.add(1, dialogue.getUserID(), 0, context.getString(R.string.dialogue_block));
                }
                menu.add(2, dialogue.getUserID(), 0, R.string.delete_chat);
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