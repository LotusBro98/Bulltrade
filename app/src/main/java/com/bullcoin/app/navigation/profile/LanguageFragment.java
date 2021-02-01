package com.bullcoin.app.navigation.profile;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bullcoin.app.LocaleManager;
import com.bullcoin.app.MainActivity;
import com.bullcoin.app.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class LanguageFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_language, container, false);

        ImageButton backButton = root.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigateUp();
            }
        });

        Button buttonEnglish = root.findViewById(R.id.language_english);
        Button buttonDeutsch = root.findViewById(R.id.language_deutsch);
        Button buttonRussian = root.findViewById(R.id.language_russian);

        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setNewLocale(((MainActivity)getActivity()), LocaleManager.ENGLISH);
            }
        });

        buttonDeutsch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setNewLocale(((MainActivity)getActivity()), LocaleManager.DEUTSCH);
            }
        });

        buttonRussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setNewLocale(((MainActivity)getActivity()), LocaleManager.RUSSIAN);
            }
        });

        return root;
    }
}
