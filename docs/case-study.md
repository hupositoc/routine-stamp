# Engineering case study: RoutineStamp

## 1. The task and operating context

Make a daily habit visible through a calendar, current streak, growth milestones, character collection and an Android home-screen widget.

A personal life-management app. Its purpose is to support a repeatable routine through feedback, not to claim scientifically measured habit improvement.

## 2. The problem before the tool

A generic checklist records completion but may provide little sense of continuity. A monthly routine also introduces edge cases: month length, leap years, a day that has not yet ended, gaps in a streak and progress thresholds near the end of February.

## 3. The intervention

Keep date/progression calculations in a Java logic class, persist checked dates and unlocked characters separately, and let the activity and widget render that state. Use growth milestones and a monthly collection as feedback rather than hiding the underlying calendar record.

### Input → decision → output

1. **Input:** Daily check-in.
2. **Processing:** Persist date state.
3. **Interpretation:** Calculate progression.
4. **Output:** Render app / widget.

This workflow is illustrated in the README and is mapped to actual source entry points in [architecture](architecture.md).

## 4. Work enabled by the implementation

- Record and review daily completion in a monthly calendar.
- See streak and growth state calculated from explicit date rules.
- Expose progress through a widget and share a generated character card.

The source includes the calendar, progression, collection, share-card and widget paths. Existing tests verify the date and growth calculations in isolation. No usage study, retention improvement or behavioral outcome is claimed.

## 5. Technical choices and tradeoffs

### Make date semantics explicit

A streak should not disappear in the morning merely because today has not yet been checked. Leap years and short months are part of the core logic.

### Separate state from presentation

The logic class can be exercised outside Android, while activities and widgets render a shared source of progress.

### Keep historical version evidence

The manifest identifies 6.7, while older build notes refer to 6.6. The public documentation explains this instead of silently rewriting the development history.

## 6. Engineering contribution

Represent daily completion, streak progression and character growth in an Android app. The linked architecture document identifies implementation entry points; validation records distinguish source inspection, executable checks and hardware-dependent behavior.

## 7. Evidence and scope

The two existing standalone suites cover date counting, leap/month boundaries, progression milestones, next milestone and streak behavior. A tiny drawable-ID fixture resolves generated resource references only; it does not emulate Android.

- The archive uses src/res/AndroidManifest layout and is not yet a self-contained modern Gradle project.
- Android screen, widget and share-provider behavior were not tested on a device here.
- Character-art provenance and redistribution conditions require confirmation before broad reuse.
- The inherited package identifier does not indicate affiliation with the organization named in it.

## 8. Next measurable milestone

- [ ] Migrate to a reproducible Gradle build without changing calendar semantics.
- [ ] Add widget and activity instrumentation tests.
- [ ] Document asset provenance and accessibility checks for text scale and touch targets.

## Before and after the intervention

| Before | Implementation | Work enabled and evidence |
|---|---|---|
| Routine completion and progress needed a consistent personal tracking workflow. | Represent daily completion, streak progression and character growth in an Android app. | The implementation connects routine records to visible progress and widget-oriented use; behavioral improvement is not quantified. |
