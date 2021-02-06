package com.bullcoin.app.datamodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.bullcoin.app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bullcoin.app.datamodel.DataModel.doGet;

public class Dialogue {

    int userID;
    Drawable avatar;
    String name;
    List<Message> messages;
    int start_seq;
    int last_seq;

    public static final int MESSAGES_LIMIT = 100;

    private Dialogue() {
        messages = new ArrayList<>();
    }

    public Dialogue(String name, List<Message> messages, Drawable avatar, int userID) {
        this.avatar = avatar;
        this.name = name;
        this.messages = messages;
        this.userID = userID;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void sendMessage(String text, Runnable callback) {
        new SendMessageTask(this, text, callback).execute();
    }

    public int updateMessages() {
        if (start_seq > 0) {
            List<Message> newMessages = getUserMessages(userID, -MESSAGES_LIMIT);
            messages.clear();
            messages.addAll(newMessages);
            int new_start = last_seq - MESSAGES_LIMIT;
            int inserted = newMessages.size() - 1;
            if (newMessages.size() == 0)
                inserted = 0;
            if (new_start < 0) {
                new_start = 0;
            }
            start_seq = new_start;
            return inserted;
        } else {
            List<Message> newMessages = getUserMessages(userID, last_seq + 1);
            messages.addAll(newMessages);
            return newMessages.size();
        }
    }

    class SendMessageTask extends AsyncTask<Void, Void, String> {

        Runnable callback;
        Dialogue dialogue;
        String text;

        SendMessageTask(Dialogue dialogue, String text, Runnable callback) {
            this.dialogue = dialogue;
            this.text = text;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Map<String, String> args = new HashMap<>();
                args.put("user_id", String.valueOf(DataModel.get().getUserID()));
                args.put("friend_id", String.valueOf(dialogue.userID));
                args.put("text", text);
                return DataModel.doGet("send_message", args);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (!result.equals("Ok"))
                return;

//            Message message = new Message(Message.FROM_ME, text);
//            dialogue.messages.add(message);
            callback.run();
        }
    }

    public static List<Dialogue> loadDialogues(Context context, String search) {
        Map<String, String> queryData = new HashMap<>();
        queryData.put("query", search);
        queryData.put("user_id", String.valueOf(DataModel.get().getUserID()));
        try {
            String response = doGet("get_users", queryData);
            JSONObject obj = new JSONObject(response);

            List<Dialogue> dialogues= new ArrayList<>();

            JSONArray jsonArray = obj.getJSONArray("users");
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0; i<len; i++){
                    JSONObject user = jsonArray.getJSONObject(i);

                    int userID = user.getInt("id");
                    String name = user.getString("first_name") + " " + user.getString("last_name");

                    String avatarUrl = user.getString("avatar");
                    Drawable avatar = null;
                    if (!avatarUrl.equals("")) {
                        Bitmap avatarBitmap = DataModel.getBitmapFromURL(avatarUrl);
                        if (avatarBitmap != null) {
                            avatar = new BitmapDrawable(context.getResources(), avatarBitmap);
                        }
                    }
                    if (avatar == null) {
                        avatar = context.getResources().getDrawable(R.drawable.avatar);
                    }

                    Dialogue dialogue = new Dialogue(name, null, avatar, userID);
                    dialogue.messages = dialogue.getUserMessages(userID, -1);
                    dialogue.start_seq = dialogue.last_seq;
                    dialogues.add(dialogue);
                }
            }

            return dialogues;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Message> getUserMessages(int userID, int fromN) {
        int myID = DataModel.get().getUserID();

        Map<String, String> queryData = new HashMap<>();
        queryData.put("user_id", String.valueOf(myID));
        queryData.put("friend_id", String.valueOf(userID));
        queryData.put("from_n", String.valueOf(fromN));

        try {
            String response = doGet("get_messages", queryData);
            JSONObject obj = new JSONObject(response);

            List<Message> messages = new ArrayList<>();

            JSONArray jsonArray = obj.getJSONArray("messages");
            if (jsonArray == null) {
                return messages;
            }

            int len = jsonArray.length();
            for (int i = 0; i < len; i++){
                JSONObject messageObj = jsonArray.getJSONObject(i);

                int fromID = messageObj.getInt("from");
                String text = messageObj.getString("text");
                int source = fromID == myID ? Message.FROM_ME : Message.FROM_FRIEND;
                Message message = new Message(source, text);
                messages.add(message);
            }

            last_seq = obj.getInt("count") - 1;

            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
