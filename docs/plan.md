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
| `:core:model` (JVM) | `VehicleReading` (raw), `Signal<T>`, `VehicleStatus`, `RideState`, `VehicleDashboard`, `MonotonicClock` |
| `:core:domain` (JVM) | `VehicleRepository` port, `VehicleSignalValidator`, `RideStateMachine`, `ObserveVehicleDashboard`, `@Dispatcher` |
| `:core:data` | `VehicleDataSource` + `SimulatedVehicleDataSource`, `VehicleRepositoryImpl`, Hilt modules (dispatcher, clock) |
| `:core:designsystem` | Always-dark HMI theme, status colours, text styles, strings |
| `:feature:dashboard:ui` | `DashboardViewModel`, `DashboardUiState`, mapper, `DashboardScreen` |
| `:app` | `HmiApplication` (Hilt root), `MainActivity` (landscape, keep screen on) |

Data flow: `SimulatedVehicleDataSource` → `VehicleRepository` (flowOn Default) → `ObserveVehicleDashboard`
(order check → validate → state machine → staleness → error recovery) → `DashboardViewModel`
(`stateIn`, `WhileSubscribed(5s)`) → `collectAsStateWithLifecycle` → stateless `DashboardScreen`.

## Phases

Each phase: build → tests green → you review → next.

### Phase 0 — Boilerplate (done)
Modules, convention plugins, version catalog, Hilt wiring, contracts and stubs. App launches in
landscape showing "Connecting to vehicle…". One smoke test.

### Phase 1 — Data layer (done, in review)

Goal: a fake vehicle that sends one reading a second, on a background thread. The data layer
passes values through **unchanged**, including bad ones; judging them is the domain's job (Phase 2).

#### How a reading travels
```
SimulatedVehicleDataSource      VehicleRepositoryImpl     ObserveVehicleDashboard (Phase 2)    DashboardViewModel     DashboardScreen
  plays a list of samples   ──►   moves it off the    ──►   checks values, works out  ──►   turns it into     ──►  draws it
  one per second, forever         main thread               the ride state, spots          UI state (StateFlow)
                                  (flowOn Default)          missing / stale data
        :core:data                    :core:data                 :core:domain               :feature:dashboard:ui
```

#### The ride, second by second (17 s, then it starts again)
```
second   0  1  2 | 3  4  5 | 6  7  8 | 9  10 11 12 13 | 14 15 16 | 17 ...
speed    0  0  0 | 0  0  0 | 25 25 25| 30 35 40 45 50 | 0  0  0  | 0 (again)
motor    OFF     | ON      | ON      | ON             | OFF       |
kill sw  off     | off     | off     | off            | ON        |
         Parked    Ready     Riding    Accelerating     Motor disabled
```
Accelerating is a rising ramp: holding 50 would read as Riding from the second sample (decision 1).

#### Code — `core/data/.../source/SimulatedVehicleDataSource.kt`
```kotlin
internal class SimulatedVehicleDataSource @Inject constructor(
    private val clock: MonotonicClock,
) : VehicleDataSource {

    override fun readings(): Flow<VehicleReading> = flow {
        while (true) {
            for (sample in RideSamples.normal) {
                emit(sample.toReading(clock.nowMillis()))
                delay(1.seconds)
            }
        }
    }
}
```
- Sends first, then waits, so the screen leaves "Connecting" straight away.
- `flow {}` is cold and `delay` is cancellable: it stops when the screen stops collecting.
- Timestamps come from `MonotonicClock`, not wall time.

#### Code — `core/data/.../source/RideSamples.kt`
```kotlin
internal data class Sample(val speedKmh: Float, val motor: MotorState, val killSwitch: KillSwitchState)

internal object RideSamples {
    val normal: List<Sample> =
        List(3) { Sample(0f, MotorState.OFF, KillSwitchState.INACTIVE) } +    // Parked
        List(3) { Sample(0f, MotorState.ON, KillSwitchState.INACTIVE) } +     // Ready
        List(3) { Sample(25f, MotorState.ON, KillSwitchState.INACTIVE) } +    // Riding
        listOf(30f, 35f, 40f, 45f, 50f).map { Sample(it, MotorState.ON, KillSwitchState.INACTIVE) } + // Accelerating
        List(3) { Sample(0f, MotorState.OFF, KillSwitchState.ACTIVE) }       // Motor disabled
}
```
`VehicleReading` carries `MotorState?` (`ON`/`OFF`) and `KillSwitchState?` (`ACTIVE`/`INACTIVE`),
the same words the UI shows. null = missing.

#### Where the edge cases are tested
The simulator only plays the clean ride. Bad data is fed in by tests, using the
`FakeVehicleRepository` already in `:core:testing`, so each case is one small test in Phase 2:

| Edge case | How the test feeds it |
|---|---|
| Negative / NaN / 400 km/h speed | `repo.emit(reading(speed = -5f))` |
| Missing speed or motor | `repo.emit(reading(speed = null))` |
| Stale data | emit once, then `advanceTimeBy(3.seconds)` with nothing sent |
| Old reading arrives late | emit t=2000, then t=1000 |
| Source fails | `repo.fail(IOException())` |
| Motor OFF while moving | `repo.emit(reading(speed = 20f, motor = MotorState.OFF))` |

Optional later: a second sample list with bad values, switched on by a debug flag, to show them on
the emulator. Not needed for the MVP.

#### Tests — `core/data/src/test/` (`runTest` virtual time)
`SimulatedVehicleDataSourceTest`
- first reading comes at t = 0, then one every 1000 ms
- the first 17 readings are the ride above, in order
- reading 18 is Parked again (it loops)
- timestamps come from the clock

`VehicleRepositoryImplTest`
- passes readings through unchanged

#### Verify
- `./gradlew :core:data:testDebugUnitTest`, then the full command in AGENTS.md.
- The screen stays on "Connecting" until Phase 2 wires the use case.
- Branch `feat/data-simulator`, commit `feat: simulate a vehicle ride`.

### Phase 2 — Domain
- `VehicleSignalValidator`: speed NaN/∞ → Invalid(NOT_A_NUMBER); < 0 → Invalid(NEGATIVE);
  > max → Invalid(ABOVE_PLAUSIBLE_MAX); null → Missing. Motor and kill switch: null → Missing.
- `RideStateMachine.reduce(current, status)` — pure, table-tested (rules below).
- `ObserveVehicleDashboard`:
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
- Tests: ViewModel maps every `VehicleDashboard` case; mapper edge cases; optional Compose UI test.

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
5. **Negative noise — decided:** clamp small noise (−1 km/h up to 0) to 0; anything below −1 is
   Invalid and shows "--".
