package viz;

public interface Stepper {
    boolean isFinished();
    StepEvent step();   // מחזיר אירוע אחד; אם הסתיים – יחזיר DONE פעם אחת ואז יישאר finished=true
    void reset();       // לאפס מצב פנימי (אופציונלי לשימוש עתידי)
}
