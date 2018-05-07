package com.hireboss.android.roachlabs.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by administrator on 20/7/15.
 */
public class EditTextWithFont extends EditText {



    public EditTextWithFont(Context context, AttributeSet attrs) {

        super(context, attrs);
        android.graphics.Typeface fontsStyle = android.graphics.Typeface.createFromAsset(context.getAssets(),"miso-bold.otf");
        this.setTypeface(fontsStyle);

    }

    public EditTextWithFont(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        android.graphics.Typeface fontsStyle = android.graphics.Typeface.createFromAsset(context.getAssets(),"miso-bold.otf");
        this.setTypeface(fontsStyle);

    }

    public EditTextWithFont(Context context) {

        super(context);
        android.graphics.Typeface fontsStyle = android.graphics.Typeface.createFromAsset(context.getAssets(),"miso-bold.otf");
        this.setTypeface(fontsStyle);

    }

}