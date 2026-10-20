package com.openai.routinequest;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Set;

public final class CardShare {
    private CardShare() {}

    public static void shareHeroCard(Activity activity, Hero hero, Set<String> checked) {
        try {
            Uri uri = createHeroCardImage(activity, hero, checked);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT, hero.monthLabel + " 영웅 " + hero.name + " 완성! #나의루틴모험");
            intent.setClipData(ClipData.newRawUri("character_card", uri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, "완성 캐릭터 카드 공유"));
        } catch (Exception e) {
            Toast.makeText(activity, "이미지 카드 생성 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public static Uri createHeroCardImage(Activity activity, Hero hero, Set<String> checked) throws Exception {
        final int w = 1080, h = 1440;
        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(out);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        p.setShader(new LinearGradient(0, 0, w, h, new int[]{Color.rgb(241,248,238), Color.rgb(245,239,220), Color.rgb(54,96,65)}, null, Shader.TileMode.CLAMP));
        c.drawRect(0,0,w,h,p);
        p.setShader(null);

        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(16); p.setColor(Color.rgb(210,166,58));
        c.drawRoundRect(new RectF(34,34,w-34,h-34), 48,48,p);
        p.setStrokeWidth(4); p.setColor(Color.rgb(59,112,71));
        c.drawRoundRect(new RectF(58,58,w-58,h-58), 40,40,p);
        p.setStyle(Paint.Style.FILL);

        drawCentered(c, p, "MONTHLY HERO", w/2f, 138, 42, Color.rgb(59,112,71), true);
        drawCentered(c, p, hero.monthLabel + " · " + hero.role, w/2f, 205, 34, Color.rgb(71,80,73), false);

        Bitmap heroBmp = BitmapFactory.decodeResource(activity.getResources(), hero.imageRes);
        drawBitmapFit(c, heroBmp, new RectF(150,250,930,940), p);

        p.setColor(Color.argb(235, 33, 73, 45));
        c.drawRoundRect(new RectF(125,880,955,1055), 34,34,p);
        drawCentered(c, p, "10 / 10 COMPLETE", w/2f, 935, 34, Color.rgb(245,222,142), true);
        drawCentered(c, p, hero.name, w/2f, 1005, 68, Color.WHITE, true);

        Calendar now = Calendar.getInstance();
        int streak = RoutineLogic.currentStreak(checked, now);
        int done = RoutineLogic.checkedInMonth(checked, now.get(Calendar.YEAR), now.get(Calendar.MONTH));
        int days = RoutineLogic.daysInMonth(now.get(Calendar.YEAR), now.get(Calendar.MONTH));
        int percent = Math.round(done * 100f / days);

        p.setColor(Color.argb(225,250,248,239));
        c.drawRoundRect(new RectF(105,1080,975,1345), 30,30,p);
        drawCentered(c,p, hero.tagline, w/2f,1145,31,Color.rgb(54,64,57),false);
        drawCentered(c,p, "최종 장비 · " + hero.rewards[9], w/2f,1210,28,Color.rgb(59,112,71),true);
        drawCentered(c,p, "연속 " + streak + "일 · 이번 달 " + percent + "%", w/2f,1275,28,Color.rgb(54,64,57),false);
        drawCentered(c,p, "작은 습관이 위대한 모험이 됩니다.", w/2f,1335,24,Color.rgb(95,101,97),false);
        drawCentered(c,p, "나의 루틴 모험", w/2f,1390,28,Color.rgb(244,225,157),true);

        File dir = new File(activity.getCacheDir(), "shared_cards");
        if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("cache directory");
        File file = new File(dir, "hero_" + hero.id + ".png");
        FileOutputStream fos = new FileOutputStream(file);
        out.compress(Bitmap.CompressFormat.PNG, 96, fos);
        fos.flush(); fos.close();
        return Uri.parse("content://com.openai.routinequest.share/" + file.getName());
    }

    private static void drawBitmapFit(Canvas c, Bitmap bmp, RectF dst, Paint p) {
        if (bmp == null) return;
        float srcRatio = bmp.getWidth() / (float) bmp.getHeight();
        float dstRatio = dst.width() / dst.height();
        Rect src;
        if (srcRatio > dstRatio) {
            int newW = Math.round(bmp.getHeight() * dstRatio);
            int left = (bmp.getWidth() - newW) / 2;
            src = new Rect(left, 0, left + newW, bmp.getHeight());
        } else {
            int newH = Math.round(bmp.getWidth() / dstRatio);
            int top = (bmp.getHeight() - newH) / 2;
            src = new Rect(0, top, bmp.getWidth(), top + newH);
        }
        c.drawBitmap(bmp, src, dst, p);
    }

    private static void drawCentered(Canvas c, Paint p, String s, float x, float y, float size, int color, boolean bold) {
        p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); p.setTextSize(size); p.setTextAlign(Paint.Align.CENTER); p.setFakeBoldText(bold);
        if (p.measureText(s) <= 900) { c.drawText(s, x, y, p); return; }
        float scale = 900f / p.measureText(s);
        p.setTextSize(size * scale);
        c.drawText(s, x, y, p);
    }
}
