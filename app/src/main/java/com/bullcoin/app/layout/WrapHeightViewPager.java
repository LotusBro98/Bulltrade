package com.bullcoin.app.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bullcoin.app.R;

public class WrapHeightViewPager extends ViewPager {

    boolean fillViewport;
    boolean useCurrentHeight;

    public WrapHeightViewPager(Context context) {
        super(context);
    }

    public WrapHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WrapHeightViewPager, 0, 0);
        try {
            fillViewport = ta.getBoolean(R.styleable.WrapHeightViewPager_fillViewport, false);
            useCurrentHeight = ta.getBoolean(R.styleable.WrapHeightViewPager_useCurrentHeight, false);
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (useCurrentHeight) {
            setOffscreenPageLimit(getAdapter().getCount());
            addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    requestLayout();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height;
        if (useCurrentHeight) {
            View currentChild = getChildAt(getCurrentItem());
            currentChild.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
            int current_height = getChildAt(getCurrentItem()).getMeasuredHeight();
            height = current_height;
        } else {
            int max_height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
                int h = child.getMeasuredHeight();
                if (h > max_height) max_height = h;
            }
            height = max_height;
        }

        if (fillViewport && MeasureSpec.getSize(heightMeasureSpec) > height) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }

        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
