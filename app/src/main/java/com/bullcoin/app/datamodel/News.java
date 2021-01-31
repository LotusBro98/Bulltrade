package com.bullcoin.app.datamodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bullcoin.app.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class News {
    String source;
    String time;
    String text;
    int iconResourceID;
    int id;
    boolean selected;

    private News() {
        Log.d("YA RODILSA", this.toString());
    }

    private void loadSelected(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        selected = preferences.getBoolean("selectedNews_" + id, false);
    }

    public void setSelected(Context context, boolean selected) {
        this.selected = selected;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("selectedNews_" + id, selected); // value to store
        editor.commit();
    }

    public boolean isSelected() {
        return selected;
    }

    public String getSource() {
        return source;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public int getIconResourceID() {
        return iconResourceID;
    }

    public static List<News> parseFromXML(Context context) throws XmlPullParserException, IOException {
        Resources res = context.getResources();
        XmlResourceParser xmlResourceParser = res.getXml(R.xml.news);

        String logTag = "ASSET_PARSER";
        ArrayList<String> xmlTagStack = new ArrayList<>();
        ArrayList<News> news = new ArrayList<>();
        News currentNews = null;
        int assetID = 0;

        xmlResourceParser.next();
        int eventType = xmlResourceParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                Log.d(logTag, "Begin Document");
            } else if (eventType == XmlPullParser.START_TAG) {
                String tagName = xmlResourceParser.getName();
                xmlTagStack.add(tagName);

                if (tagName.equals("new")) {
                    currentNews = new News();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                String tagName = xmlResourceParser.getName();
                if (xmlTagStack.size() < 1) {
                    Log.e(logTag, "Error 101: encountered END_TAG " + xmlResourceParser.getName() + " while TagStack is empty");
                    return null;
                }
                xmlTagStack.remove(xmlTagStack.size() - 1);

                if (tagName.equals("new")) {
                    if (currentNews != null) {
                        currentNews.id = assetID;
                        currentNews.loadSelected(context);
                        assetID++;
                        news.add(currentNews);
                    }
                    currentNews = null;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String currentTag = xmlTagStack.get(xmlTagStack.size() - 1);
                String text = xmlResourceParser.getText();

                if (currentNews == null) {
                    Log.e(logTag, "currentAsset is not initialized! text: " + text + ", current tag: " + currentTag + ", depth: " + xmlTagStack.size());
                    continue;
                }

                if (currentTag.equals("icon")) {
                    currentNews.iconResourceID = context.getResources().getIdentifier(text, "drawable", context.getPackageName());
                } else if (currentTag.equals("source")) {
                    currentNews.source = text;
                } else if (currentTag.equals("time")) {
                    currentNews.time = text;
                } else if (currentTag.equals("text")) {
                    currentNews.text = text;
                }
            }
            eventType = xmlResourceParser.next();
        }

        return news;
    }
}
