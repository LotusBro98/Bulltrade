package com.bullcoin.app.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.bullcoin.app.MainActivity;
import com.bullcoin.app.datamodel.DataModel;

public class PinLoginActivity extends PinActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataModel.initialize(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String firstName = preferences.getString("firstName", "");
        String lastName = preferences.getString("lastName", "");

        messageText.setText(firstName + " " + lastName);

        createAccBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PinLoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean checkPin(String pin) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String saved_pin = preferences.getString("pin", "");
        return pin.equals(saved_pin);
    }

    @Override
    public boolean onPinEnter(String pin) {
        if (checkPin(pin)) {
            Intent intent = new Intent(PinLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return false;
        } else {
            vibrate(50);
            return true;
        }
    }
}
