package com.cloudwell.fontsmaterialuiux;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;



public class Medium_TextView extends androidx.appcompat.widget.AppCompatTextView {
    public Medium_TextView(Context context) {
        super(context);
        init();
    }

    public Medium_TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Medium_TextView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        setLineSpacing(0, 0.9f);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/regular.ttf");
            setTypeface(tf);
        }
    }
}
