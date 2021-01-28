package com.bullcoin.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PinCreateActivity extends PinActivity {

    String pin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAccBtn.setVisibility(View.INVISIBLE);
        forgotPassBtn.setVisibility(View.INVISIBLE);

        messageText.setText("Create a pin");
    }

    private void savePin(String pin) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pin", pin); // value to store
        editor.commit();
    }

    @Override
    public boolean onPinEnter(String pin) {
        if (this.pin == null) {
            this.pin = pin;
            messageText.setText("Repeat");
            return true;
        } else {
            if (!this.pin.equals(pin)) {
                vibrate(50);
                Toast.makeText(this, "Pins doesn't match", Toast.LENGTH_LONG).show();
                messageText.setText("Create a pin");
                this.pin = null;
                return true;
            }

            savePin(pin);
            Intent intent = new Intent(PinCreateActivity.this, PinLoginActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
    }
}
