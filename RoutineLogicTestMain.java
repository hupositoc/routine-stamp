import com.openai.routinequest.RoutineLogic;
import java.util.*;
public class RoutineLogicTestMain {
  static void ok(boolean b, String m){ if(!b) throw new AssertionError(m); }
  static Calendar d(int y,int m,int day){ Calendar c=Calendar.getInstance(); c.clear(); c.set(y,m-1,day,12,0,0); return c; }
  public static void main(String[] args){
    Set<String> s=new HashSet<>();
    s.add("2026-08-08"); s.add("2026-08-09"); s.add("2026-08-10");
    ok(RoutineLogic.checkedInMonth(s,2026,7)==3,"month count");
    ok(RoutineLogic.daysInMonth(2026,7)==31,"aug days");
    ok(RoutineLogic.daysInMonth(2028,1)==29,"leap feb");
    ok(RoutineLogic.currentStreak(s,d(2026,8,11))==3,"pending-today streak");
    s.add("2026-08-11"); ok(RoutineLogic.currentStreak(s,d(2026,8,11))==4,"today streak");
    s.remove("2026-08-10"); ok(RoutineLogic.currentStreak(s,d(2026,8,11))==1,"gap streak");
    System.out.println("RoutineLogic tests: PASS (6 checks)");
  }
}
