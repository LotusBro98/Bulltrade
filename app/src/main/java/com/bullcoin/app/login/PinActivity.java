package com.bullcoin.app.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bullcoin.app.R;

public abstract class PinActivity extends AppCompatActivity {

    int enter_i = 0;
    int[] digits;
    TextView messageText;
    ImageView[] pinChips;
    Button createAccBtn;
    Button forgotPassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_BULLCOIN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        createAccBtn = findViewById(R.id.button_create_acc);
        forgotPassBtn = findViewById(R.id.button_forgot_pass);
        messageText = findViewById(R.id.pin_message_view);

        digits = new int[4];

        pinChips = new ImageView[]{
                findViewById(R.id.pin_chip_1),
                findViewById(R.id.pin_chip_2),
                findViewById(R.id.pin_chip_3),
                findViewById(R.id.pin_chip_4)
        };
    }

    public abstract boolean onPinEnter(String pin);

    protected void vibrate(int millis) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(millis);
        }
    }

    private String collectPin() {
        String pinStr = "";
        for (int i = 0; i < enter_i; i++) {
            pinStr += String.valueOf(digits[i]);
        }
        return pinStr;
    }

    public void onPinButtonClick(View view)
    {
        int digit = Integer.parseInt((String)view.getTag());
        digits[enter_i] = digit;
        enter_i++;
        if (enter_i == 4) {
            boolean clear = onPinEnter(collectPin());
            if (clear) {
                enter_i = 0;
                renderPin();
            } else {
                renderPin();
                enter_i = 0;
            }
        } else {
            renderPin();
        }


    }

    private void renderPin() {
        for (int i = 0; i < 4; i++) {
            if (i < enter_i) {
                pinChips[i].setImageResource(R.drawable.pin_chip_active);
            } else {
                pinChips[i].setImageResource(R.drawable.pin_chip_inactive);
            }
        }
    }
}
