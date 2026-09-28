# CS4928 Lab 2 — Evidence

## Baseline
- Pinned SHA: `cf437186b2ae2d9100a187e0df3cce3a163e9b4b` (confirmed with `git rev-parse HEAD`)
- Upstream `mvn test`: PASS — Tests run: 138, Failures: 0, Errors: 0, Skipped: 0
- `mvn -Dtest=CS4928Lab2CheckTest test` on the unchanged baseline: PASS — Tests run: 5, Failures: 0, Errors: 0
- Tag `CS4928_BASELINE` created on the pinned SHA

## Problem A
Problem A: validateName mixes general validation with package-type-specific normalisation. Goal: move the varying name-normalisation algorithm behind a stable policy while preserving every existing result.

Intended structure: a package-private `PackageNamePolicy` interface (`String normalize(String value)`) and a `PackageNamePolicies` resolver holding four reusable policies (PRESERVE, LOWERCASE, PUB, PYPI) plus `forType(type)`, which keeps exactly the same package-type groups as the original switch.

## Refactoring A
- Files: `PackageNamePolicy.java` (new), `PackageNamePolicies.java` (new), `PackageURL.java` (`validateName` simplified)
- Tests after the change: `mvn test` PASS (0 failures, 0 errors); `CS4928Lab2CheckTest` PASS; `git diff --check` clean
- Commit: `c1176791178648c5a00d3f2afd4ca868f21bec6b` — "Lab 2A: extract package name policy"
- Before → after: the type-specific normalisation algorithms and the decision of which one applies moved out of `validateName` into `PackageNamePolicies`; `validateName` now only rejects an empty name and delegates, and every package type still produces exactly the same name (GitHub-style types lowercased, PUB/PYPI special rules kept, RPM preserved).

## Problem B
Both `parseQualifiers(Map)` and `parseQualifiers(String)` express the rule "qualifier keys are lowercase" separately, so the rule had two sources of truth. The two input paths must still stay separate: the encoded-string path splits on `&`/`=`, percent-decodes values and rejects duplicate keys after lowercasing, while the Map path filters out empty values and lets later entries overwrite earlier ones.

## Refactoring B
- Helper: `private static String canonicalizeQualifierKey(String key)` in `PackageURL.java`
- Tests after the change: `mvn test` PASS (0 failures, 0 errors); `CS4928Lab2CheckTest` PASS; `git diff --check` clean
- Commit: `0ab2da0e006ec10458326ca84304ee2bad51fd85` — "Lab 2B: centralise qualifier key canonicalisation"
- Before → after: key lowercasing moved from two inline expressions into one helper; empty-value filtering (Map path) and duplicate-key detection (String path) stay exactly where they were, and the two methods were not merged.

## Comparison with baseline
`git diff --stat CS4928_BASELINE..HEAD`: 4 files changed, 195 insertions(+), 27 deletions(-). `git diff --check CS4928_BASELINE..HEAD`: clean.

- Refactoring A: the choice of name-normalisation algorithm per package type is now local to `PackageNamePolicies`, so changing or adding a type's name rule touches one place. Extra structure introduced: one interface, one resolver class, and an extra indirection when reading `validateName`.
- Refactoring B: "qualifier keys are lowercase" now has a single source of truth (`canonicalizeQualifierKey`). The two parsing algorithms deliberately remain separate because they handle different inputs and have different duplicate-key and empty-value semantics.
- Passing tests are necessary because they show that the behaviour they cover was preserved. They do not prove the design is better: tests say nothing about cohesion, readability or ease of future change, and they only cover the cases someone wrote. Design quality has to be judged by inspecting the diff and reasoning about likely future changes.

## Problem C
The wider package-type variation in `validateNamespace`, `validateVersion` and `verifyTypeConstraints` is left for later because a bounded, small refactoring is easier to verify and understand, whereas changing that whole axis at once would mix several behaviours and make any regression harder to attribute.

## Final judgement
Yes, I would keep both changes in a real pull request. Benefit: each varying or shared rule now lives in one place, so future changes are smaller and easier to review. Trade-off: Refactoring A adds two files and an indirection for what was a short switch, which only pays off if more type-specific name rules are expected.

## Repository
- URL: https://github.com/Xll2004/cs4928-lab2.git
- Refactoring B commit SHA: `0ab2da0e006ec10458326ca84304ee2bad51fd85`
- The final HEAD SHA (after this evidence commit) is shown to the coordinator with `git rev-parse HEAD` and is intentionally not stored in this file.
