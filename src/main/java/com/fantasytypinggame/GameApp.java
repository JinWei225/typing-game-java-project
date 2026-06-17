package com.fantasytypinggame;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameApp extends Application {

    private Stage primaryStage;

    // Check input word match with enemy word or not
    private void checkPlayerInput(
        ArrayList<Enemy> enemyList,
        Player player,
        PowerUpManager powerUpManager,
        ArrayList<DamageText> damageTexts
    ) {
        Iterator<Enemy> iterator = enemyList.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            String currentWord = enemy.getCurrentWord();
            if (currentWord != null && currentWord.equals(player.getInput())) {
                enemy.removeCurrentWord();
                enemy.takeDamage(1);
                player.setInput("");
                damageTexts.add(
                    new DamageText(enemy.getX() + 20, enemy.getY() - 15, "-1")
                );
                if (enemy.isDefeated()) {
                    int point = enemy.onDeath();
                    if (powerUpManager.isDoublePointsActive()) point *= 2;
                    player.addScore(point);
                    player.increaseCombo();
                    powerUpManager.checkCombo(player.getCombo());
                    iterator.remove();
                } else {
                    // Enemy hit but not yet defeated — show damage text
                    damageTexts.add(
                        new DamageText(
                            enemy.getX() + 20,
                            enemy.getY() - 15,
                            "-1"
                        )
                    );
                }
            }
        }
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        Group menuRoot = new Group();
        Group instructionRoot = new Group();
        Group difficultyRoot = new Group();
        Group gameRoot = new Group();
        Group loseRoot = new Group();
        Group winRoot = new Group();
        Scene startScene = new Scene(menuRoot, 800, 600, Color.BEIGE);
        Scene instructionScene = new Scene(
            instructionRoot,
            800,
            600,
            Color.BEIGE
        );
        Scene difficultyScene = new Scene(
            difficultyRoot,
            800,
            600,
            Color.BEIGE
        );
        Scene gameScene = new Scene(gameRoot, 800, 600, Color.BEIGE);
        Scene loseScene = new Scene(loseRoot, 800, 600, Color.LIGHTGRAY);
        Scene winScene = new Scene(winRoot, 800, 600, Color.LIGHTGRAY);

        // Initialize game window and setup
        stage.setTitle("Fantasy Typing Game");
        stage.setScene(startScene);
        Canvas canvas = new Canvas(800, 600);
        gameRoot.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        stage.show();

        String[] gameState = { "MENU" };
        long[] countdownStart = { 0 };
        Castle castle = new Castle(100); // Castle total health is 100
        WaveManager waveManager = new WaveManager(30); // Number of waves per game is 20
        Player player = new Player(); // Initialize player object
        PowerUpManager powerUpManager = new PowerUpManager();
        ArrayList<DamageText> damageTexts = new ArrayList<>();

        // Player input handler
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                if (player.getInput().length() > 0) {
                    player.removeLastInput();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                if (gameState[0].equals("PLAYING")) {
                    gameState[0] = "PAUSED";
                } else if (gameState[0].equals("PAUSED")) {
                    gameState[0] = "COUNTDOWN";
                    countdownStart[0] = 0;
                }
            } else {
                String key = event.getText();
                if (key.equals("\\")) {
                    PowerUpManager.PowerUpType used =
                        powerUpManager.activateNext(
                            waveManager.getActiveEnemies(),
                            System.nanoTime()
                        );
                    if (used == PowerUpManager.PowerUpType.DAMAGE_ALL) {
                        Iterator<Enemy> iterator = waveManager
                            .getActiveEnemies()
                            .iterator();
                        while (iterator.hasNext()) {
                            Enemy enemy = iterator.next();
                            damageTexts.add(
                                new DamageText(
                                    enemy.getX() + 20,
                                    enemy.getY() - 15,
                                    "-1"
                                )
                            );
                            if (enemy.isDefeated()) {
                                int point = enemy.onDeath();
                                if (powerUpManager.isDoublePointsActive()) {
                                    point *= 2;
                                }
                                player.addScore(point);
                                player.increaseCombo();
                                powerUpManager.checkCombo(player.getCombo());
                                iterator.remove();
                            }
                        }
                    }
                }
                if (!key.isBlank() && Character.isLetter(key.charAt(0))) {
                    player.updateInput(key);
                    checkPlayerInput(
                        waveManager.getActiveEnemies(),
                        player,
                        powerUpManager,
                        damageTexts
                    );
                }
            }
        });

        // Game title on menu screen
        Text titleText = new Text("Typing Game");
        titleText.setFont(Font.font("Verdana", 40));
        titleText.setFill(Color.BROWN);
        titleText.setX(270);
        titleText.setY(200);
        menuRoot.getChildren().add(titleText);

        // Start game button
        Button startGameButton = new Button("Start Game");
        startGameButton.setLayoutX(357);
        startGameButton.setLayoutY(250);
        startGameButton.setOnAction(event -> {
            primaryStage.setScene(difficultyScene);
            gameState[0] = "DIFFICULTY SELECTION";
        });
        menuRoot.getChildren().add(startGameButton);

        Button menuInstructionsButton = new Button("How to Play");
        menuInstructionsButton.setLayoutX(356);
        menuInstructionsButton.setLayoutY(300); // Placed between Start and Exit
        menuInstructionsButton.setOnAction(event -> {
            gameState[0] = "INSTRUCTIONS";
            primaryStage.setScene(instructionScene);
        });
        menuRoot.getChildren().add(menuInstructionsButton);

        // Exit game button
        Button exitButton = new Button("Exit Game");
        exitButton.setLayoutX(360);
        exitButton.setLayoutY(350);
        exitButton.setOnAction(event -> {
            primaryStage.close();
        });
        menuRoot.getChildren().add(exitButton);

        // Build Instruction View Content
        Text instructTitleText = new Text("How to Play");
        instructTitleText.setFont(Font.font("Verdana", 36));
        instructTitleText.setFill(Color.BROWN);
        instructTitleText.setX(300);
        instructTitleText.setY(100);
        instructionRoot.getChildren().add(instructTitleText);

        Text instructBodyText = new Text(
            "OBJECTIVE:\n" +
                "   Defend your castle on the right. Survive all 30 waves to win!\n\n" +
                "CONTROLS:\n" +
                "   • Type words appearing above enemies to attack them.\n" +
                "   • Press 'BACKSPACE' to fix typing mistakes.\n" +
                "   • Press backslash '\\' to use earned Power-ups (Damage All, Slow, etc.).\n" +
                "   • Press 'ESC' or click the Pause button to freeze the game.\n\n" +
                "BONUS:\n" +
                "   Maintain a continuous 10 typing Combo to automatically gain Power-ups!"
        );
        instructBodyText.setFont(Font.font("Verdana", 15));
        instructBodyText.setX(100);
        instructBodyText.setY(180);
        instructBodyText.setLineSpacing(8);
        instructionRoot.getChildren().add(instructBodyText);

        // Backward Navigation Button Configuration**
        Button instructBackButton = new Button("Back to Main Menu");
        instructBackButton.setLayoutX(335);
        instructBackButton.setLayoutY(500);
        instructBackButton.setOnAction(event -> {
            gameState[0] = "MENU";
            primaryStage.setScene(startScene); // Swaps stage back to the default menu scene**
        });

        instructionRoot.getChildren().add(instructBackButton);

        // Difficulty screen title text
        Text difficultyText = new Text("Difficulty:");
        difficultyText.setFont(Font.font("Verdana", 40));
        difficultyText.setFill(Color.BROWN);
        difficultyText.setX(320);
        difficultyText.setY(100);
        difficultyRoot.getChildren().add(difficultyText);

        // Start easy game button
        Button startEasyGameButton = new Button("Easy");
        startEasyGameButton.setLayoutX(385);
        startEasyGameButton.setLayoutY(150);
        startEasyGameButton.setOnAction(event -> {
            primaryStage.setScene(gameScene);
            castle.reset();
            waveManager.reset();
            waveManager.setDifficulty("EASY");
            waveManager.spawnNextWave();
            player.reset();
            powerUpManager.reset();
            damageTexts.clear();
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        difficultyRoot.getChildren().add(startEasyGameButton);

        // Start medium game button
        Button startMediumGameButton = new Button("Medium");
        startMediumGameButton.setLayoutX(375);
        startMediumGameButton.setLayoutY(200);
        startMediumGameButton.setOnAction(event -> {
            primaryStage.setScene(gameScene);
            castle.reset();
            waveManager.reset();
            waveManager.setDifficulty("MEDIUM");
            waveManager.spawnNextWave();
            player.reset();
            powerUpManager.reset();
            damageTexts.clear();
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        difficultyRoot.getChildren().add(startMediumGameButton);

        // Start hard game button
        Button startHardGameButton = new Button("Hard");
        startHardGameButton.setLayoutX(385);
        startHardGameButton.setLayoutY(250);
        startHardGameButton.setOnAction(event -> {
            primaryStage.setScene(gameScene);
            castle.reset();
            waveManager.reset();
            waveManager.setDifficulty("HARD");
            waveManager.spawnNextWave();
            player.reset();
            powerUpManager.reset();
            damageTexts.clear();
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        difficultyRoot.getChildren().add(startHardGameButton);

        // Back to menu button in difficulty screen
        Button difficultyBackToMenuButton = new Button("Back to Main Menu");
        difficultyBackToMenuButton.setLayoutX(345);
        difficultyBackToMenuButton.setLayoutY(300);
        difficultyBackToMenuButton.setOnAction(event -> {
            gameState[0] = "MENU";
            primaryStage.setScene(startScene);
        });
        difficultyRoot.getChildren().add(difficultyBackToMenuButton);

        // Pause button in game screen
        Button pauseButton = new Button("Pause");
        pauseButton.setLayoutX(460);
        pauseButton.setLayoutY(5);
        pauseButton.setOnAction(event -> {
            if (gameState[0].equals("PLAYING")) {
                gameState[0] = "PAUSED";
            }
        });
        gameRoot.getChildren().add(pauseButton);

        // Resume button in pause screen
        Button resumeButton = new Button("Resume");
        resumeButton.setLayoutX(370);
        resumeButton.setLayoutY(330);
        resumeButton.setOnAction(event -> {
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        resumeButton.setVisible(false);
        gameRoot.getChildren().add(resumeButton);

        // Exit to menu button in pause screen
        Button pauseExitToMenuButton = new Button("Exit to Main Menu");
        pauseExitToMenuButton.setLayoutX(345);
        pauseExitToMenuButton.setLayoutY(380);
        pauseExitToMenuButton.setOnAction(event -> {
            player.setInput("");
            gameState[0] = "MENU";
            primaryStage.setScene(startScene);
        });
        gameRoot.getChildren().add(pauseExitToMenuButton);

        // Play again button in win screen
        Button winPlayAgainButton = new Button("Play Again");
        winPlayAgainButton.setLayoutX(360);
        winPlayAgainButton.setLayoutY(300);
        winPlayAgainButton.setOnAction(event -> {
            primaryStage.setScene(difficultyScene);
            gameState[0] = "DIFFICULTY SELECTION";
        });
        winRoot.getChildren().add(winPlayAgainButton);

        // Play again button in lose screen
        Button losePlayAgainButton = new Button("Play Again");
        losePlayAgainButton.setLayoutX(360);
        losePlayAgainButton.setLayoutY(300);
        losePlayAgainButton.setOnAction(event -> {
            primaryStage.setScene(difficultyScene);
            gameState[0] = "DIFFICULTY SELECTION";
        });
        loseRoot.getChildren().add(losePlayAgainButton);

        // Win screen display
        Text winText = new Text("You Win!");
        winText.setFont(Font.font("Verdana", 40));
        winText.setFill(Color.GREEN);
        winText.setX(315);
        winText.setY(200);
        winRoot.getChildren().add(winText);

        // Lose screen display
        Text loseText = new Text("You Lose!");
        loseText.setFont(Font.font("Verdana", 40));
        loseText.setFill(Color.RED);
        loseText.setX(315);
        loseText.setY(200);
        loseRoot.getChildren().add(loseText);

        // Total score text in win screen
        Text winScoreText = new Text("");
        winScoreText.setFont(Font.font("Verdana", 30));
        winScoreText.setFill(Color.BLACK);
        winScoreText.setX(315);
        winScoreText.setY(275);
        winRoot.getChildren().add(winScoreText);

        // Total score text in lose screen
        Text loseScoreText = new Text("");
        loseScoreText.setFont(Font.font("Verdana", 30));
        loseScoreText.setFill(Color.BLACK);
        loseScoreText.setX(315);
        loseScoreText.setY(275);
        loseRoot.getChildren().add(loseScoreText);

        // Back to menu button in win screen
        Button winBackToMenuButton = new Button("Back to Main Menu");
        winBackToMenuButton.setLayoutX(336);
        winBackToMenuButton.setLayoutY(340);
        winBackToMenuButton.setOnAction(event -> {
            gameState[0] = "MENU";
            player.setInput("");
            primaryStage.setScene(startScene);
        });
        winRoot.getChildren().add(winBackToMenuButton);

        // Back to menu button in lose screen
        Button loseBackToMenuButton = new Button("Back to Main Menu");
        loseBackToMenuButton.setLayoutX(336);
        loseBackToMenuButton.setLayoutY(340);
        loseBackToMenuButton.setOnAction(event -> {
            gameState[0] = "MENU";
            player.setInput("");
            primaryStage.setScene(startScene);
        });
        loseRoot.getChildren().add(loseBackToMenuButton);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Clear screen
                gc.clearRect(0, 0, 800, 600);

                // Show countdown screen
                if (gameState[0].equals("COUNTDOWN")) {
                    pauseButton.setVisible(false);
                    resumeButton.setVisible(false);
                    pauseExitToMenuButton.setVisible(false);
                    if (countdownStart[0] == 0) countdownStart[0] = now;
                    long elapsed = now - countdownStart[0];
                    int secondsLeft = 3 - (int) (elapsed / 1_000_000_000);

                    if (secondsLeft > 0) {
                        gc.setFont(Font.font("Verdana", 80));
                        gc.setFill(Color.BLACK);
                        gc.fillText(String.valueOf(secondsLeft), 380, 320);
                        return;
                    } else {
                        gameState[0] = "PLAYING";
                    }
                }

                // Show pause screen
                if (gameState[0].equals("PAUSED")) {
                    gc.setFont(Font.font("Verdana", 60));
                    gc.setFill(Color.BLACK);
                    gc.fillText("PAUSED", 290, 280);
                    pauseButton.setVisible(false);
                    resumeButton.setVisible(true);
                    pauseExitToMenuButton.setVisible(true);
                }
                // Show game screen
                if (gameState[0].equals("PLAYING")) {
                    // Hide unnecessary button
                    pauseButton.setVisible(true);
                    resumeButton.setVisible(false);
                    pauseExitToMenuButton.setVisible(false);

                    // Update screen content
                    Iterator<Enemy> iterator = waveManager
                        .getActiveEnemies()
                        .iterator();
                    powerUpManager.update(waveManager.getActiveEnemies(), now);
                    while (iterator.hasNext()) {
                        Enemy enemy = iterator.next();
                        if (enemy.getX() >= 600) {
                            castle.takeDamage(enemy.getDamage());
                            player.resetCombo(0);
                            powerUpManager.resetLastComboMilestone();
                            iterator.remove();
                        } else {
                            enemy.move();
                            enemy.render(gc);
                        }
                    }

                    // Update and render floating damage texts
                    Iterator<DamageText> dtIterator = damageTexts.iterator();
                    while (dtIterator.hasNext()) {
                        DamageText dt = dtIterator.next();
                        dt.update();
                        dt.render(gc);
                        if (dt.isExpired()) dtIterator.remove();
                    }

                    // Score text update
                    gc.setFont(Font.font("Verdana", 20));
                    gc.setFill(Color.GREEN);
                    gc.fillText("Score: " + player.getScore() + "", 10, 20);

                    // Combo text update
                    gc.setFont(Font.font("Verdana", 20));
                    gc.setFill(Color.GREEN);
                    gc.fillText("Combo: " + player.getCombo() + "", 150, 20);

                    // Power-up inventory display
                    gc.setFont(Font.font("Verdana", 16));
                    gc.setFill(Color.PURPLE);
                    String puDisplay =
                        "Power-ups: " +
                        powerUpManager.getStoredPowerUps().size();
                    gc.fillText(puDisplay, 300, 20);

                    // Active effect timers
                    if (powerUpManager.isSlowActive()) {
                        gc.setFill(Color.CYAN);
                        gc.fillText(
                            String.format(
                                "SLOW: %.1fs",
                                powerUpManager.getSlowRemaining(now)
                            ),
                            300,
                            45
                        );
                    }
                    if (powerUpManager.isDoublePointsActive()) {
                        gc.setFill(Color.GOLD);
                        gc.fillText(
                            String.format(
                                "2x PTS: %.1fs",
                                powerUpManager.getDoublePointsRemaining(now)
                            ),
                            300,
                            65
                        );
                    }

                    // Wave number update
                    gc.setFont(Font.font("Verdana", 20));
                    gc.setFill(Color.BLACK);
                    gc.fillText(
                        "Wave Number: " +
                            (waveManager.getWaveNumber() - 1) +
                            "",
                        10,
                        50
                    );

                    // Health bar update
                    gc.setFill(Color.GRAY);
                    gc.fillRect(600, 20, 150, 10);
                    gc.setFill(Color.RED);
                    gc.fillRect(600, 20, 150 * castle.getHealthPercent(), 10);
                    gc.setFill(Color.BLUE);
                    gc.fillText(player.getInput(), 310, 570);

                    // Castle block in game screen
                    gc.setFill(Color.BROWN);
                    gc.fillRect(600, 50, 150, 500);

                    // Lose and win situation
                    if (castle.isDestroyed()) {
                        gameState[0] = "ENDED";
                        primaryStage.setScene(loseScene);
                        loseScoreText.setText("Score: " + player.getScore());
                    } else if (waveManager.isWaveCleared()) {
                        if (waveManager.isFinalWaveCleared()) {
                            gameState[0] = "ENDED";
                            primaryStage.setScene(winScene);
                            winScoreText.setText("Score: " + player.getScore());
                        } else {
                            waveManager.spawnNextWave();
                        }
                    }
                }
            }
        };
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
