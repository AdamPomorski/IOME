package com.cloudwell.fontsmaterialuiux;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;

public class CustomTextInputEditText extends TextInputEditText {


    public CustomTextInputEditText(Context context) {
        super(context);
        init();

    }

    public CustomTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CustomTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        setLineSpacing(0, 0.9f);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/medium.ttf");
            setTypeface(tf);
        }
    }
}
