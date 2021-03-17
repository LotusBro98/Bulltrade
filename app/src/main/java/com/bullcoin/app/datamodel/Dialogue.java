package com.bullcoin.app.datamodel;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;

import com.bullcoin.app.R;
import com.bullcoin.app.navigation.chat.ChatDialogueActivity;

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
    public boolean unread;

    public static final int MESSAGES_LIMIT = 100;
    private static final int NOTIFY_ID = 192;

    private Dialogue() {
        messages = new ArrayList<>();
        unread = false;
    }

    Message getLastMessage() {
        if (messages.size() == 0) {
            return null;
        } else {
            return messages.get(messages.size()-1);
        }
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

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public Bitmap getRoundAvatar() {
        if (avatar instanceof BitmapDrawable) {
            return getCircleBitmap(((BitmapDrawable) avatar).getBitmap());
        } else {
            return null;
        }
    }

    public void sendNotification(Context context) {
        try {
            Message message = getLastMessage();

            if (message.source == Message.FROM_ME) {
                return;
            }

            Intent intent = new Intent(context, ChatDialogueActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Bundle args = new Bundle();
            args.putInt("userID", getUserID());
            args.putBoolean("fromNotification", true);
            intent.putExtras(args);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                    .setSmallIcon(R.drawable.ic_chat)
                    .setContentTitle(context.getString(R.string.notification_title) + " " + getName())
                    .setContentText(message.text)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Bitmap roundAvatar = getRoundAvatar();
            if (roundAvatar != null) {
                builder.setLargeIcon(roundAvatar);
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFY_ID, builder.build());
            unread = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void updateDialogues(Context context, String search, boolean notify) {
        Map<String, String> queryData = new HashMap<>();
        queryData.put("query", search);
        queryData.put("user_id", String.valueOf(DataModel.get().getUserID()));
        try {
            String response = doGet("get_users", queryData);
            JSONObject obj = new JSONObject(response);

            JSONArray jsonArray = obj.getJSONArray("users");
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0; i<len; i++){
                    JSONObject user = jsonArray.getJSONObject(i);


                    int userID = user.getInt("id");
                    Dialogue oldDialogue = DataModel.get().getDialogue(userID);
                    if (oldDialogue != null) {
                        continue;
                    }

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
                    DataModel.get().getDialogues().add(dialogue);
                    if (notify) {
                        dialogue.sendNotification(context);
                    }
                }
            }

            for (Dialogue dialogue : DataModel.get().getDialogues()) {
                ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if (cn.getClassName().equals("com.bullcoin.app.navigation.chat.ChatDialogueActivity") && DataModel.get().activeDialogue.userID == dialogue.userID) {
                    continue;
                }

                int inserted = dialogue.updateMessages();
                if (inserted > 0 && notify) {
                    dialogue.sendNotification(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                String time = messageObj.getString("time");
                Message message = new Message(source, text, time);
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
