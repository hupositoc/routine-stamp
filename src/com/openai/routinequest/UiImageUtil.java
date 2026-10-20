package com.openai.routinequest;

import android.content.Context;
import android.widget.ImageView;

public final class UiImageUtil {
    private UiImageUtil() {}

    public static void setFitImage(Context context, ImageView view, int resId) {
        view.clearColorFilter();
        view.setAlpha(1f);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setAdjustViewBounds(true);
        view.setPadding(18, 18, 18, 18);
        view.setImageResource(resId);
    }

    public static void setSilhouette(Context context, ImageView view, int resId) {
        view.clearColorFilter();
        view.setAlpha(1f);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setAdjustViewBounds(true);
        view.setPadding(18, 18, 18, 18);
        view.setImageResource(silhouetteRes(resId));
    }

    private static int silhouetteRes(int resId) {
        if (resId == R.drawable.stage_1) return R.drawable.sil_stage_1;
        if (resId == R.drawable.stage_2) return R.drawable.sil_stage_2;
        if (resId == R.drawable.stage_3) return R.drawable.sil_stage_3;
        if (resId == R.drawable.stage_4) return R.drawable.sil_stage_4;
        if (resId == R.drawable.stage_5) return R.drawable.sil_stage_5;
        if (resId == R.drawable.hero_jan) return R.drawable.sil_hero_jan;
        if (resId == R.drawable.hero_feb) return R.drawable.sil_hero_feb;
        if (resId == R.drawable.hero_mar) return R.drawable.sil_hero_mar;
        if (resId == R.drawable.hero_apr) return R.drawable.sil_hero_apr;
        if (resId == R.drawable.hero_may) return R.drawable.sil_hero_may;
        if (resId == R.drawable.hero_jun) return R.drawable.sil_hero_jun;
        if (resId == R.drawable.hero_jul) return R.drawable.sil_hero_jul;
        if (resId == R.drawable.hero_aug) return R.drawable.sil_hero_aug;
        if (resId == R.drawable.hero_sep) return R.drawable.sil_hero_sep;
        if (resId == R.drawable.hero_oct) return R.drawable.sil_hero_oct;
        if (resId == R.drawable.hero_nov) return R.drawable.sil_hero_nov;
        if (resId == R.drawable.hero_dec) return R.drawable.sil_hero_dec;
        return R.drawable.sil_stage_1;
    }
}
