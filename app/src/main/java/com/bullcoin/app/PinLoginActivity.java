package com.bullcoin.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PinLoginActivity extends PinActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onPinEnter(String pin) {
        if (checkPin(pin)) {
            Toast.makeText(this, "SUCCESS", Toast.LENGTH_LONG).show();
        } else {
            vibrate(50);
        }
    }
}
