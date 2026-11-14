package com.monad.dualcityclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class DualClockWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] widgetIds) {
        for (int id : widgetIds) updateOne(context, manager, id);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager manager,
            int widgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, manager, widgetId, newOptions);
        updateOne(context, manager, widgetId);
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, DualClockWidgetProvider.class);
        for (int id : manager.getAppWidgetIds(provider)) updateOne(context, manager, id);
    }

    private static void updateOne(Context context, AppWidgetManager manager, int widgetId) {
        CityCatalog.City first = CityCatalog.CITIES[CityCatalog.firstIndex(context)];
        CityCatalog.City second = CityCatalog.CITIES[CityCatalog.secondIndex(context)];
        boolean stacked = ClockSettings.stacked(context);
        int layout = stacked ? R.layout.widget_dual_clock : R.layout.widget_dual_clock_horizontal;
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
        views.setTextViewText(R.id.city_left, first.label);
        views.setTextViewText(R.id.city_right, second.label);
        views.setString(R.id.clock_left, "setTimeZone", first.zone);
        views.setString(R.id.clock_right, "setTimeZone", second.zone);

        Bundle options = manager.getAppWidgetOptions(widgetId);
        float widthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250);
        float heightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180);
        if (widthDp < 100) widthDp = 250;
        if (heightDp < 70) heightDp = 180;
        float clockWidth = stacked ? widthDp - 28f : (widthDp - 38f) / 2f;
        float clockHeight = stacked ? (heightDp - 31f) / 2f - 19f : heightDp - 40f;
        float displaySize = ClockSettings.fittedSp(context, ClockSettings.fontSize(context),
            Math.max(clockWidth, 40f), Math.max(clockHeight, 30f));
        views.setTextViewTextSize(R.id.clock_left, TypedValue.COMPLEX_UNIT_SP, displaySize);
        views.setTextViewTextSize(R.id.clock_right, TypedValue.COMPLEX_UNIT_SP, displaySize);

        Intent intent = new Intent(context, MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent settings = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, settings);
        manager.updateAppWidget(widgetId, views);
    }
}
