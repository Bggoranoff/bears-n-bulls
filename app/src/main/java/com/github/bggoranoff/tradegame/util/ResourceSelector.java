package com.github.bggoranoff.tradegame.util;

import android.content.Context;

public class ResourceSelector {

    public static int getDrawable(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }
}
