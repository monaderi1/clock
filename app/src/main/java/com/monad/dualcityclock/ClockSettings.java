package com.monad.dualcityclock;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

final class ClockSettings {
    static final String FONT_SIZE = "clock_font_sp";
    static final String LAYOUT = "layout_stacked";
    static final int MIN_SP = 36;
    static final int MAX_SP = 100;
    static final int DEFAULT_SP = 76;

    static int fontSize(Context context) {
        int stored = CityCatalog.prefs(context).getInt(FONT_SIZE, DEFAULT_SP);
        return Math.max(MIN_SP, Math.min(MAX_SP, stored));
    }

    static boolean stacked(Context context) {
        return CityCatalog.prefs(context).getBoolean(LAYOUT, true);
    }

    static void save(Context context, int sp, boolean stacked) {
        CityCatalog.prefs(context).edit()
            .putInt(FONT_SIZE, Math.max(MIN_SP, Math.min(MAX_SP, sp)))
            .putBoolean(LAYOUT, stacked)
            .apply();
    }

    /** Fit a 24-hour HH:mm text within the allocated widget cell. */
    static float fittedSp(Context context, int requestedSp, float widthDp, float heightDp) {
        float density = context.getResources().getDisplayMetrics().density;
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(Typeface.create("sans-serif-condensed-medium", Typeface.NORMAL));
        paint.setTextSize(100f * scaledDensity);
        float charsWidthAt100 = paint.measureText("23:59");
        float widthLimitSp = (widthDp * density / charsWidthAt100) * 100f;
        float heightLimitSp = (heightDp * density / scaledDensity) / 1.18f;
        // Clamp the rendered size when the launcher gives the widget less room.
        return Math.max(18f, Math.min(requestedSp, Math.min(widthLimitSp, heightLimitSp)));
    }

    private ClockSettings() {}
}
