package com.bullcoin.app.datamodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bullcoin.app.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Asset {
    public static final int TYPE_STOCK = 0;
    public static final int TYPE_SHARE = 1;
    public static final int TYPE_CRYPT = 2;

    private double price;
    private double percent;
    private int owned;
    private int iconResourceID;
    private int type;
    private String name;
    private int id;
    private int descriptionID;

    public String getDescription(Context context) {
        return context.getResources().getString(descriptionID);
    }

    public int getId() {
        return id;
    }

    private Asset() {
    }

    private void loadOwned(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        owned = preferences.getInt("owned_" + name, 0);
    }

    public void setOwned(Context context, int owned) {
        this.owned = owned;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("owned_" + name, owned); // value to store
        editor.commit();
    }

    public double getPrice() {
        return price;
    }

    public double getPercent() {
        return percent;
    }

    public int getOwned() {
        return owned;
    }

    public int getIconResourceID() {
        return iconResourceID;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static List<Asset> parseFromXML(Context context) throws XmlPullParserException, IOException {
        Resources res = context.getResources();
        XmlResourceParser xmlResourceParser = res.getXml(R.xml.buyable_assets);

        String logTag = "ASSET_PARSER";
        ArrayList<String> xmlTagStack = new ArrayList<>();
        ArrayList<Asset> assets = new ArrayList<>();
        Asset currentAsset = null;
        int assetID = 0;

        xmlResourceParser.next();
        int eventType = xmlResourceParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                Log.d(logTag, "Begin Document");
            } else if (eventType == XmlPullParser.START_TAG) {
                String tagName = xmlResourceParser.getName();
                xmlTagStack.add(tagName);

                if (tagName.equals("asset")) {
                    currentAsset = new Asset();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                String tagName = xmlResourceParser.getName();
                if (xmlTagStack.size() < 1) {
                    Log.e(logTag, "Error 101: encountered END_TAG " + xmlResourceParser.getName() + " while TagStack is empty");
                    return null;
                }
                xmlTagStack.remove(xmlTagStack.size() - 1);

                if (tagName.equals("asset")) {
                    if (currentAsset != null) {
                        currentAsset.loadOwned(context);
                        currentAsset.id = assetID;
                        assetID++;
                        assets.add(currentAsset);
                    }
                    currentAsset = null;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String currentTag = xmlTagStack.get(xmlTagStack.size() - 1);
                String text = xmlResourceParser.getText();

                if (currentAsset == null) {
                    Log.e(logTag, "currentAsset is not initialized! text: " + text + ", current tag: " + currentTag + ", depth: " + xmlTagStack.size());
                    continue;
                }

                if (currentTag.equals("icon")) {
                    currentAsset.iconResourceID = context.getResources().getIdentifier(text, "drawable", context.getPackageName());
                } else if (currentTag.equals("name")) {
                    currentAsset.name = text;
                } else if (currentTag.equals("type")) {
                    if (text.equals("stock")) {
                        currentAsset.type = TYPE_STOCK;
                    } else if (text.equals("share")) {
                        currentAsset.type = TYPE_SHARE;
                    } else if (text.equals("crypt")) {
                        currentAsset.type = TYPE_CRYPT;
                    } else {
                        currentAsset.type = 0;
                    }
                } else if (currentTag.equals("price")) {
                    currentAsset.price = Double.parseDouble(text);
                } else if (currentTag.equals("percent")) {
                    currentAsset.percent = Double.parseDouble(text);
                } else if (currentTag.equals("text")) {
                    currentAsset.descriptionID = context.getResources().getIdentifier(text, "string", context.getPackageName());
                }
            }
            eventType = xmlResourceParser.next();
        }

        return assets;
    }
}
