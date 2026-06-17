# Fantasy Typing Game

A fantasy-themed typing game built with JavaFX. Waves of enemies march toward your castle — type their words to defeat them before they reach you.

## Gameplay

- Enemies spawn on the left side of the screen and march toward the castle on the right
- Each enemy displays a word (or multiple words on higher difficulties) above its head
- Type the word to deal damage; clear all words to defeat the enemy
- If an enemy reaches the castle it deals damage — the game ends when castle health hits zero
- Survive all 30 waves to win

### Enemy Types

| Type | Description | Speed | Score (Easy/Medium/Hard) |
|---|---|---|---|
| **FastMob** | Short words, moves quickly | Fast | +5/10/20 pts |
| **SmallMob** | Medium words, moderate speed | Moderate | +5/10/20 pts |
| **Boss** | Long multi-word sequence, high HP | Slow | +20/30/40 pts |

Bosses appear every 5th wave; the mix of FastMobs and SmallMobs grows with each wave number.

### Difficulty

| Difficulty | Enemy HP | Damage to Castle | Words per Enemy |
|---|---|---|---|
| Easy | Low | Low | 1 words (FastMob/SmallMob), 2 words (Boss) |
| Medium | Medium | Medium | 1 word (FastMob), 2 words (SmallMob) 3 words (Boss) |
| Hard | High | High | 2 words (FastMob/SmallMob), 4 words (Boss) |

### Controls

| Key | Action |
|---|---|
| Letters | Build your typed word |
| Backspace | Delete last character |
| `\` (backslash) | Use the next stored power-up |
| Escape | Pause / Unpause |

### Power-ups

Earn a random power-up every **10 combo kills**. Use them with `\`.

| Power-up | Effect | Duration |
|---|---|---|
| Damage All | Deals 1 HP to every enemy on screen | Instant |
| Slow Enemies | Halves all enemy movement speed | 10 seconds |
| Double Points | All kills score 2× points | 10 seconds |

Active effect timers are shown in the HUD. Stored power-up count is displayed at the top of the screen.

## Requirements

- Java 11+
- Maven 3.x

## Running the Game

```bash
mvn clean javafx:run
```

## Project Structure

```
src/main/java/com/fantasytypinggame/
├── GameApp.java          # JavaFX entry point, game loop, all screens
├── Enemy.java            # Abstract base class for all enemy types
├── SmallMob.java         # Extends Enemy — moderate speed, medium words
├── FastMob.java          # Extends Enemy — fast speed, short words
├── Boss.java             # Extends Enemy — slow, multi-word, high HP
├── WaveManager.java      # Spawns and tracks enemy waves
├── Castle.java           # Player's base; tracks HP and destruction state
├── Player.java           # Tracks score, combo, and typed input
├── PowerUpManager.java   # Combo-triggered power-up inventory and effects
├── DamageText.java       # Floating "-1" text shown on enemy hits
├── WordBank.java         # Static word pools (easy / medium / boss)
└── SystemInfo.java       # Utility class
```

## Screens

1. **Main Menu** — Start Game, Exit
2. **Difficulty Selection** — Easy / Medium / Hard / Back
3. **Game** — Live HUD with score, combo, wave number, power-up count, castle health bar, and typed input preview
4. **Pause** — Resume or Exit to Main Menu
5. **Win / Lose** — Final score, Play Again, Back to Main Menu
