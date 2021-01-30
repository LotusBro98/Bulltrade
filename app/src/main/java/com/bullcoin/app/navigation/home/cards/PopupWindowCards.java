package com.bullcoin.app.navigation.home.cards;

import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.navigation.Navigation;

import com.bullcoin.app.R;

public class PopupWindowCards extends PopupWindow {
    public PopupWindowCards(View contentView, int width, int height, boolean focusable, View root) {
        super(contentView, width, height, focusable);

        Button button_top_up = contentView.findViewById(R.id.button_top_up);
        button_top_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_selectCardFragment);
            }
        });
    }
}
