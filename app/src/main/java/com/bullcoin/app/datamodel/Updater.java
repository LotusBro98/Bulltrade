package com.bullcoin.app.datamodel;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.bullcoin.app.navigation.chat.ChatDialogueActivity;

public class Updater {

    Runnable updateRunnable;
    Handler updateHandler;

    public boolean first = true;
    public static Updater instance;

    Context context;

    public Updater(Context context) {
        instance = this;
        this.context = context;
        updateHandler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                new UpdateTask(context).execute();
            }
        };
    }

    public void stop() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    public void start() {
        first = true;
        updateHandler.removeCallbacks(updateRunnable);
        updateHandler.post(updateRunnable);
    }

    private static final int UPDATE_PERIOD = 5000;

    public void update(Context context) {
        if (!DataModel.get().lastSearch.equals(""))
            return;
        DataModel.updateDialogues(context, "", !first);
        first = false;
    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {
        Context context;

        UpdateTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(java.lang.Void... voids) {
            update(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            updateHandler.removeCallbacks(updateRunnable);
            updateHandler.postDelayed(updateRunnable, UPDATE_PERIOD);
        }
    }
}
