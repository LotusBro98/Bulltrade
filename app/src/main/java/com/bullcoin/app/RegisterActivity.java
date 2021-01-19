package com.bullcoin.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText phone;
    Spinner language;
    EditText email;
    EditText country;
    EditText firstName;
    EditText lastName;
    EditText password;
    EditText repeatPassword;


    enum Step {
        PHONE_LANG,
        EMAIL_COUNTRY,
        NAME_PASS
    };

    Step step = Step.PHONE_LANG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = findViewById(R.id.editPhone);
        language = findViewById(R.id.language_selector);
        email = findViewById(R.id.editEmail);
        country = findViewById(R.id.editCountry);
        firstName = findViewById(R.id.editFirstName);
        lastName = findViewById(R.id.editLastName);
        password = findViewById(R.id.editPassword);
        repeatPassword = findViewById(R.id.editPasswordRepeat);

        String[] languages = getResources().getStringArray(R.array.languages);
        ArrayAdapter<?> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, languages);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        language.setAdapter(adapter);

        email.setVisibility(View.GONE);
        country.setVisibility(View.GONE);
        firstName.setVisibility(View.GONE);
        lastName.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        repeatPassword.setVisibility(View.GONE);
    }

    private boolean handlePhoneLang() {
        return true;
    }

    private boolean handleEmailCountry() {
        return true;
    }

    private boolean handleNamePass() {
        String pass = password.getText().toString();
        String passRep = repeatPassword.getText().toString();
        Log.d("PASS", pass);
        Log.d("PASS", passRep);
        if (!passRep.equals(pass)) {
            Toast.makeText(this, "Passwords doesn't match", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void finishRegistration() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phone", phone.getText().toString());
        String languageStr = getResources().getStringArray(R.array.languages)[language.getSelectedItemPosition()];
        editor.putString("language", languageStr);
        editor.putString("email", email.getText().toString());
        editor.putString("country", country.getText().toString());
        editor.putString("firstName", firstName.getText().toString());
        editor.putString("lastName", lastName.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.commit();

        Intent intent = new Intent(RegisterActivity.this, PinCreateActivity.class);
        startActivity(intent);
        finish();
    }

    public void onNextClick(View view) {
        switch (step) {
            case PHONE_LANG:
                if (handlePhoneLang()) {
                    phone.setVisibility(View.GONE);
                    language.setVisibility(View.GONE);
                    email.setVisibility(View.VISIBLE);
                    country.setVisibility(View.VISIBLE);
                }
                step = Step.EMAIL_COUNTRY;
                break;

            case EMAIL_COUNTRY:
                if (handleEmailCountry()) {
                    email.setVisibility(View.GONE);
                    country.setVisibility(View.GONE);
                    firstName.setVisibility(View.VISIBLE);
                    lastName.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    repeatPassword.setVisibility(View.VISIBLE);
                }
                step = Step.NAME_PASS;
                break;

            case NAME_PASS:
                if (handleNamePass()) {
                    finishRegistration();
                }
                break;
        }
    }
}
