package com.openai.routinequest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Set;

public class RoutineWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_MARK_TODAY = "com.openai.routinequest.ACTION_MARK_TODAY";

    @Override public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) updateWidget(context, appWidgetManager, id);
    }

    @Override public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_MARK_TODAY.equals(intent.getAction())) {
            AppData.markToday(context);
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName name = new ComponentName(context, RoutineWidgetProvider.class);
            onUpdate(context, mgr, mgr.getAppWidgetIds(name));
        }
    }

    static void updateWidget(Context context, AppWidgetManager manager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_routine);
        Calendar now = Calendar.getInstance();
        Set<String> checked = AppData.checked(context);
        int y = now.get(Calendar.YEAR), m = now.get(Calendar.MONTH);
        int done = RoutineLogic.checkedInMonth(checked, y, m);
        int days = RoutineLogic.daysInMonth(y, m);
        int streak = RoutineLogic.currentStreak(checked, now);
        int percent = Math.round(done * 100f / days);
        int level = RoutineLogic.growthLevel(done, y, m);
        Hero hero = HeroCatalog.heroForMonth(m);

        views.setTextViewText(R.id.widgetDate, (m + 1) + "/" + now.get(Calendar.DAY_OF_MONTH) + " (" + RoutineLogic.weekdayKor(now.get(Calendar.DAY_OF_WEEK)) + ")");
        views.setTextViewText(R.id.widgetStats, "연속 " + streak + "일 · 달성률 " + percent + "%");
        views.setTextViewText(R.id.widgetHero, hero.role + " · 성장 " + level + "/10");
        views.setImageViewResource(R.id.widgetImage, RoutineLogic.stageArtRes(level, hero.imageRes));

        Intent tap = new Intent(context, RoutineWidgetProvider.class).setAction(ACTION_MARK_TODAY);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1001, tap, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetButton, pi);
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent openPi = PendingIntent.getActivity(context, 1002, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetRoot, openPi);
        manager.updateAppWidget(appWidgetId, views);
    }
}
