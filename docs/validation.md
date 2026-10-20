# Validation and evidence record

## Evidence vocabulary

| Label | Meaning |
|---|---|
| Source inspected | A mechanism exists in the cited implementation; this alone is not proof of successful execution. |
| Locally executed | A named check was run in the current preparation environment, with its scope stated. |
| Author-reported use | Historical operating context supplied by the author; private reports are not published. |
| Pending | A check has not been reproduced and is not presented as a result. |

## Current record

The two existing standalone suites cover date counting, leap/month boundaries, progression milestones, next milestone and streak behavior. A tiny drawable-ID fixture resolves generated resource references only; it does not emulate Android.

**Historical/context statement:** The source includes the calendar, progression, collection, share-card and widget paths. Existing tests verify the date and growth calculations in isolation. No usage study, retention improvement or behavioral outcome is claimed.

## Integration verification checklist

- [ ] Record exact runtime, dependency versions, input/configuration and date.
- [ ] Run a nominal case with a predictable expected output.
- [ ] Exercise an invalid input or interruption and inspect the failure state.
- [ ] Retain a shareable output and the procedure that produced it.
- [ ] Compare the result with the original task, rather than only checking that the program launched.

## Claims deliberately kept separate

- The archive uses src/res/AndroidManifest layout and is not yet a self-contained modern Gradle project.
- Android screen, widget and share-provider behavior were not tested on a device here.
- Character-art provenance and redistribution conditions require confirmation before broad reuse.
- The inherited package identifier does not indicate affiliation with the organization named in it.

An expected output, architecture drawing or sample configuration is not a measured result. This document is the place to add test logs when integration work is actually completed.

## Executed standalone suites

`python tools/run_logic_tests.py` passed the existing RoutineLogicTestMain (**6 checks**) and RoutineV3LogicTestMain (**21 checks**) on JDK 21. The fixture supplies drawable integer IDs solely for Java linkage. It does not replace Android UI, widget, persistence or share-provider integration tests.
