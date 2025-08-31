# Tic Tac Toe Android Game

A modern, feature-rich Tic Tac Toe game for Android with advanced animations, sound effects, and gaming-style UI.

## Features

### 🎮 Gaming Experience
- **Advanced UI Design**: Modern dark theme with gradient backgrounds and neon colors
- **Smooth Animations**: Custom animations for piece placement, win celebrations, and UI transitions
- **Particle Effects**: Explosion and celebration particle systems for wins
- **Sound Effects**: Gaming sounds for moves, wins, draws, and button interactions
- **Haptic Feedback**: Vibration feedback for enhanced user experience
- **Custom Game Cells**: Hand-drawn X and O symbols with animated placement

### 🏆 Game Features
- **Score Tracking**: Persistent score tracking for X and O players
- **Win Detection**: Automatic detection of horizontal, vertical, and diagonal wins
- **Win Highlighting**: Visual highlighting of winning cells with glow effects
- **Draw Detection**: Automatic draw detection when board is full
- **Game Reset**: New game and score reset functionality

### 📱 Technical Features
- **Android 8+ Support**: Compatible with Android API level 26 and above
- **Portrait Orientation**: Optimized for portrait mode gaming
- **No Login Required**: Instant play without any registration
- **Offline Play**: No internet connection required
- **Material Design**: Modern Material Design 3 components

## Screenshots

The app features:
- Dark gradient background with neon accent colors
- Custom animated game board with rounded corners
- Smooth piece placement animations
- Particle explosion effects on wins
- Modern button styling with ripple effects
- Score display with player-specific colors

## Technical Architecture

### Core Components

1. **GameLogic.kt**: Core game logic handling board state, moves, and win detection
2. **MainActivity.kt**: Main activity with UI management and animations
3. **CustomGameCell.kt**: Custom view for animated game cells
4. **ParticleSystem.kt**: Particle effects system for celebrations
5. **SoundManager.kt**: Sound effects and audio management

### Key Technologies
- **Kotlin**: Primary programming language
- **Android SDK**: Native Android development
- **Material Design 3**: Modern UI components
- **Custom Animations**: ObjectAnimator and ValueAnimator
- **Sound Pool**: Audio management for game sounds
- **Vibration API**: Haptic feedback

## Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK with API level 26+
- Kotlin support

### Build Instructions

1. Clone the repository:
```bash
git clone <repository-url>
cd tic-tac-toe
```

2. Open in Android Studio:
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory

3. Build and run:
   - Connect an Android device or start an emulator
   - Click "Run" or press Ctrl+R (Cmd+R on Mac)

### APK Generation
```bash
./gradlew assembleRelease
```

## Game Rules

1. **Objective**: Get three of your symbols (X or O) in a row
2. **Players**: Two players alternate turns (X goes first)
3. **Winning**: First to get 3 in a row (horizontal, vertical, or diagonal) wins
4. **Draw**: If all 9 cells are filled without a winner, it's a draw
5. **Scoring**: Wins are tracked and displayed at the top

## Controls

- **Tap Cell**: Place your symbol (X or O)
- **New Game**: Start a fresh game (keeps score)
- **Reset Score**: Reset both scores to 0 and start new game

## Customization

### Colors
Edit `app/src/main/res/values/colors.xml` to customize:
- Player colors (X and O)
- Background gradients
- Accent colors
- Win celebration colors

### Animations
Modify animation durations and effects in:
- `MainActivity.kt` for UI animations
- `CustomGameCell.kt` for cell animations
- `ParticleSystem.kt` for particle effects

### Sounds
Update `SoundManager.kt` to:
- Add custom sound files
- Modify tone frequencies
- Adjust volume levels

## Performance Optimizations

- **Efficient Animations**: Uses hardware-accelerated animations
- **Memory Management**: Proper cleanup of resources in onDestroy()
- **Sound Optimization**: Reuses SoundPool instances
- **View Recycling**: Efficient cell management

## Compatibility

- **Minimum SDK**: Android 8.0 (API level 26)
- **Target SDK**: Android 14 (API level 34)
- **Architecture**: Supports all Android architectures
- **Screen Sizes**: Optimized for phones and small tablets

## Testing

Run unit tests:
```bash
./gradlew test
```

The project includes comprehensive tests for:
- Game logic validation
- Win condition detection
- Score tracking
- Board state management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Future Enhancements

Potential improvements:
- AI opponent with difficulty levels
- Online multiplayer support
- Tournament mode
- Custom themes and skins
- Statistics and achievements
- Landscape orientation support
