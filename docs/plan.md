# RideHmi plan

Landscape Android HMI dashboard: speed (km/h), motor ON/OFF, kill switch Active/Inactive, driven by a
simulated vehicle feed about once a second. MVP first: the mandatory requirements, done well.

## Grading weights to optimise for

Functional correctness 30 · State management 25 · Architecture 20 · Testing and edge cases 15 · Code and docs 10

## Architecture

```
:app ──► :feature:dashboard:ui ──► :core:domain ──► :core:model
  │              │                     ▲
  │              └──► :core:designsystem
  └──► :core:data ─────────────────────┘
:core:testing (fakes, rules) → test classpaths only
build-logic (ridehmi.* convention plugins)
```

| Module | Holds |
|---|---|
| `:core:model` (JVM) | `VehicleReading` (raw), `Signal<T>`, `VehicleStatus`, `RideState`, `VehicleFeed`, `MonotonicClock` |
| `:core:domain` (JVM) | `VehicleRepository` port, `VehicleSignalValidator`, `RideStateMachine`, `ObserveVehicleFeed`, `@Dispatcher` |
| `:core:data` | `VehicleDataSource` + `SimulatedVehicleDataSource`, `VehicleRepositoryImpl`, Hilt modules (dispatcher, clock) |
| `:core:designsystem` | Always-dark HMI theme, status colours, text styles, strings |
| `:feature:dashboard:ui` | `DashboardViewModel`, `DashboardUiState`, mapper, `DashboardScreen` |
| `:app` | `HmiApplication` (Hilt root), `MainActivity` (landscape, keep screen on) |

Data flow: `SimulatedVehicleDataSource` → `VehicleRepository` (flowOn Default) → `ObserveVehicleFeed`
(order check → validate → state machine → staleness → error recovery) → `DashboardViewModel`
(`stateIn`, `WhileSubscribed(5s)`) → `collectAsStateWithLifecycle` → stateless `DashboardScreen`.

## Phases

Each phase: build → tests green → you review → next.

### Phase 0 — Boilerplate (done)
Modules, convention plugins, version catalog, Hilt wiring, contracts and stubs. App launches in
landscape showing "Connecting to vehicle…". One smoke test.

### Phase 1 — Data layer

Goal: a simulator that plays a script of raw readings, about one a second, on a background
dispatcher. The data layer passes values through **unchanged**, including bad ones; judging them
is the domain's job (Phase 2). Done when every item below has a passing test.

#### 1.1 Script model — `core/data/.../simulation/`
- `ScenarioStep` (sealed, internal):
  - `Emit(label, speedKmh: Float?, motorOn: Boolean?, killSwitchActive: Boolean?, holdTicks: Int = 1, timestampSkewMillis: Long = 0)`;
    `init { require(holdTicks >= 1) }`. `null` = signal missing from that reading.
  - `Silence(ticks: Int)`: emit nothing for that long (stale-data case).
  - `Fail(message)`: throw `VehicleBusException` (source failure case).
- `SimulationConfig(script: List<ScenarioStep>, tick: Duration = 1.seconds, loop: Boolean = true)`.
- `VehicleBusException(message) : IOException`.

#### 1.2 Normal script — `RideScripts.normalRide` (17 readings, then loops)

| Step | Readings | Speed | Motor | Kill switch | State the domain will show |
|---|---|---|---|---|---|
| Parked | 3 | 0 | OFF | Inactive | PARKED |
| Ready | 3 | 0 | ON | Inactive | READY |
| Riding | 3 | 25 | ON | Inactive | ACCELERATING once (0→25), then RIDING ×2 |
| Accelerating | 5 | 30, 35, 40, 45, 50 | ON | Inactive | ACCELERATING ×5 |
| Motor disabled | 3 | 0 | OFF | Active | MOTOR_DISABLED |

Why a ramp for Accelerating: with the speed-change rule (decision 1), holding 50 would show RIDING
from the second reading. A rising ramp keeps it ACCELERATING and ends at the doc's 50 km/h. The ramp
step (5 km/h) must stay above the Phase 2 acceleration threshold.

#### 1.3 Fault script — `RideScripts.withFaults` = one normal ride, then:

| Step | Readings | Raw values | Edge case it exercises |
|---|---|---|---|
| Coasting | 2 | 20, OFF, Inactive | Motor OFF while moving (decision 2) |
| Negative speed | 2 | -5, ON, Inactive | Invalid → "--" |
| Sensor noise | 1 | -0.4, ON, Inactive | Clamp-to-0 rule (decision 5) |
| Not a number | 1 | NaN, ON, Inactive | Invalid → "--" |
| Implausible | 1 | 400, ON, Inactive | Above max → "--" |
| Speed missing | 2 | null, ON, Inactive | Missing → "--", others still shown |
| Motor missing | 1 | 25, null, Inactive | Missing motor |
| Out of order | 1 | 25, ON, Inactive, timestamp −1500 ms | Dropped by the domain |
| Silence | 5 ticks | — | Stale → SignalLost after 3 s, then recovers |
| Fail | — | throws | Domain catches, shows SignalLost, retries |

`Fail` is last on purpose: the domain's retry re-collects the cold flow, which restarts the script,
so recovery and looping are the same thing.

#### 1.4 `SimulatedVehicleDataSource`
```kotlin
override fun readings(): Flow<VehicleReading> = flow {
    do {
        for (step in config.script) when (step) {
            is Emit -> repeat(step.holdTicks) {
                emit(step.toReading(clock.nowMillis() + step.timestampSkewMillis))
                delay(config.tick)
            }
            is Silence -> delay(config.tick * step.ticks)
            is Fail -> throw VehicleBusException(step.message)
        }
    } while (config.loop)
}
```
- Emits first, then waits, so the screen leaves "Connecting" straight away instead of after 1 s.
- Cold flow: each collector gets its own run from the start. `delay` keeps it cancellable.
- Timestamps come from the injected `MonotonicClock` (`SystemClock.elapsedRealtime` in the app), not wall time.
- "About once a second": `delay` after each emit can drift by a few ms per tick. That's fine for a
  display; the domain uses reading timestamps, not tick counts.

#### 1.5 Repository and DI
- `VehicleRepositoryImpl.observeReadings() = dataSource.readings().flowOn(Default)` (already in place).
  No conflation needed at 1 Hz; the ViewModel's `StateFlow` conflates at the end.
- `SimulationModule` (`@Provides SimulationConfig`): picks `withFaults` when
  `BuildConfig.SIMULATE_FAULTS` is true, otherwise `normalRide`.
- `:core:data` gets `buildConfig = true` and
  `buildConfigField("boolean", "SIMULATE_FAULTS", <gradle property ridehmi.simulateFaults, default false>)`.
  Run with faults: `./gradlew installDebug -Pridehmi.simulateFaults=true`.

#### 1.6 Tests — `core/data/src/test/` (JVM, `runTest` virtual time, clock = `testScheduler.currentTime`)

`RideScriptsTest`
- normal ride plays Parked → Ready → Riding → Accelerating → Motor disabled with the exact values above
- Riding holds 25 for at least 3 readings (decision 1)
- Accelerating ramp is strictly rising and ends at 50
- `Emit` with `holdTicks = 0` is rejected

`SimulatedVehicleDataSourceTest`
- first reading at t = 0, then one every 1000 ms
- one full cycle is exactly the 17 expected readings, in order
- loops: reading 18 is Parked again
- timestamps come from the clock and rise strictly (apart from the skewed step)
- `Silence(5)` leaves a 5 s gap with no readings
- `Fail` ends the flow with `VehicleBusException`
- bad values (−5, NaN, 400, null) come out unchanged
- `loop = false` completes after one pass

`VehicleRepositoryImplTest`
- passes readings through unchanged from a fake data source
- runs the source on the injected dispatcher

#### 1.7 Verify
- `./gradlew :core:data:testDebugUnitTest` green, then the full command in AGENTS.md.
- No on-device check yet: `ObserveVehicleFeed` is still a stub, so the screen stays on
  "Connecting" until Phase 2.
- Suggested branch and commits: `feat/data-simulator`
  (`feat: add ride scenario script`, `feat: simulate vehicle readings`, `feat: add fault script behind a build flag`).

#### Not in Phase 1
- Sharing one source between many collectors (`shareIn`): only one screen collects today.
- A coast-down ramp after the kill switch (50 → 0): the doc's step goes straight to 0.

### Phase 2 — Domain
- `VehicleSignalValidator`: speed NaN/∞ → Invalid(NOT_A_NUMBER); < 0 → Invalid(NEGATIVE);
  > max → Invalid(ABOVE_PLAUSIBLE_MAX); null → Missing. Motor and kill switch: null → Missing.
- `RideStateMachine.reduce(current, status)` — pure, table-tested (rules below).
- `ObserveVehicleFeed`:
  - drop readings whose timestamp is not newer than the last one (out-of-order / duplicates);
  - `scan` through the state machine;
  - `transformLatest { emit(Live); delay(staleAfter); emit(SignalLost) }` for stale data;
  - `retryWhen` with a short delay on source errors, emitting `SignalLost` meanwhile; never crash;
  - `distinctUntilChanged` so identical frames don't recompose.
- Tests: state machine table, validator boundaries, stale after 3 s of silence and recovery,
  out-of-order drop, error → SignalLost → recovers.

### Phase 3 — UI
- `DashboardUiState.Content`: speed text, motor (ON/OFF), kill switch (Active/Inactive/Unknown),
  ride state label, signal warning. `SignalLost` state.
- Mapper does all formatting (rounding, "--" for Missing/Invalid).
- Screen per the reference: big centre speed + km/h, ride-state label, motor pill bottom-right
  (green ON / grey OFF), kill-switch indicator with distinct colour **and** icon/text (not colour alone).
- Optional: animate speed changes (`animateIntAsState`), immersive mode.
- Tests: ViewModel maps every `VehicleFeed` case; mapper edge cases; optional Compose UI test.

### Phase 4 — Docs and submission
README (build/run, architecture, assumptions/limitations, how to run tests), screenshots, GitHub repo.

## State machine (proposal — confirm before Phase 2)

States: `PARKED, READY, RIDING, ACCELERATING, MOTOR_DISABLED, UNKNOWN`.

| Priority | Condition | Next state |
|---|---|---|
| 1 | kill switch Active | MOTOR_DISABLED (speed still shown as reported) |
| 2 | motor or speed not Valid | UNKNOWN |
| 3 | motor OFF, speed 0 | PARKED |
| 4 | motor ON, speed 0 | READY |
| 5 | motor ON, speed > 0, speed rose ≥ threshold since last reading | ACCELERATING |
| 6 | motor ON, speed > 0, otherwise | RIDING |
| 7 | motor OFF, speed > 0, kill switch Inactive | same as 5–6 (decision 2) |

The previous state matters because "accelerating" needs the last speed, so the reducer takes the
previous speed too (kept in the `scan` accumulator).

## Edge cases covered

| Case | Handling |
|---|---|
| Negative / NaN / ∞ / implausibly high speed | `Invalid`, shown as "--", never crashes |
| One signal missing in a frame | That signal `Missing`, others still shown |
| No frames for 3 s (stale) | `SignalLost`: values hidden, warning shown |
| Source throws | Caught, `SignalLost`, retried |
| Out-of-order / duplicate frames | Dropped by monotonic timestamp |
| Motor OFF while moving | Allowed (doc: not a safety rule), see decision 2 |
| Kill switch Active while moving | MOTOR_DISABLED, real speed still displayed |
| First frame not yet received | `Connecting` |

## Decisions

1. **Accelerating vs Riding — decided:** the speed-change rule. The script holds 25 km/h for 3
   readings (RIDING shows on 2 of them) and ramps 30 → 50 for Accelerating.
2. **Motor OFF but still moving — decided:** no new state. The ride state describes how the vehicle
   moves, and the motor pill already shows OFF, so this falls under the existing rules: RIDING, or
   ACCELERATING if speed is rising (for example downhill). It is never PARKED, because PARKED
   needs speed 0.
3. **Stale data — decided:** show "--" and a signal-lost warning.
4. **Speed rounding — decided:** `roundToInt()`. The README will note UNECE R39.
5. **Negative noise — open:** treat every negative as Invalid, or clamp small noise
   (≥ −1 km/h) to 0 and treat anything lower as Invalid. Recommend clamping. Needed before Phase 2.
