# Fantasy Typing Game — Project Draft
**Course:** CST 210 Object-Oriented Programming Java  
**Deadline:** 23:55, 2nd July 2026  
**IDE:** Eclipse  
**Framework:** JavaFX  

---

## 1. Game Concept

A fantasy-themed typing game where waves of enemies march toward the player's castle. Each enemy has a word floating above its head. The player defeats enemies by correctly typing their words and pressing Enter. If an enemy reaches the castle, it deals damage. The game ends when the castle's health reaches zero (lose) or all waves are cleared (win).

**Why this concept works for the rubric:**
- Naturally maps to OOP — enemies, waves, a player, a castle, a game manager
- Easily demonstrates inheritance, polymorphism, and encapsulation
- Visually buildable in JavaFX using only shapes and text (no drawing required)
- Engaging and creative enough to score well on the Concept component (10%)

---

## 2. Class Structure

### 2.1 Class Hierarchy Overview

```
Enemy  (abstract parent class)
├── SmallMob
├── FastMob
└── Boss
```

### 2.2 Class Descriptions

#### `Enemy` (Abstract Class)
The parent of all enemy types. Holds all data and behaviour that every enemy shares.

**Fields (all private — encapsulation):**
| Field | Type | Description |
|---|---|---|
| `word` | `String` | The word the player must type |
| `health` | `int` | Current health points |
| `damage` | `int` | Damage dealt to castle on arrival |
| `speed` | `double` | Movement speed across the screen |
| `x`, `y` | `double` | Current screen position |

**Methods:**
| Method | Description |
|---|---|
| `getWord()`, `getHealth()`, etc. | Getters for all private fields |
| `takeDamage(int amount)` | Reduces health; marks enemy as dead if health ≤ 0 |
| `isDefeated()` | Returns true if health ≤ 0 |
| `move()` | Moves enemy forward by its speed value |
| `onDeath()` | **Abstract** — each subclass defines its own death behaviour |
| `render(GraphicsContext gc)` | **Abstract** — each subclass draws itself differently |

> `onDeath()` and `render()` being abstract is where **polymorphism** appears in this project. The game logic calls `enemy.onDeath()` without caring which subclass it is — each type handles it differently.

---

#### `SmallMob` (extends Enemy)
Basic, weak enemies that appear in large numbers early in the game.

**Additional fields:** none  
**Overridden methods:**
- `onDeath()` — awards a small score bonus (e.g. +10 points)
- `render()` — draws a small green goblin-like shape using JavaFX rectangles/circles

---

#### `FastMob` (extends Enemy)
Enemies with a shorter word but higher movement speed. Adds variety and urgency.

**Additional fields:** none  
**Overridden methods:**
- `onDeath()` — awards a medium score bonus (e.g. +20 points)
- `render()` — draws a slim, fast-looking shape in a different colour

> `FastMob` justifies itself in the class hierarchy by having a *meaningfully different constructor* (higher speed, shorter word) and a visually distinct appearance — not just different stat numbers.

---

#### `Boss` (extends Enemy)
A powerful enemy with a long word, high health, and high damage. Appears at the end of a wave.

**Additional fields:**
| Field | Type | Description |
|---|---|---|
| `phase` | `int` | Tracks which phase the boss is in (1 or 2) |

**Overridden methods:**
- `onDeath()` — awards a large score bonus (e.g. +100 points), possibly triggers a special effect
- `render()` — draws a large, visually distinct boss shape

> The `phase` field is something only `Boss` has — it does not belong in the parent class. This is a clean example of a subclass extending the parent with its own unique state.

---

#### `Castle` (standalone class)
Represents the player's base that enemies are trying to destroy.

**Fields:** `health`, `maxHealth`  
**Methods:** `takeDamage(int amount)`, `isDestroyed()`, `getHealthPercent()`

---

#### `Player` (standalone class)
Tracks the player's current input and score.

**Fields:** `score`, `currentInput` (the string the player is currently typing)  
**Methods:** `addScore(int points)`, `getScore()`, `getCurrentInput()`, `updateInput(String input)`

---

#### `WaveManager` (standalone class)
Manages the sequence of enemy waves. Responsible for spawning enemies on a timer.

**Fields:** `currentWave`, `enemies` (`ArrayList<Enemy>`), `waveConfigs`  
**Methods:** `spawnNextWave()`, `getActiveEnemies()`, `isWaveCleared()`, `isFinalWaveCleared()`

> This is the class where `ArrayList<Enemy>` is used. Because `SmallMob`, `FastMob`, and `Boss` all extend `Enemy`, they can all live in the same list — another benefit of inheritance.

---

#### `GameController` (main game loop class)
The central class that coordinates everything — handles user input, updates game state, checks win/lose conditions, and triggers rendering.

**Responsibilities:**
- Listens for keyboard input and matches typed words against active enemies
- Calls `enemy.takeDamage()` on a match
- Calls `enemy.onDeath()` when an enemy is defeated
- Calls `castle.takeDamage()` when an enemy reaches the end
- Checks `castle.isDestroyed()` and `waveManager.isFinalWaveCleared()` each frame

---

#### `GameApp` (JavaFX entry point)
The class that extends `javafx.application.Application`. Sets up the Stage, Scene, and Canvas, then launches the game.

---

### 2.3 Full Class Diagram (Text Format)

```
GameApp
  └── launches GameController
        ├── uses WaveManager  ──> ArrayList<Enemy>
        │                              ├── SmallMob
        │                              ├── FastMob
        │                              └── Boss
        ├── uses Castle
        └── uses Player
```

---

## 3. OOP Concepts Mapping

| OOP Concept | Where It Appears |
|---|---|
| **Encapsulation** | All `Enemy` fields are `private`, accessed via getters/setters |
| **Inheritance** | `SmallMob`, `FastMob`, `Boss` all extend `Enemy` |
| **Polymorphism** | `onDeath()` and `render()` behave differently per subclass; called uniformly via `ArrayList<Enemy>` |
| **Abstraction** | `Enemy` is abstract — you cannot instantiate it directly, only its concrete subclasses |

---

## 4. Game Flow

```
Launch App
    │
    ▼
Main Menu Screen  ──[Start]──►  Game Screen
                                    │
                                    ▼
                             Wave starts, enemies spawn
                                    │
                                    ▼
                        Player types words to defeat enemies
                                    │
                          ┌─────────┴──────────┐
                          ▼                    ▼
                   Enemy defeated         Enemy reaches castle
                   onDeath() called       Castle.takeDamage()
                          │                    │
                          └────────┬───────────┘
                                   ▼
                            Check win/lose condition
                          ┌────────┴────────┐
                          ▼                 ▼
                     All waves clear    Castle destroyed
                      WIN screen         LOSE screen
```

---

## 5. Suggested Work Division (Group of 4)

| Member | Suggested Responsibility |
|---|---|
| **Jin Wei** | `Enemy`, `SmallMob`, `FastMob`, `Boss` class hierarchy; `WaveManager`; `GameController` logic |
| Member 2 | `GameApp` setup, JavaFX Scene/Stage/Canvas scaffolding |
| Member 3 | UI screens (Main Menu, Win/Lose screens), score display, health bar rendering |
| Member 4 | Word list generation, comments & documentation, demo video |

---

## 6. Screens to Implement

1. **Main Menu** — Game title, Start button, maybe a simple How-To-Play section
2. **Game Screen** — Canvas with moving enemies, castle health bar, score display, typed input display
3. **Win Screen** — Final score, Play Again button
4. **Lose Screen** — Final score, Play Again button

---

## 7. Word List Strategy

Since file I/O is unfamiliar, words can be stored as hardcoded `String[]` arrays grouped by difficulty:

```java
String[] easyWords   = {"fire", "rock", "wind", "blade"};
String[] mediumWords = {"dragon", "castle", "knight", "potion"};
String[] bossWords   = {"necromancer", "thunderstrike", "enchantment"};
```

Words are assigned to enemies on spawn based on their type. This avoids file I/O entirely while keeping the design clean.

---

## 8. Open Questions (To Decide Later)

- [ ] What is the fantasy theme's visual colour palette? (e.g. dark forest, fire kingdom)
- [ ] How many waves per game? (Suggested: 3 waves + 1 boss wave)
- [ ] Does the Boss have a second phase mechanic, or is it just a stat difference?
- [ ] Does the player have a limited number of wrong attempts, or is only castle health the loss condition?
- [ ] What is the group number for submission naming?

---

*Draft written after Q&A session. To be revised as implementation progresses.*
