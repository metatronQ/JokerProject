package com.example.jokerproject.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class StatusBarView extends View {
    public StatusBarView(Context context) {
        super(context);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setBackgroundColor(int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setBackground(new ColorDrawable(Color.BLACK));
        } else {
            super.setBackgroundColor(color);
        }
    }
}
