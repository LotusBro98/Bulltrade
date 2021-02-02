package com.bullcoin.app.datamodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bullcoin.app.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
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

    private static final String BASE_URL = "http://82.148.29.197:8000/";

    private int userID;

    private static DataModel instance;

    public static DataModel get() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new DataModel(context);
    }

    public DataModel(Context context) {
        try {
            assets = Asset.parseFromXML(context);
        } catch (Exception e) {
            Log.e("ASSET_PARSING", "Failed to parse assets");
            e.printStackTrace();
            assets = new ArrayList<>();
        }

         cards = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userFirstName = preferences.getString("firstName", "");
        userLastName = preferences.getString("lastName", "");
        brokerBalance = preferences.getFloat("brokerBalance", 100000.0f);
        bankBalance = preferences.getFloat("bankBalance", 0.0f);

        boolean hasCard1 = preferences.getBoolean("hasCard1", false);
        boolean hasCard2 = preferences.getBoolean("hasCard2", false);
        if (hasCard1) {
            cards.add(new Card(Card.CARD_BULLTRADE));
        }
        if (hasCard2) {
            cards.add(new Card(Card.CARD_BULLBANK));
        }

        try {
            dialogues = Dialogue.parseFromXML(context);
        } catch (Exception e) {
            Log.e("ASSET_PARSING", "Failed to parse dialogues");
            dialogues = new ArrayList<>();
            e.printStackTrace();
        }

        try {
            news = News.parseFromXML(context);
        } catch (Exception e) {
            Log.e("ASSET_PARSING", "Failed to parse news");
            news = new ArrayList<>();
            e.printStackTrace();
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

    public List<Dialogue> getDialogues() {
        return dialogues;
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

    public static void doGetAsync(String url, Map<String, String> args) {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String s = "";
                try {
                    s = doGet(url, args);
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

    public static void register(Context context,
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
        doGetAsync("register", registerData);

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
