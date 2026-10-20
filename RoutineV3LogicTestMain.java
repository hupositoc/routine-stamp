import com.openai.routinequest.RoutineLogic;
import java.util.*;

public class RoutineV3LogicTestMain {
    static int tests = 0;
    static void ok(boolean cond, String name) {
        tests++;
        if (!cond) throw new AssertionError(name);
    }
    static Calendar d(int y,int m,int day){ Calendar c=Calendar.getInstance(); c.clear(); c.set(y,m-1,day,12,0,0); return c; }
    public static void main(String[] args) {
        ok(RoutineLogic.growthLevel(0, 2026, 7) == 0, "0 days prologue");
        ok(RoutineLogic.growthLevel(2, 2026, 7) == 0, "2 days prologue");
        ok(RoutineLogic.growthLevel(3, 2026, 7) == 1, "3 days level1");
        ok(RoutineLogic.growthLevel(6, 2026, 7) == 2, "6 days level2");
        ok(RoutineLogic.growthLevel(9, 2026, 7) == 3, "9 days level3");
        ok(RoutineLogic.growthLevel(12, 2026, 7) == 4, "12 days level4");
        ok(RoutineLogic.growthLevel(15, 2026, 7) == 5, "15 days level5");
        ok(RoutineLogic.growthLevel(18, 2026, 7) == 6, "18 days level6");
        ok(RoutineLogic.growthLevel(21, 2026, 7) == 7, "21 days level7");
        ok(RoutineLogic.growthLevel(24, 2026, 7) == 8, "24 days level8");
        ok(RoutineLogic.growthLevel(27, 2026, 7) == 9, "27 days level9");
        ok(RoutineLogic.growthLevel(30, 2026, 7) == 10, "30 days level10");
        ok(RoutineLogic.finalMilestone(2026, 1) == 28, "Feb 2026 final 28");
        ok(RoutineLogic.growthLevel(27, 2026, 1) == 9, "Feb 27 level9");
        ok(RoutineLogic.growthLevel(28, 2026, 1) == 10, "Feb 28 level10");
        ok(RoutineLogic.finalMilestone(2028, 1) == 29, "leap Feb final 29");
        ok(RoutineLogic.growthLevel(29, 2028, 1) == 10, "leap Feb level10");
        ok(RoutineLogic.finalMilestone(2026, 3) == 30, "30 day month final30");
        ok(RoutineLogic.nextMilestone(17, 2026, 7) == 18, "next milestone");
        Set<String> s=new HashSet<>(); s.add("2026-08-08"); s.add("2026-08-09"); s.add("2026-08-10");
        ok(RoutineLogic.checkedInMonth(s,2026,7)==3,"month count");
        ok(RoutineLogic.currentStreak(s,d(2026,8,11))==3,"pending today streak");
        System.out.println("RoutineStamp v3 logic tests: PASS (" + tests + " checks)");
    }
}
