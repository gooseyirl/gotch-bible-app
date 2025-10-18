# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Gotch Bible is an Android workout app based on Karl Gotch's legendary deck-of-cards conditioning method. Each card suit maps to an exercise (Push-ups, Squats, Sit-ups, Burpees), and the card value determines the number of reps. A complete workout consists of all 52 cards (364 total reps).

**Package:** `com.gooseco.gotchbible`
**Min SDK:** API 24 (Android 7.0)
**Target SDK:** API 35 (Android 15)
**Language:** Kotlin
**Build System:** Gradle with Kotlin DSL

## Build Commands

### Development Builds
```bash
# Build debug APK
./gradlew assembleDebug

# Install debug build to connected device
./gradlew installDebug

# Build and install
./gradlew build
```

### Release Builds
```bash
# Build release APK (includes ProGuard minification and resource shrinking)
./gradlew assembleRelease

# Output location: app/build/outputs/apk/release/gotch-bible-{version}-release.apk
```

### Cleaning
```bash
# Clean build artifacts
./gradlew clean
```

## Versioning

This project uses [Reckon](https://github.com/ajoberstar/reckon) for Git-based semantic versioning. Version is automatically calculated from Git tags and configured in `settings.gradle.kts`.

**To create a new version:**
```bash
git tag -a v0.3.0 -m "Release version 0.3.0"
git push origin v0.3.0
```

Version name and code are automatically derived:
- `versionName` comes directly from the Git tag
- `versionCode` is calculated from version parts (e.g., 1.2.3 → 10203) in `app/build.gradle.kts:19-22`

## Architecture

### Core Components

**DeckManager** (`DeckManager.kt`)
- Manages the 52-card deck (4 suits × 13 ranks)
- Creates, shuffles, and resets the deck
- Provides immutable deck access via `getDeck()`

**Card** (`Card.kt`)
- Data class representing a playing card
- Enums: `Suit` (with symbol and color), `Rank` (with display string and rep value), `CardColor`
- Card values: Ace=1, 2-10=face value, Jack=11, Queen=12, King=13

**MainActivity** (`MainActivity.kt`)
- Main workout interface with two states: menu and active workout
- Manages workout timer (decisecond precision), card progression, and rep tracking
- Handles screen wake lock during workouts
- Triggers confetti celebration on workout completion via Konfetti library
- Uses `Handler` with `Runnable` for timer updates (100ms intervals)

**WorkoutStorage** (`WorkoutStorage.kt`)
- Persists workout history to SharedPreferences using JSON serialization
- Stores top 10 workouts sorted by fastest completion time
- Key: `"workouts"` in SharedPreferences named `"GotchBible"`

**WorkoutRecord** (`WorkoutRecord.kt`)
- Data class for completed workouts with timestamp, duration, rep counts, and exercise names per suit
- Manual JSON serialization via `toJson()` and `fromJson()` (no GSON/Moshi)

**SoundPlayer** (`SoundPlayer.kt`)
- Manages audio feedback for card changes and workout completion

### UI Components

**Activities:**
- `MainActivity` - Home screen and workout interface
- `SettingsActivity` - Exercise customization per suit
- `PastWorkoutsActivity` - Leaderboard showing top 10 workouts
- `WorkoutDetailActivity` - Detailed view of individual workout

**Adapters:**
- `CardAdapter` - RecyclerView adapter for displaying completed cards
- `WorkoutAdapter` - RecyclerView adapter for workout history list

**Key UI Features:**
- All activities locked to portrait orientation (defined in `AndroidManifest.xml`)
- ViewBinding enabled (`app/build.gradle.kts:53`)
- Material Design Components with custom theme (orange accent)

## Dependencies

- `androidx.core:core-ktx` - Kotlin extensions
- `com.google.android.material:material` - Material Design components
- `androidx.constraintlayout:constraintlayout` - Layout system
- `androidx.recyclerview:recyclerview` - List displays
- `nl.dionsegijn:konfetti-xml` - Celebration animations

No testing dependencies currently configured (no test files present).

## Data Persistence

All data stored locally via SharedPreferences:
- Workout history (top 10 by duration)
- Exercise customization (suit-to-exercise mapping)
- No remote storage or data transmission

## Workout Flow

1. User starts workout from `MainActivity`
2. Deck shuffled via `DeckManager.shuffle()`
3. Screen wake lock acquired
4. Timer starts on first card view
5. User advances through cards, rep counts accumulated per suit
6. On completion: timer stops, confetti displays, workout saved to storage
7. Screen wake lock released

## Important Notes

- APK output filename customized to include version and build type: `gotch-bible-{versionName}-{buildType}.apk` (configured in `app/build.gradle.kts:36-41`)
- ProGuard enabled for release builds with resource shrinking
- No network permissions or external API calls
- No analytics or tracking (privacy-focused)
