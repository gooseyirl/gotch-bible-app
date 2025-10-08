# Gotch Bible

An Android workout app based on the legendary deck-of-cards conditioning method popularized by Karl Gotch, "The God of Wrestling."

## About

Gotch Bible is an intense, no-equipment workout app where you complete exercises based on a shuffled deck of cards:

- **Each suit** represents a different exercise (Push-ups, Squats, Sit-ups, Burpees)
- **Each card value** determines the number of reps (Ace = 1, 2-10 = face value, Jack = 11, Queen = 12, King = 13)
- **Complete all 52 cards** for a total of 364 reps per workout

## Features

- ✅ **Customizable Exercises** - Assign any exercise to each suit
- ✅ **Workout Tracking** - Timer and rep counter for every workout
- ✅ **History & Leaderboard** - View past workouts with top 10 fastest times
- ✅ **Dark Theme** - Modern, clean interface with orange accent
- ✅ **Sound Effects** - Audio feedback for card changes and celebrations
- ✅ **Screen Wake Lock** - Screen stays on during active workouts
- ✅ **Portrait Lock** - Optimized for portrait orientation
- ✅ **No Ads, No Tracking** - Completely free with no data collection

## Screenshots

[Add screenshots here]

## Installation

### From Google Play Store
[Link to be added]

### Building from Source

1. Clone the repository:
```bash
git clone https://github.com/[your-username]/gotch-bible.git
cd gotch-bible
```

2. Open the project in Android Studio

3. Build and run on your device or emulator

### Building Release APK

```bash
./gradlew assembleRelease
```

The APK will be generated at: `app/build/outputs/apk/release/`

## Requirements

- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 15 (API 35)
- **Language:** Kotlin
- **Build System:** Gradle with Kotlin DSL

## Technology Stack

- **Android SDK** - Native Android development
- **Kotlin** - Primary programming language
- **Material Design Components** - UI components
- **Konfetti** - Celebration animations
- **SharedPreferences** - Local data storage
- **Reckon** - Git-based semantic versioning

## About Karl Gotch

This app is inspired by [Karl Gotch](https://en.wikipedia.org/wiki/Karl_Gotch) (1924-2007), a legendary professional wrestler known as "The God of Wrestling." His scientific approach to conditioning and grappling influenced generations of wrestlers and martial artists worldwide. The deck-of-cards workout became one of his signature conditioning methods, famous for building functional strength and endurance.

## Privacy

Gotch Bible does not collect, store, or transmit any personal data. All workout history is stored locally on your device only. See [PRIVACY_POLICY.md](PRIVACY_POLICY.md) for details.

## Versioning

This project uses [Reckon](https://github.com/ajoberstar/reckon) for automatic semantic versioning based on Git tags.

To create a new version:
```bash
git tag -a v0.3.0 -m "Release version 0.3.0"
git push origin v0.3.0
```

## License

[Add your license here - e.g., MIT, Apache 2.0, GPL, etc.]

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have suggestions, please open an issue on GitHub.

## Acknowledgments

- Karl Gotch for the original workout methodology
- The wrestling and martial arts community for keeping these conditioning methods alive

---

**Note:** This is a fitness app. Always consult with a healthcare professional before starting any new exercise program.
