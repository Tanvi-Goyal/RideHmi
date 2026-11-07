# AGENTS.md

Guidance for anyone, human or AI agent, changing RideHmi. The plan and open decisions are in
[docs/plan.md](docs/plan.md).

## Project map

| Module | Holds | Depends on |
|---|---|---|
| `:app` | Activity, Application (Hilt root) | everything it wires |
| `:feature:dashboard:ui` | Dashboard screen, ViewModel, UI state, mapper | `:core:domain`, `:core:designsystem` |
| `:core:domain` (JVM) | Repository port, validator, `RideStateMachine`, use cases | `:core:model` |
| `:core:model` (JVM) | Plain Kotlin models | nothing |
| `:core:data` | Simulated data source, repository impl, Hilt modules | `:core:domain` |
| `:core:designsystem` | Theme, colours, text styles, strings | nothing project-level |
| `:core:testing` (JVM) | Fakes and test rules | test classpaths only |
| `build-logic` | `ridehmi.*` convention plugins | n/a |

## Architecture rules

- Dependencies point inward. Features never depend on `:core:data`; `:app` connects them.
- Domain and model stay pure Kotlin (no Android imports), so their tests run on the JVM.
- The state machine is a pure function; all time-based behaviour lives in the use case and is tested with virtual time.
- Time comes from `MonotonicClock`, dispatchers from `@Dispatcher` qualifiers, never directly from the system.
- ViewModels expose one `StateFlow` of UI state. Composables are stateless and take that state.
- Invalid or missing data is a state to render, never an exception.
- Versions live in `gradle/libs.versions.toml`; modules apply convention plugins.

## Code style

- Kotlin official style. Comments only to explain *why*.
- User-facing strings in `:core:designsystem` resources.

## Testing

- Use the fakes in `:core:testing`. No `Thread.sleep` or real clocks; use `runTest` virtual time.
- A bug fix starts with a test that fails without it.

```bash
./gradlew testDebugUnitTest :core:model:test :core:domain:test :app:assembleDebug
```

## Git workflow

- Branches `<type>/<kebab-description>`; Conventional Commits; one logical change per commit.
