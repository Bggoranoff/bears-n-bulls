package com.github.bggoranoff.tradegame.util;

import android.content.Context;
import android.util.TypedValue;

public class IconsSelector {

    public static int getDrawable(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }

    public static int getInDps(Context context, int d) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, d, context.getResources().getDisplayMetrics());
    }
}
