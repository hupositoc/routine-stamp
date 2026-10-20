package com.openai.routinequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public final class RoutineLogic {
    private RoutineLogic() {}

    public static String dateKey(Calendar c) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.getTime());
    }

    public static int checkedInMonth(Set<String> checked, int year, int monthZeroBased) {
        String prefix = String.format(Locale.US, "%04d-%02d-", year, monthZeroBased + 1);
        int n = 0;
        for (String s : checked) if (s != null && s.startsWith(prefix)) n++;
        return n;
    }

    public static int daysInMonth(int year, int monthZeroBased) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, monthZeroBased, 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int milestoneForLevel(int level, int year, int monthZeroBased) {
        if (level < 1) return 0;
        if (level >= 10) return Math.min(30, daysInMonth(year, monthZeroBased));
        return level * 3;
    }

    public static int growthLevel(int checkedDays, int year, int monthZeroBased) {
        int level = 0;
        for (int i = 1; i <= 10; i++) {
            if (checkedDays >= milestoneForLevel(i, year, monthZeroBased)) level = i;
            else break;
        }
        return level;
    }

    public static int nextMilestone(int checkedDays, int year, int monthZeroBased) {
        int level = growthLevel(checkedDays, year, monthZeroBased);
        if (level >= 10) return milestoneForLevel(10, year, monthZeroBased);
        return milestoneForLevel(level + 1, year, monthZeroBased);
    }

    public static int finalMilestone(int year, int monthZeroBased) {
        return milestoneForLevel(10, year, monthZeroBased);
    }

    public static int currentStreak(Set<String> checked, Calendar now) {
        Calendar cursor = (Calendar) now.clone();
        zeroTime(cursor);
        if (!checked.contains(dateKey(cursor))) cursor.add(Calendar.DAY_OF_MONTH, -1);
        int streak = 0;
        while (checked.contains(dateKey(cursor))) {
            streak++;
            cursor.add(Calendar.DAY_OF_MONTH, -1);
        }
        return streak;
    }

    public static String weekdayKor(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "일";
            case Calendar.MONDAY: return "월";
            case Calendar.TUESDAY: return "화";
            case Calendar.WEDNESDAY: return "수";
            case Calendar.THURSDAY: return "목";
            case Calendar.FRIDAY: return "금";
            default: return "토";
        }
    }

    public static int stageArtRes(int level, int heroFinalRes) {
        if (level >= 10) return heroFinalRes;
        if (level <= 0) return R.drawable.stage_1;
        if (level <= 2) return R.drawable.stage_2;
        if (level <= 4) return R.drawable.stage_3;
        if (level <= 7) return R.drawable.stage_4;
        return R.drawable.stage_5;
    }

    public static void zeroTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }
}
