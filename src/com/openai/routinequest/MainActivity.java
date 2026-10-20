package com.openai.routinequest;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {
    private final Calendar shownMonth = Calendar.getInstance();
    private Set<String> checked = new HashSet<>();
    private Set<String> unlocked = new HashSet<>();

    private TextView summaryStats, summarySub, monthTitle, heroTitle, heroInfo;
    private ImageView currentHeroImage;
    private ProgressBar growthProgress;
    private LinearLayout stageStrip, contentSection;
    private GridLayout calendarGrid;
    private Button markTodayButton, shareButton;
    private boolean transitioning = false;

    private int dp(float v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        shownMonth.set(Calendar.DAY_OF_MONTH, 1);
        getWindow().setStatusBarColor(Color.rgb(49, 100, 67));
        getWindow().setNavigationBarColor(Color.rgb(248, 247, 242));
        AppData.reconcileUnlocks(this);
        buildUi();
        reload();
    }

    @Override protected void onResume() {
        super.onResume();
        if (contentSection != null) {
            contentSection.setTranslationX(0f);
            contentSection.setAlpha(1f);
        }
        transitioning = false;
        reload();
    }

    private TextView text(String value, float sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(sp);
        v.setTextColor(color);
        if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD);
        return v;
    }

    private android.graphics.drawable.GradientDrawable bg(int fill, float radiusDp, int strokeColor, float strokeDp) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) d.setStroke(dp(strokeDp), strokeColor);
        return d;
    }

    private Button pill(String label, boolean active) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setGravity(Gravity.CENTER);
        b.setTextColor(active ? Color.WHITE : Color.rgb(46, 60, 43));
        b.setPadding(dp(20), dp(8), dp(18), dp(8));
        b.setMinHeight(0); b.setMinimumHeight(0);
        b.setBackground(bg(active ? Color.rgb(74,145,92) : Color.WHITE, 16, active ? Color.rgb(74,145,92) : Color.rgb(220,226,220), 1));
        return b;
    }

    private void styleButton(Button b, boolean active) {
        b.setTextColor(active ? Color.WHITE : Color.rgb(46, 60, 43));
        b.setBackground(bg(active ? Color.rgb(74,145,92) : Color.WHITE, 16, active ? Color.rgb(74,145,92) : Color.rgb(220,226,220), 1));
    }

    private LinearLayout card() {
        LinearLayout v = new LinearLayout(this);
        v.setOrientation(LinearLayout.VERTICAL);
        v.setPadding(dp(16), dp(16), dp(16), dp(16));
        v.setBackground(bg(Color.WHITE, 20, Color.rgb(230, 232, 228), 1));
        return v;
    }

    private void addWithBottom(LinearLayout root, View view, int marginDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(marginDp);
        root.addView(view, lp);
    }

    private LinearLayout.LayoutParams weightedLp(float weight, int rightMargin, int heightDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(heightDp), weight);
        lp.rightMargin = dp(rightMargin);
        return lp;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(28));
        root.setBackgroundColor(Color.rgb(248, 247, 242));
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));

        TextView appTitle = text("나의 루틴 모험", 28, Color.rgb(36, 50, 39), true);
        TextView appSub = text("오늘 체크로 영웅을 성장시키세요.", 13, Color.rgb(96,105,99), false);
        appSub.setPadding(0, dp(4), 0, dp(10));
        root.addView(appTitle);
        root.addView(appSub);

        LinearLayout menu = new LinearLayout(this);
        Button home = pill("홈", true);
        Button collection = pill("보관함", false);
        menu.addView(home, weightedLp(1, 6, 48));
        menu.addView(collection, weightedLp(1, 0, 48));
        collection.setOnClickListener(v -> animateToCollection());
        addWithBottom(root, menu, 12);

        contentSection = new LinearLayout(this);
        contentSection.setOrientation(LinearLayout.VERTICAL);
        root.addView(contentSection, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout summary = card();
        summaryStats = text("", 17, Color.rgb(39, 88, 57), true);
        summarySub = text("", 13, Color.rgb(95, 101, 97), false);
        summarySub.setPadding(0, dp(6), 0, 0);
        summary.addView(summaryStats);
        summary.addView(summarySub);
        LinearLayout actions = new LinearLayout(this);
        actions.setPadding(0, dp(12), 0, 0);
        markTodayButton = pill("오늘 체크", false);
        markTodayButton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_my_calendar, 0, 0, 0);
        markTodayButton.setCompoundDrawablePadding(dp(10));
        shareButton = pill("공유", false);
        shareButton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_share, 0, 0, 0);
        shareButton.setCompoundDrawablePadding(dp(10));
        actions.addView(markTodayButton, weightedLp(1, 8, 48));
        actions.addView(shareButton, weightedLp(1, 0, 48));
        summary.addView(actions);
        addWithBottom(contentSection, summary, 12);

        LinearLayout heroCard = card();
        heroCard.addView(text("성장", 15, Color.rgb(91, 126, 91), true));
        currentHeroImage = new ImageView(this);
        currentHeroImage.setBackground(bg(Color.rgb(247,246,239), 18, Color.rgb(225,229,223), 1));
        currentHeroImage.setMinimumHeight(dp(280));
        currentHeroImage.setAdjustViewBounds(true);
        currentHeroImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(-1, -2);
        imageLp.topMargin = dp(10);
        heroCard.addView(currentHeroImage, imageLp);
        heroTitle = text("", 21, Color.rgb(46,60,43), true);
        heroInfo = text("", 13, Color.rgb(95,101,97), false);
        heroInfo.setPadding(0, dp(4), 0, dp(8));
        growthProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        growthProgress.setMax(10);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2); titleLp.topMargin = dp(10);
        heroCard.addView(heroTitle, titleLp);
        heroCard.addView(heroInfo);
        heroCard.addView(growthProgress, new LinearLayout.LayoutParams(-1, dp(10)));

        stageStrip = new LinearLayout(this);
        stageStrip.setOrientation(LinearLayout.HORIZONTAL);
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.addView(stageStrip, new HorizontalScrollView.LayoutParams(-2, -2));
        LinearLayout.LayoutParams hLp = new LinearLayout.LayoutParams(-1, -2); hLp.topMargin = dp(10);
        heroCard.addView(hsv, hLp);
        addWithBottom(contentSection, heroCard, 12);

        LinearLayout calCard = card();
        calCard.addView(text("월 달력", 20, Color.rgb(46,60,43), true));
        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setPadding(0, dp(10), 0, dp(8));
        Button prev = pill("‹", false);
        Button next = pill("›", false);
        Button today = pill("오늘", false);
        monthTitle = text("", 18, Color.rgb(46,60,43), true);
        monthTitle.setGravity(Gravity.CENTER);
        nav.addView(prev, new LinearLayout.LayoutParams(dp(50), dp(48)));
        nav.addView(monthTitle, new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(today, new LinearLayout.LayoutParams(dp(88), dp(48)));
        LinearLayout.LayoutParams nextLp = new LinearLayout.LayoutParams(dp(50), dp(48)); nextLp.leftMargin = dp(5);
        nav.addView(next, nextLp);
        calCard.addView(nav);
        calendarGrid = new GridLayout(this);
        calendarGrid.setColumnCount(7);
        calCard.addView(calendarGrid, new LinearLayout.LayoutParams(-1, -2));
        addWithBottom(contentSection, calCard, 12);

        TextView footer = text("v6.7 · 만든이 김지백", 12, Color.rgb(120, 126, 121), false);
        footer.setGravity(Gravity.CENTER_HORIZONTAL);
        footer.setPadding(0, dp(6), 0, 0);
        contentSection.addView(footer, new LinearLayout.LayoutParams(-1, -2));

        markTodayButton.setOnClickListener(v -> markToday());
        shareButton.setOnClickListener(v -> shareCurrentMonthCard());
        prev.setOnClickListener(v -> { shownMonth.add(Calendar.MONTH, -1); reload(); });
        next.setOnClickListener(v -> { shownMonth.add(Calendar.MONTH, 1); reload(); });
        today.setOnClickListener(v -> { shownMonth.setTimeInMillis(System.currentTimeMillis()); shownMonth.set(Calendar.DAY_OF_MONTH, 1); reload(); });

        setContentView(scroll);
    }

    private void animateToCollection() {
        if (transitioning) return;
        transitioning = true;
        float width = getResources().getDisplayMetrics().widthPixels;
        contentSection.animate().translationX(-width).alpha(0.92f).setDuration(220).withEndAction(() -> {
            startActivity(new Intent(this, CollectionActivity.class));
        }).start();
    }

    private void reload() {
        AppData.reconcileUnlocks(this);
        checked = AppData.checked(this);
        unlocked = AppData.unlocked(this);
        renderSummary();
        renderHero();
        renderCalendar();
        updateWidgets();
    }

    private void renderSummary() {
        Calendar now = Calendar.getInstance();
        int y = now.get(Calendar.YEAR), m = now.get(Calendar.MONTH);
        int done = RoutineLogic.checkedInMonth(checked, y, m);
        int days = RoutineLogic.daysInMonth(y, m);
        int percent = Math.round(done * 100f / days);
        int streak = RoutineLogic.currentStreak(checked, now);
        int level = RoutineLogic.growthLevel(done, y, m);
        summaryStats.setText(HeroCatalog.heroForMonth(m).monthLabel + " · 연속 " + streak + "일 · " + percent + "%");
        summarySub.setText("성장 " + level + "/10 · 체크 " + done + "/" + days + "일");

        String todayKey = RoutineLogic.dateKey(now);
        boolean checkedToday = checked.contains(todayKey);
        markTodayButton.setText(checkedToday ? "오늘 완료" : "오늘 체크");
        markTodayButton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_my_calendar, 0, 0, 0);
        styleButton(markTodayButton, checkedToday);

        Hero hero = HeroCatalog.heroForMonth(m);
        boolean shareEnabled = unlocked.contains(hero.id);
        styleButton(shareButton, false);
        shareButton.setAlpha(shareEnabled ? 1f : 0.65f);
    }

    private void renderHero() {
        int y = shownMonth.get(Calendar.YEAR), m = shownMonth.get(Calendar.MONTH);
        Hero hero = HeroCatalog.heroForMonth(m);
        int done = RoutineLogic.checkedInMonth(checked, y, m);
        int level = RoutineLogic.growthLevel(done, y, m);
        int nextLevel = Math.min(level + 1, 10);
        int finalMilestone = RoutineLogic.finalMilestone(y, m);

        UiImageUtil.setFitImage(this, currentHeroImage, RoutineLogic.stageArtRes(level, hero.imageRes));
        heroTitle.setText(hero.monthLabel + " · " + (level >= 10 ? hero.name : "???") + " · Lv." + level + "/10");
        String small = hero.role + " · " + done + "일" + (finalMilestone < 30 ? " · 최종 " + finalMilestone + "일" : "");
        if (level < 10) small += " · 다음 " + hero.rewards[nextLevel - 1];
        heroInfo.setText(small);
        growthProgress.setProgress(level);

        stageStrip.removeAllViews();
        for (int i = 1; i <= 10; i++) {
            boolean reached = level >= i;
            int milestone = RoutineLogic.milestoneForLevel(i, y, m);
            stageStrip.addView(stageCard(i, milestone, reached, hero, i == 10), stageLp());
        }
    }

    private View stageCard(int level, int milestone, boolean reached, Hero hero, boolean finalStage) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(10), dp(10), dp(10), dp(10));
        box.setBackground(bg(reached ? Color.rgb(239, 249, 240) : Color.rgb(249,249,248), 18,
                reached ? Color.rgb(96, 157, 108) : Color.rgb(225,228,225), 1));
        ImageView art = new ImageView(this);
        int artRes = (finalStage && reached) ? hero.imageRes : RoutineLogic.stageArtRes(level, hero.imageRes);
        if (reached) UiImageUtil.setFitImage(this, art, artRes);
        else UiImageUtil.setSilhouette(this, art, artRes);
        box.addView(art, new LinearLayout.LayoutParams(-1, dp(96)));
        TextView a = text(milestone + "일", 12, reached ? Color.rgb(61,120,76) : Color.rgb(130,135,132), true);
        a.setPadding(0, dp(7), 0, 0);
        TextView b = text(reached ? hero.rewards[level - 1] : "미공개", 13, Color.rgb(46,60,43), true);
        box.addView(a); box.addView(b);
        return box;
    }

    private LinearLayout.LayoutParams stageLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(124), -2);
        lp.rightMargin = dp(8);
        return lp;
    }

    private GridLayout.LayoutParams cellParams(int row, int col, int heightDp) {
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.rowSpec = GridLayout.spec(row, 1f);
        lp.columnSpec = GridLayout.spec(col, 1f);
        lp.width = 0;
        lp.height = dp(heightDp);
        int m = dp(2);
        lp.setMargins(m,m,m,m);
        return lp;
    }

    private void renderCalendar() {
        monthTitle.setText(new SimpleDateFormat("yyyy년 M월", Locale.KOREAN).format(shownMonth.getTime()));
        calendarGrid.removeAllViews();
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < 7; i++) {
            TextView h = text(days[i], 12, i == 0 ? Color.rgb(191,74,74) : (i == 6 ? Color.rgb(72,105,177) : Color.rgb(105,110,107)), true);
            h.setGravity(Gravity.CENTER);
            calendarGrid.addView(h, cellParams(0, i, 28));
        }
        Calendar first = (Calendar) shownMonth.clone(); first.set(Calendar.DAY_OF_MONTH, 1);
        int offset = first.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        int max = first.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();
        for (int blank = 0; blank < offset; blank++) {
            TextView empty = text("", 12, Color.TRANSPARENT, false);
            calendarGrid.addView(empty, cellParams(1 + blank / 7, blank % 7, 54));
        }
        for (int day = 1; day <= max; day++) {
            final Calendar date = (Calendar) first.clone(); date.set(Calendar.DAY_OF_MONTH, day);
            final String key = RoutineLogic.dateKey(date);
            boolean done = checked.contains(key);
            boolean isToday = sameDate(date, today);
            boolean isFuture = date.get(Calendar.YEAR) > today.get(Calendar.YEAR)
                    || (date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR));
            TextView cell = text(done ? ("✓\n" + day) : String.valueOf(day), done ? 18 : 16,
                    done ? Color.WHITE : (isFuture ? Color.rgb(175,180,176) : Color.rgb(50,58,53)), done);
            cell.setGravity(Gravity.CENTER);
            android.graphics.drawable.GradientDrawable d = bg(done ? Color.rgb(74,145,92) : Color.WHITE, 15,
                    isToday ? Color.rgb(214,165,53) : (done ? Color.rgb(74,145,92) : Color.rgb(229,232,229)), isToday ? 2 : 1);
            cell.setBackground(d);
            if (!isFuture) cell.setOnClickListener(v -> toggleDate(key, v));
            int pos = offset + day - 1;
            calendarGrid.addView(cell, cellParams(1 + pos / 7, pos % 7, 54));
        }
    }

    private void markToday() {
        Calendar now = Calendar.getInstance();
        int y = now.get(Calendar.YEAR), m = now.get(Calendar.MONTH);
        int oldLevel = RoutineLogic.growthLevel(RoutineLogic.checkedInMonth(checked, y, m), y, m);
        boolean changed = AppData.markToday(this);
        shownMonth.setTimeInMillis(System.currentTimeMillis());
        shownMonth.set(Calendar.DAY_OF_MONTH, 1);
        reload();
        int newLevel = RoutineLogic.growthLevel(RoutineLogic.checkedInMonth(checked, y, m), y, m);
        getWindow().getDecorView().performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        if (changed && newLevel > oldLevel) {
            playEvolutionAnimation();
            Toast.makeText(this, "성장 단계 상승! Lv." + newLevel, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, changed ? "오늘 체크 완료!" : "오늘은 이미 체크되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleDate(String key, View v) {
        int y = shownMonth.get(Calendar.YEAR), m = shownMonth.get(Calendar.MONTH);
        int oldLevel = RoutineLogic.growthLevel(RoutineLogic.checkedInMonth(checked, y, m), y, m);
        boolean added = AppData.toggleDate(this, key);
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        reload();
        int newLevel = RoutineLogic.growthLevel(RoutineLogic.checkedInMonth(checked, y, m), y, m);
        if (added && newLevel > oldLevel) {
            playEvolutionAnimation();
            Toast.makeText(this, "성장 단계 상승! Lv." + newLevel, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, added ? "스탬프 완료 ✓" : "스탬프 취소", Toast.LENGTH_SHORT).show();
        }
    }

    private void playEvolutionAnimation() {
        AnimationSet set = new AnimationSet(true);
        AlphaAnimation alpha = new AlphaAnimation(0.35f, 1f);
        alpha.setDuration(480);
        ScaleAnimation scale = new ScaleAnimation(0.92f, 1.06f, 0.92f, 1.06f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(480);
        set.addAnimation(alpha);
        set.addAnimation(scale);
        currentHeroImage.startAnimation(set);
        heroTitle.startAnimation(set);
    }

    private boolean sameDate(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private void shareCurrentMonthCard() {
        Calendar now = Calendar.getInstance();
        Hero hero = HeroCatalog.heroForMonth(now.get(Calendar.MONTH));
        if (!unlocked.contains(hero.id)) {
            Toast.makeText(this, "최종 완성 후 공유할 수 있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        CardShare.shareHeroCard(this, hero, checked);
    }

    private void updateWidgets() {
        AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        ComponentName cn = new ComponentName(this, RoutineWidgetProvider.class);
        int[] ids = mgr.getAppWidgetIds(cn);
        for (int id : ids) RoutineWidgetProvider.updateWidget(this, mgr, id);
    }
}
