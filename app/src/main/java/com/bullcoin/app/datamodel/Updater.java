package com.bullcoin.app.datamodel;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.bullcoin.app.navigation.chat.ChatDialogueActivity;

public class Updater {

    Runnable updateRunnable;
    Handler updateHandler;

    boolean first = true;

    public Updater() {}

    public void start(Context context) {
        updateHandler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                new UpdateTask(context).execute();
            }
        };
        updateRunnable.run();
    }

    private static final int UPDATE_PERIOD = 5000;

    public void update(Context context) {
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
            updateHandler.postDelayed(updateRunnable, UPDATE_PERIOD);
        }
    }
}
