package com.openai.routinequest;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class AppData {
    public static final String PREFS = "routine_stamp_prefs";
    public static final String CHECKED = "checked_dates"; // v1/v2 호환
    public static final String UNLOCKED_V3 = "unlocked_heroes_v3";

    private AppData() {}

    public static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static Set<String> checked(Context c) {
        return new HashSet<>(prefs(c).getStringSet(CHECKED, new HashSet<String>()));
    }

    public static void saveChecked(Context c, Set<String> checked) {
        prefs(c).edit().putStringSet(CHECKED, new HashSet<>(checked)).apply();
    }

    public static Set<String> unlocked(Context c) {
        return new HashSet<>(prefs(c).getStringSet(UNLOCKED_V3, new HashSet<String>()));
    }

    public static void saveUnlocked(Context c, Set<String> unlocked) {
        prefs(c).edit().putStringSet(UNLOCKED_V3, new HashSet<>(unlocked)).apply();
    }

    public static boolean markToday(Context c) {
        Set<String> checked = checked(c);
        Calendar today = Calendar.getInstance();
        RoutineLogic.zeroTime(today);
        String key = RoutineLogic.dateKey(today);
        boolean changed = checked.add(key);
        if (changed) saveChecked(c, checked);
        maybeUnlockHeroForMonth(c, checked, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        return changed;
    }

    public static boolean toggleDate(Context c, String key) {
        Set<String> checked = checked(c);
        boolean added;
        if (checked.contains(key)) {
            checked.remove(key);
            added = false;
        } else {
            checked.add(key);
            added = true;
        }
        saveChecked(c, checked);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(key));
            maybeUnlockHeroForMonth(c, checked, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        } catch (Exception ignored) {}
        return added;
    }

    public static boolean maybeUnlockHeroForMonth(Context c, Set<String> checked, int year, int monthZeroBased) {
        int done = RoutineLogic.checkedInMonth(checked, year, monthZeroBased);
        if (done < RoutineLogic.finalMilestone(year, monthZeroBased)) return false;
        Hero hero = HeroCatalog.heroForMonth(monthZeroBased);
        Set<String> unlocked = unlocked(c);
        boolean changed = unlocked.add(hero.id);
        if (changed) saveUnlocked(c, unlocked);
        return changed;
    }

    public static void reconcileUnlocks(Context c) {
        Set<String> checked = checked(c);
        Map<String, Integer> counts = new HashMap<>();
        for (String key : checked) {
            if (key == null || key.length() < 7) continue;
            String ym = key.substring(0, 7);
            counts.put(ym, counts.containsKey(ym) ? counts.get(ym) + 1 : 1);
        }
        Set<String> unlocked = unlocked(c);
        boolean changed = false;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            try {
                String[] p = e.getKey().split("-");
                int year = Integer.parseInt(p[0]);
                int month = Integer.parseInt(p[1]) - 1;
                if (e.getValue() >= RoutineLogic.finalMilestone(year, month)) {
                    changed |= unlocked.add(HeroCatalog.heroForMonth(month).id);
                }
            } catch (Exception ignored) {}
        }
        if (changed) saveUnlocked(c, unlocked);
    }

    public static void resetMonth(Context c, int year, int monthZeroBased) {
        String prefix = String.format(Locale.US, "%04d-%02d-", year, monthZeroBased + 1);
        Set<String> copy = checked(c);
        Set<String> remove = new HashSet<>();
        for (String s : copy) if (s != null && s.startsWith(prefix)) remove.add(s);
        copy.removeAll(remove);
        saveChecked(c, copy);
    }
}
