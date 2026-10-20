# Engineering decision record

## Context

Calendar boundaries and progression rules can disagree across screens and widgets.

## Decision

Exercise shared routine logic independently of the Android runtime.

## Consequences and evidence

Twenty-seven logic checks pass; Android lifecycle and widget behavior require device validation.

See [architecture](architecture.md), [validation](validation.md) and the [case study](case-study.md) for implementation details and limits.
