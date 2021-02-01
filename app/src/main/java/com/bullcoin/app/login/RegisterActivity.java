package com.bullcoin.app.login;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bullcoin.app.LocaleManager;
import com.bullcoin.app.LocalizedActivity;
import com.bullcoin.app.R;
import com.bullcoin.app.datamodel.DataModel;

public class RegisterActivity extends LocalizedActivity {
    EditText phone;
    Spinner language;
    EditText email;
    EditText country;
    EditText firstName;
    EditText lastName;
    EditText password;
    EditText repeatPassword;

    TextView registration;
    Button next;


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
        registration = findViewById(R.id.registration);
        next = findViewById(R.id.button_next);

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

        String locale_code = LocaleManager.getLocale(getResources()).toString();
        if (locale_code.equals("en")) {
            language.setSelection(0);
        } else if (locale_code.equals("de")) {
            language.setSelection(1);
        } else if (locale_code.equals("ru")) {
            language.setSelection(2);
        } else {
            language.setSelection(0);
        }

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String languageStr = LocaleManager.LocaleDef.SUPPORTED_LOCALES[position];
                setLocale(languageStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        String languageStr = getResources().getStringArray(R.array.languages)[language.getSelectedItemPosition()];
        DataModel.register(this,
                phone.getText().toString(),
                languageStr,
                email.getText().toString(),
                country.getText().toString(),
                password.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString()
        );

        Intent intent = new Intent(RegisterActivity.this, PinCreateActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        phone.setText(getResources().getString(R.string.phone_number));
        email.setHint(getResources().getString(R.string.email));
        country.setHint(getResources().getString(R.string.country));
        firstName.setHint(getResources().getString(R.string.first_name));
        lastName.setHint(getResources().getString(R.string.last_name));
        password.setHint(getResources().getString(R.string.password));
        repeatPassword.setHint(getResources().getString(R.string.repeat_your_password));
        registration.setText(getResources().getString(R.string.registration));
        next.setText(getResources().getString(R.string.next));
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
