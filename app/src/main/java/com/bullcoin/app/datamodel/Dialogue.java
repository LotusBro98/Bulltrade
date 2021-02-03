package com.bullcoin.app.datamodel;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.bullcoin.app.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dialogue {

    int id;
    int userID;
    int iconResourceID;
    String name;
    List<Message> messages;

    private Dialogue() {
        messages = new ArrayList<>();
    }

    public Dialogue(int iconResourceID, String name, List<Message> messages) {
        this.iconResourceID = iconResourceID;
        this.name = name;
        this.messages = messages;
    }

    public int getIconResourceID() {
        return iconResourceID;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public static List<Dialogue> parseFromXML(Context context) throws XmlPullParserException, IOException {
        Resources res = context.getResources();
        XmlResourceParser xmlResourceParser = res.getXml(R.xml.dialogues);

        String logTag = "ASSET_PARSER";
        ArrayList<String> xmlTagStack = new ArrayList<>();
        ArrayList<Dialogue> dialogues = new ArrayList<>();
        Dialogue currentDialogue = null;
        Message currentMessage = null;
        int source = Message.FROM_ME;
        int id = 0;

        xmlResourceParser.next();
        int eventType = xmlResourceParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                Log.d(logTag, "Begin Document");
            } else if (eventType == XmlPullParser.START_TAG) {
                String tagName = xmlResourceParser.getName();
                xmlTagStack.add(tagName);

                if (tagName.equals("dialogue")) {
                    Log.d(logTag, "new dialogue");
                    currentDialogue = new Dialogue();
                } else if (tagName.equals("message")) {
                    boolean from_me = xmlResourceParser.getAttributeBooleanValue(null, "from_me", false);
                    source = from_me ? Message.FROM_ME : Message.FROM_FRIEND;
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                String tagName = xmlResourceParser.getName();
                if (xmlTagStack.size() < 1) {
                    Log.e(logTag, "Error 101: encountered END_TAG " + xmlResourceParser.getName() + " while TagStack is empty");
                    return null;
                }

                xmlTagStack.remove(xmlTagStack.size() - 1);

                if (tagName.equals("dialogue")) {
                    if (currentDialogue != null) {
                        currentDialogue.id = id;
                        id++;
                        dialogues.add(currentDialogue);
                    }
                    currentDialogue = null;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String currentTag = xmlTagStack.get(xmlTagStack.size() - 1);
                String text = xmlResourceParser.getText();

                if (currentDialogue == null) {
                    Log.e(logTag, "currentDialogue is not initialized! text: " + text + ", current tag: " + currentTag + ", depth: " + xmlTagStack.size());
                    continue;
                }

                if (currentTag.equals("icon")) {
                    currentDialogue.iconResourceID = context.getResources().getIdentifier(text, "drawable", context.getPackageName());
                } else if (currentTag.equals("name")) {
                    currentDialogue.name = text;
                } else if (currentTag.equals("message")) {
                    currentDialogue.messages.add(new Message(source, text));
                }
            }
            eventType = xmlResourceParser.next();
        }

        return dialogues;
    }
}
