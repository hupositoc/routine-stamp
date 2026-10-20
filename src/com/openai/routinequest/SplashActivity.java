package com.openai.routinequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(49, 100, 67));
        getWindow().setNavigationBarColor(Color.rgb(49, 100, 67));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.rgb(49, 100, 67));
        root.setPadding(40, 40, 40, 40);

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.app_icon);
        root.addView(icon, new LinearLayout.LayoutParams(220, 220));

        TextView title = new TextView(this);
        title.setText("나의 루틴 모험");
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(-2, -2); tlp.topMargin = 28;
        root.addView(title, tlp);

        TextView sub = new TextView(this);
        sub.setText("작은 체크가 영웅을 성장시킵니다.");
        sub.setTextSize(14);
        sub.setTextColor(Color.argb(220,255,255,255));
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-2, -2); slp.topMargin = 12;
        root.addView(sub, slp);

        setContentView(root);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1200);
    }
}
