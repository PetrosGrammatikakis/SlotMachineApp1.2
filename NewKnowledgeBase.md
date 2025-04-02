# Overall Program Flow and Logic

This Android application is a slot machine game built using Kotlin and Jetpack Compose.

## Structure & Navigation
- **Entry Point:** `MainActivity` initializes the application, loads saved data, and sets up the Compose UI.
- **Navigation:** A `NavHost` manages navigation between three main screens:
    - `starter`: The initial screen with "Play" and "Shop" buttons.
    - `game`: The main slot machine screen (`SlotMachineApp` composable).
    - `Shop`: The screen for buying and managing backgrounds (`ShopScreen` composable).
- **UI:** Jetpack Compose is used for building the user interface declaratively.

## State Management
- **Core State:** Key application states like `coins`, `realCoins`, `equippedBackgroundId`, and `purchasedBackgrounds` are managed in `MainActivity` using `MutableState`.
- **Persistence:** `SharedPreferences` is used to save and load these core states (`onCreate`, `onStop`), ensuring data persists across app sessions.
- **Composable State:** Individual composables like `SlotMachine` manage their own transient UI state (e.g., `spinning`, `autoSpinning`, reel values).

## Game Logic (`SlotMachine` Composable)
- **Reels:** Represented by a list of mutable lists, updated randomly during spins.
- **Spinning:** Triggered manually or via auto-spin, managed by the `spinning` state. A `LaunchedEffect` keyed on `spinning` handles the spin animation (`spinReel`), coin deduction, win checking (`checkWinningCombination`), coin awarding, and result display (Snackbar).
- **Auto-Spin:** Managed by the `autoSpinning` state and a separate `LaunchedEffect`. It triggers spins automatically when enabled and the machine is idle (`spinning == false`), checking for sufficient coins.
- **Risk/Multiplier:** Players can select a risk amount (`selectedRisk`), which affects the cost per spin and the win multiplier (`multiplier`).
- **Win Conditions:** `checkWinningCombination` analyzes the final reel state to determine payouts based on patterns (pairs, triplets, 4-of-a-kind, 5-of-a-kind, jackpot).

## Shop Logic (`ShopScreen` Composable)
- **Display:** Shows available backgrounds (`BackgroundItemModel`) with their prices and purchase/equipped status.
- **Currency:** Displays current `coins` and `realCoins`. Allows conversion between them.
- **Purchasing:** Handles coin deduction and updates the `purchasedBackgrounds` set (saved via `SharedPreferences`).
- **Equipping:** Allows selecting a purchased background, updating `equippedBackgroundId` (saved via `SharedPreferences`).

## Daily Bonus
- `updateDailyCoins` in `MainActivity` checks if the day has changed since the last update. If so, and if coins are below 50, it tops them up to 50.

## Theme & Boilerplate
- `ui/theme/Color.kt`: Defines basic `Color` constants (Purple, Pink shades) used in the theme.
- `ui/theme/Theme.kt`: Defines the `SlotMachineAppTheme` composable, setting up Material 3 light/dark color schemes (using colors from `Color.kt`), handling dynamic color on Android 12+, and applying the theme (colors and typography) to the app content.
- `ui/theme/Type.kt`: Defines the `Typography` object containing Material 3 text styles (font family, weight, size) used by the theme.
- `androidTest/.../ExampleInstrumentedTest.kt`: Contains boilerplate Android instrumentation tests (runs on a device/emulator) verifying the app context.
- `test/.../ExampleUnitTest.kt`: Contains boilerplate local unit tests (runs on the host machine) with a simple assertion.

---

# New Knowledge Base

## 2025-04-02

*   The `spinning` state variable in `MainActivity.kt` is set to `false` only *after* the `snackbarHostState.showSnackbar` coroutine completes. This simplifies triggering subsequent actions (like auto-spin) that should wait for the spin result message to finish, as checking `spinning == false` implicitly confirms the Snackbar is done.
*   The application uses `LaunchedEffect` keyed on state changes (like `spinning` and `autoSpinning`) to manage asynchronous tasks and UI updates related to the slot machine's state.
