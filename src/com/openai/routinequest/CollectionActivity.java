package com.openai.routinequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class CollectionActivity extends Activity {
    private Set<String> checked = new HashSet<>();
    private Set<String> unlocked = new HashSet<>();
    private GridLayout collectionGrid;
    private TextView headerCount;
    private LinearLayout contentSection;

    private int dp(float v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(49, 100, 67));
        getWindow().setNavigationBarColor(Color.rgb(248, 247, 242));
        buildUi();
        reload();
    }

    @Override protected void onResume() {
        super.onResume();
        if (contentSection != null) {
            contentSection.setTranslationX(0f);
            contentSection.setAlpha(1f);
        }
        reload();
    }

    private TextView text(String value, float sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value); v.setTextSize(sp); v.setTextColor(color);
        if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD);
        return v;
    }

    private android.graphics.drawable.GradientDrawable bg(int fill, float radiusDp, int strokeColor, float strokeDp) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(fill); d.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) d.setStroke(dp(strokeDp), strokeColor);
        return d;
    }

    private Button pill(String label, boolean active) {
        Button b = new Button(this);
        b.setText(label); b.setAllCaps(false); b.setTextSize(13);
        b.setGravity(Gravity.CENTER);
        b.setTextColor(active ? Color.WHITE : Color.rgb(46,60,43));
        b.setPadding(dp(12), dp(8), dp(12), dp(8));
        b.setMinHeight(0); b.setMinimumHeight(0);
        b.setBackground(bg(active ? Color.rgb(74,145,92) : Color.WHITE, 16, active ? Color.rgb(74,145,92) : Color.rgb(220,226,220), 1));
        return b;
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(28));
        root.setBackgroundColor(Color.rgb(248,247,242));
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));

        root.addView(text("캐릭터 보관함", 28, Color.rgb(36,50,39), true));
        TextView sub = text("잠긴 캐릭터는 실루엣으로만 보입니다.", 13, Color.rgb(96,105,99), false);
        sub.setPadding(0, dp(4), 0, dp(10));
        root.addView(sub);

        LinearLayout menu = new LinearLayout(this);
        Button home = pill("홈", false);
        Button collection = pill("보관함", true);
        menu.addView(home, weightedLp(1, 6, 48));
        menu.addView(collection, weightedLp(1, 0, 48));
        home.setOnClickListener(v -> animateToHome());
        addWithBottom(root, menu, 12);

        contentSection = new LinearLayout(this);
        contentSection.setOrientation(LinearLayout.VERTICAL);
        root.addView(contentSection, new LinearLayout.LayoutParams(-1, -2));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        card.setBackground(bg(Color.WHITE, 20, Color.rgb(230,232,228), 1));
        headerCount = text("", 18, Color.rgb(39,88,57), true);
        card.addView(headerCount);
        collectionGrid = new GridLayout(this);
        collectionGrid.setColumnCount(2);
        LinearLayout.LayoutParams gridLp = new LinearLayout.LayoutParams(-1, -2);
        gridLp.topMargin = dp(12);
        card.addView(collectionGrid, gridLp);
        addWithBottom(contentSection, card, 12);

        TextView footer = text("v6.7 · 만든이 김지백", 12, Color.rgb(120, 126, 121), false);
        footer.setGravity(Gravity.CENTER_HORIZONTAL);
        contentSection.addView(footer, new LinearLayout.LayoutParams(-1, -2));

        setContentView(scroll);
    }

    private void addWithBottom(LinearLayout root, android.view.View view, int marginDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(marginDp);
        root.addView(view, lp);
    }

    private LinearLayout.LayoutParams weightedLp(float weight, int rightMargin, int heightDp) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(heightDp), weight);
        lp.rightMargin = dp(rightMargin);
        return lp;
    }

    private void animateToHome() {
        float width = getResources().getDisplayMetrics().widthPixels;
        contentSection.animate().translationX(width).alpha(0.92f).setDuration(220).withEndAction(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }).start();
    }

    private void reload() {
        AppData.reconcileUnlocks(this);
        checked = AppData.checked(this);
        unlocked = AppData.unlocked(this);
        headerCount.setText(unlocked.size() + " / 12 해제됨");
        renderCollection();
    }

    private void renderCollection() {
        collectionGrid.removeAllViews();
        for (int i = 0; i < HeroCatalog.HEROES.length; i++) {
            final Hero hero = HeroCatalog.HEROES[i];
            boolean isUnlocked = unlocked.contains(hero.id);
            LinearLayout box = new LinearLayout(this);
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(10), dp(10), dp(10), dp(10));
            box.setBackground(bg(isUnlocked ? Color.rgb(239,249,240) : Color.rgb(249,249,248), 17,
                    isUnlocked ? Color.rgb(96,157,108) : Color.rgb(226,229,226), 1));

            ImageView art = new ImageView(this);
            if (isUnlocked) UiImageUtil.setFitImage(this, art, hero.imageRes);
            else UiImageUtil.setSilhouette(this, art, hero.imageRes);
            box.addView(art, new LinearLayout.LayoutParams(-1, dp(160)));

            TextView n = text(isUnlocked ? (hero.monthLabel + " · " + hero.name) : (hero.monthLabel + " · ???"), 14, Color.rgb(46,60,43), true);
            n.setPadding(0, dp(7), 0, dp(2));
            n.setGravity(Gravity.CENTER);
            TextView r = text(isUnlocked ? hero.role : "미공개", 12, Color.rgb(95,101,97), false);
            r.setGravity(Gravity.CENTER);
            box.addView(n); box.addView(r);

            if (isUnlocked) {
                Button action = pill("공유", false);
                action.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_share, 0, 0, 0);
                action.setCompoundDrawablePadding(dp(6));
                action.setOnClickListener(v -> CardShare.shareHeroCard(this, hero, checked));
                LinearLayout.LayoutParams aLp = new LinearLayout.LayoutParams(-1, dp(48));
                aLp.topMargin = dp(8);
                box.addView(action, aLp);
            }

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.columnSpec = GridLayout.spec(i % 2, 1f);
            lp.rowSpec = GridLayout.spec(i / 2);
            lp.width = 0; lp.height = -2;
            int m = dp(4); lp.setMargins(m,m,m,m);
            collectionGrid.addView(box, lp);
        }
    }
}
