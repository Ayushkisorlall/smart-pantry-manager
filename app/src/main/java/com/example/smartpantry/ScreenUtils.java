package com.example.smartpantry;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Keeps screen content below the title bar and above the navigation bar
public class ScreenUtils {

    public static void fixInsets(Activity activity) {
        View content = activity.findViewById(android.R.id.content);

        // Height of the title bar
        TypedValue tv = new TypedValue();
        activity.getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true);
        int actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data, activity.getResources().getDisplayMetrics());

        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, actionBarHeight, bars.right, bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(content);
    }
}