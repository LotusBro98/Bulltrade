package com.bullcoin.app.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.bullcoin.app.R;

public class PinCreateActivity extends PinActivity {

    String pin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAccBtn.setVisibility(View.INVISIBLE);
        forgotPassBtn.setVisibility(View.INVISIBLE);

        messageText.setText(R.string.create_a_pin);
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
            messageText.setText(R.string.repeat);
            return true;
        } else {
            if (!this.pin.equals(pin)) {
                vibrate(50);
                Toast.makeText(this, getString(R.string.pins_doesnt_match), Toast.LENGTH_LONG).show();
                messageText.setText(R.string.create_a_pin);
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
