package com.bullcoin.app.datamodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.bullcoin.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class DataModel {
    private String userFirstName;
    private String userLastName;
    private double brokerBalance;
    private double bankBalance;
    private List<Asset> assets;
    private List<Card> cards;
    private List<Dialogue> dialogues;
    private List<News> news;
    private Updater updater;
    public Dialogue activeDialogue;
    public String lastSearch = "";

    Drawable avatar;
    Drawable chat_bg;

    Dialogue tradingAssistant;
    public static final int TRADING_ASSISTANT_ID = -228;

    public Dialogue getTradingAssistant() {
        return tradingAssistant;
    }

    private static final String BASE_URL = "http://82.148.29.197:8000/";

    private int userID;

    private static DataModel instance;

    public static DataModel get() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new DataModel(context);

    }

    public Drawable getAvatar() {
        return avatar;
    }

    public Drawable getChat_bg() {
        return chat_bg;
    }

    private void setUserID(Context context, int userID) {
        this.userID = userID;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("userID", userID);
        editor.commit();
    }

    public DataModel(Context context) {
        try {
            assets = Asset.parseFromXML(context);
        } catch (Exception e) {
            Log.e("ASSET_PARSING", "Failed to parse assets");
            e.printStackTrace();
            assets = new ArrayList<>();
        }

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(Message.FROM_FRIEND, context.getString(R.string.how_can_i_help_you), ""));
        tradingAssistant = new Dialogue(context.getString(R.string.trading_assistant), messages, context.getResources().getDrawable(R.drawable.max_spencer), TRADING_ASSISTANT_ID);

        cards = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userFirstName = preferences.getString("firstName", "");
        userLastName = preferences.getString("lastName", "");
        brokerBalance = preferences.getFloat("brokerBalance", 10000.0f);
        bankBalance = preferences.getFloat("bankBalance", 254.0f);
        userID = preferences.getInt("userID", -1);

        boolean hasCard1 = preferences.getBoolean("hasCard1", false);
        boolean hasCard2 = preferences.getBoolean("hasCard2", false);
        if (hasCard1) {
            cards.add(new Card(Card.CARD_BULLTRADE));
        }
        if (hasCard2) {
            cards.add(new Card(Card.CARD_BULLBANK));
        }

        dialogues = new ArrayList<>();

        try {
            news = News.parseFromXML(context);
        } catch (Exception e) {
            Log.e("ASSET_PARSING", "Failed to parse news");
            news = new ArrayList<>();
            e.printStackTrace();
        }

        loadAvatar(context);
        loadChatBG(context);

        updater = new Updater(context.getApplicationContext());
        updater.start();
    }

    public static void loadDialogues(Context context, String search, Runnable callback) {
        new AsyncTask<Void, String, List<Dialogue>>() {
            @Override
            protected List<Dialogue> doInBackground(Void... voids) {
                try {
                    DataModel.get().lastSearch = search;
                    return Dialogue.loadDialogues(context, search);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(final List<Dialogue> result) {
                DataModel.get().dialogues = new ArrayList<>(result);
                if (callback != null) {
                    callback.run();
                }
            }
        }.execute();
    }

    public static void updateDialogues(Context context, String search, boolean notify) {
        try {
            DataModel.get().lastSearch = search;
            Dialogue.updateDialogues(context, search, notify);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public int getUserID() {
        return userID;
    }

    public void setAvatar(Context context, Bitmap bitmap) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("avatar", encodeTobase64(bitmap));
        editor.commit();
        avatar = new BitmapDrawable(context.getResources(), bitmap);

        sendAvatar(getUserID(), bitmap);
    }

    public void setChatBG(Context context, Bitmap bitmap) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("chat_bg", encodeTobase64(bitmap));
        editor.commit();

        chat_bg = new BitmapDrawable(context.getResources(), bitmap);
    }

    private static void sendAvatar(int userID, Bitmap bitmap) {
        Map<String, String> args = new HashMap<>();
        args.put("user_id", String.valueOf(userID));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                    s = doPost("set_avatar", args, bos.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }

            @Override
            protected void onPostExecute(final String result) {
            }
        }.execute();
    }

    private void loadAvatar(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String avatarStr = preferences.getString("avatar", "");
        if (avatarStr.equals("")) {
            avatar = context.getResources().getDrawable(R.drawable.avatar);
        } else {
            avatar = new BitmapDrawable(context.getResources(), decodeBase64(avatarStr));
        }
    }

    private void loadChatBG(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String chat_bgStr = preferences.getString("chat_bg", "");
        if (chat_bgStr.equals("")) {
            chat_bg = null;
        } else {
            chat_bg = new BitmapDrawable(context.getResources(), decodeBase64(chat_bgStr));
        }
    }

    public List<News> getNews() {
        return news;
    }

    public List<News> getSelectedNews() {
        List<News> news = new ArrayList<>();
        for (News news1: this.news ) {
            if (news1.isSelected())
                news.add(news1);
        }
        return news;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Dialogue> getDialogues() {
        return dialogues;
    }

    public Dialogue getDialogue(int userID) {
        for (Dialogue dialogue : dialogues) {
            if (dialogue.userID == userID) {
                return dialogue;
            }
        }
        return null;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName, Context context) {
        this.userFirstName = userFirstName;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firstName", userFirstName); // value to store
        editor.commit();
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName, Context context) {
        this.userLastName = userLastName;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastName", userLastName); // value to store
        editor.commit();
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<Asset> getAssets(int type) {
        List<Asset> assets = new ArrayList<>();
        for (Asset asset: this.assets ) {
            if (asset.getType() == type)
                assets.add(asset);
        }
        return assets;
    }

    public List<Asset> getOwnedAssets() {
        List<Asset> assets = new ArrayList<>();
        for (Asset asset: this.assets ) {
            if (asset.getOwned() > 0)
                assets.add(asset);
        }
        return assets;
    }

    public Asset getAsset(String name) {
        for (Asset asset: this.assets ) {
            if (asset.getName().equals(name))
                return asset;
        }
        return null;
    }

    public boolean buyAsset(Context context, Asset asset, int quantity) {
        if (brokerBalance - asset.getPrice() * quantity >= 0) {
            asset.setOwned(context, asset.getOwned() + quantity);
            setBrokerBalance(context, brokerBalance - asset.getPrice() * quantity);
            Toast.makeText(context, context.getString(R.string.you_have_bought) + asset.getName() + " x" + quantity, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, context.getString(R.string.not_enough_money), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean sellAsset(Context context, Asset asset, int quantity) {
        if (asset.getOwned() >= quantity) {
            asset.setOwned(context, asset.getOwned() - quantity);
            setBrokerBalance(context, getBrokerBalance() + asset.getPrice() * quantity);
            Toast.makeText(context, context.getString(R.string.you_have_sold) + asset.getName() + " x" + quantity, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, context.getString(R.string.not_enough) + asset.getName(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public int getBullcoinBalance() {
        for (Asset asset: this.assets ) {
            if (asset.getName().equals("Bullcoin"))
                return asset.getOwned();
        }
        return 0;
    }

    public double getBrokerBalance() {
        return brokerBalance;
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void setBrokerBalance(Context context, double brokerBalance) {
        this.brokerBalance = brokerBalance;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("brokerBalance", (float) this.brokerBalance); // value to store
        editor.commit();
    }

    public static String doGet(String url, Map<String, String> args) throws Exception {

        String get_url = BASE_URL + url + "?";

        for (Map.Entry<String, String> entry : args.entrySet()) {
            get_url += URLEncoder.encode(entry.getKey(), "utf-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8") + "&";
        }
        if (get_url.charAt(get_url.length() - 1) == '&') {
            get_url = get_url.substring(0, get_url.length()-1);
        }

        Log.d("SERVER_GET", "Sending GET request: " + get_url);

        java.net.URL obj = new URL(get_url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        Log.d("SERVER_GET","Response string: " + response.toString());

        return response.toString();
    }

    public static String doPost(String url, Map<String, String> args, byte[] data) throws Exception {
        String get_url = BASE_URL + url + "?";

        for (Map.Entry<String, String> entry : args.entrySet()) {
            get_url += URLEncoder.encode(entry.getKey(), "utf-8") + "=" + URLEncoder.encode(entry.getValue(), "utf-8") + "&";
        }
        if (get_url.charAt(get_url.length() - 1) == '&') {
            get_url = get_url.substring(0, get_url.length()-1);
        }

        String dataEncoded = Base64.encodeToString(data, Base64.DEFAULT);
        String query = "data=" + URLEncoder.encode(dataEncoded, "utf-8");

        Log.d("SERVER_POST", "Sending POST request: " + get_url);
        Log.d("SERVER_POST", dataEncoded.substring(0, 100) + " ... " + dataEncoded.substring(dataEncoded.length() - 100));

        URL url_obj = new URL(get_url);
        HttpURLConnection conn = (HttpURLConnection) url_obj.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);

        writer.flush();
        writer.close();
        os.close();
        int responseCode = conn.getResponseCode();

        String response = "";
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        }
        else {
            response="";
        }

        Log.d("SERVER_POST", "Response: " + response);

        return response;
    }


    public static void register(Context context, Runnable callback,
            String phone,
            String language,
            String email,
            String country,
            String password,
            String firstName,
            String lastName
    )
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();

        editor.putString("phone", phone);
        editor.putString("language", language);
        editor.putString("email", email);
        editor.putString("country", country);
        editor.putString("password", password);

        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);

        editor.commit();

        Map<String, String> registerData = new HashMap<>();
        registerData.put("phone_number", phone);
        registerData.put("language", language);
        registerData.put("email", email);
        registerData.put("country", country);
        registerData.put("first_name", firstName);
        registerData.put("last_name", lastName);

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                    s = doGet("register", registerData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return s;
            }

            @Override
            protected void onPostExecute(final String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    int userID = obj.getInt("user_id");
                    DataModel.get().setUserID(context, userID);
                    Log.d("REGISTRATION", "User ID: " + userID);

                    callback.run();

                } catch (JSONException e) {
                    Toast.makeText(context, "Failed to register. Probably no internet connection", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.execute();

        DataModel.initialize(context);
    }

    public void addCard(Context context, int type) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        if (type == Card.CARD_BULLTRADE && !preferences.getBoolean("hasCard1", false)) {
            editor.putBoolean("hasCard1", true); // value to store
            cards.add(new Card(Card.CARD_BULLTRADE));
        } else if (type == Card.CARD_BULLBANK && !preferences.getBoolean("hasCard2", false)) {
            editor.putBoolean("hasCard2", true); // value to store
            cards.add(new Card(Card.CARD_BULLBANK));
        }

        editor.commit();
    }
}
