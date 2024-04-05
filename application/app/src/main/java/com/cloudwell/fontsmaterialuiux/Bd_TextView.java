package com.cloudwell.fontsmaterialuiux;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;



public class Bd_TextView extends androidx.appcompat.widget.AppCompatTextView {
    public Bd_TextView(Context context) {
        super(context);
    }

    public Bd_TextView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public Bd_TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setLineSpacing(0, 0.9f);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/bd.ttf");
            setTypeface(tf);
        }
    }
}
