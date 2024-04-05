package com.cloudwell.fontsmaterialuiux;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class Italic_TextView extends AppCompatTextView {
    public Italic_TextView(Context context) {
        super(context);
        init();
    }

    public Italic_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Italic_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLineSpacing(0, 0.9f);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/italic.ttf");
            setTypeface(tf);
        }
    }
}