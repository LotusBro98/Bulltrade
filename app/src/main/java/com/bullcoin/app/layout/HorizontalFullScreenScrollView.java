package com.bullcoin.app.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class HorizontalFullScreenScrollView extends HorizontalScrollView {
    public HorizontalFullScreenScrollView(Context context) {
        this(context, null, 0);
    }

    public HorizontalFullScreenScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalFullScreenScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
