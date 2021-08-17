package com.github.bggoranoff.tradegame.component;

import android.content.Context;
import android.widget.TableRow;

import androidx.annotation.NonNull;

import com.github.bggoranoff.tradegame.model.Asset;
import com.github.bggoranoff.tradegame.util.AssetConstants;
import com.github.bggoranoff.tradegame.util.IconsSelector;

public class AssetView extends androidx.appcompat.widget.AppCompatImageView {

    private Asset asset;

    public AssetView(@NonNull Context context) {
        super(context);
    }

    public AssetView(@NonNull Context context, @NonNull Asset asset) {
        this(context);
        this.asset = asset;

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, IconsSelector.getInDps(context, AssetConstants.DIMENSIONS), 1.0f);
        int m = IconsSelector.getInDps(context, AssetConstants.MARGIN);
        layoutParams.setMargins(m, m, m, m);
        setLayoutParams(layoutParams);

        setImageResource(IconsSelector.getDrawable(context, asset.getFileName()));
        setTag(asset.getSymbol());
        setContentDescription(asset.getSymbol());
    }

    public Asset getAsset() {
        return asset;
    }
}
