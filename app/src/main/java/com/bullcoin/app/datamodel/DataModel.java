package com.bullcoin.app.datamodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private String userFirstName;
    private String userLastName;
    private double brokerBalance;
    private double bankBalance;
    private List<Asset> assets;
    private List<Card> cards;

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
            Toast.makeText(context, "You have bought " + asset.getName() + " x" + quantity, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, "Not enough money", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean sellAsset(Context context, Asset asset, int quantity) {
        if (asset.getOwned() >= quantity) {
            asset.setOwned(context, asset.getOwned() - quantity);
            setBrokerBalance(context, getBrokerBalance() + asset.getPrice() * quantity);
            Toast.makeText(context, "You have sold " + asset.getName() + " x" + quantity, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, "Not enough " + asset.getName(), Toast.LENGTH_LONG).show();
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
