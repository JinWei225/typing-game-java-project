package com.fantasytypinggame;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameApp extends Application {

    private Stage primaryStage;

    private Image background = new Image(
        getClass().getResourceAsStream("/assets/grass_field.png")
    );

    // Check input word match with enemy word or not
    private void checkPlayerInput(
        ArrayList<Enemy> enemyList,
        Player player,
        PowerUpManager powerUpManager,
        ArrayList<DamageText> damageTexts,
        ArrayList<LightningEffect> lightningEffects
    ) {
        Iterator<Enemy> iterator = enemyList.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            String currentWord = enemy.getCurrentWord();
            if (currentWord != null && currentWord.equals(player.getInput())) {
                lightningEffects.add(
                    new LightningEffect(enemy.getX() + 10, enemy.getY() - 20)
                );
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
                    enemy.beginDeath();
                } else {
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
        boolean[] resumeCountdown = { false };
        Castle castle = new Castle(100); // Castle total health is 100
        WaveManager waveManager = new WaveManager(30); // Number of waves per game is 20
        Player player = new Player(); // Initialize player object
        PowerUpManager powerUpManager = new PowerUpManager();
        ArrayList<DamageText> damageTexts = new ArrayList<>();
        ArrayList<LightningEffect> lightningEffects = new ArrayList<>();

        // Create music manager object to control background music
        MusicManager musicManager = new MusicManager();

        // Start menu background music when the game first opens
        musicManager.playMenuMusic();

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
                            enemy.removeCurrentWord();
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
                                enemy.beginDeath();
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
                        damageTexts,
                        lightningEffects
                    );
                }
            }
        });

        // Main menu background image
        Image menuBgImage = new Image(
            getClass().getResource("/assets/Castle2.png").toExternalForm()
        );
        ImageView menuBg = new ImageView(menuBgImage);
        menuBg.setFitWidth(800);
        menuBg.setFitHeight(600);
        menuBg.setPreserveRatio(false);
        menuRoot.getChildren().add(menuBg);

        // Dark transparent layer so text is readable
        javafx.scene.shape.Rectangle darkOverlay =
            new javafx.scene.shape.Rectangle(800, 600);
        darkOverlay.setFill(Color.rgb(0, 0, 0, 0.35));
        menuRoot.getChildren().add(darkOverlay);

        // Game title on menu screen
        Text titleText = new Text("Fantasy Typing Game");
        titleText.setFont(Font.font("Georgia", 46));
        titleText.setFill(Color.GOLD);
        titleText.setX(185);
        titleText.setY(180);
        titleText.setEffect(new DropShadow(12, Color.BLACK));
        menuRoot.getChildren().add(titleText);

        // Subtitle
        Text subtitleText = new Text(
            "Defend the castle with your typing skills"
        );
        subtitleText.setFont(Font.font("Georgia", 18));
        subtitleText.setFill(Color.WHEAT);
        subtitleText.setX(260);
        subtitleText.setY(220);
        subtitleText.setEffect(new DropShadow(8, Color.BLACK));
        menuRoot.getChildren().add(subtitleText);

        // Start game button
        Button startGameButton = new Button("Start Game");
        startGameButton.setLayoutX(325);
        startGameButton.setLayoutY(280);
        startGameButton.setPrefSize(150, 42);
        startGameButton.setFont(Font.font("Georgia", 16));
        startGameButton.setStyle(
            "-fx-background-color: #6b3e1e;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        startGameButton.setOnAction(event -> {
            primaryStage.setScene(difficultyScene);
            gameState[0] = "DIFFICULTY SELECTION";
        });
        menuRoot.getChildren().add(startGameButton);

        // How to Play button
        Button menuInstructionsButton = new Button("How to Play");
        menuInstructionsButton.setLayoutX(325);
        menuInstructionsButton.setLayoutY(335);
        menuInstructionsButton.setPrefSize(150, 42);
        menuInstructionsButton.setFont(Font.font("Georgia", 16));
        menuInstructionsButton.setStyle(
            "-fx-background-color: #3b2a20;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        menuInstructionsButton.setOnAction(event -> {
            gameState[0] = "INSTRUCTIONS";
            primaryStage.setScene(instructionScene);
        });
        menuRoot.getChildren().add(menuInstructionsButton);

        // Exit game button
        Button exitButton = new Button("Exit Game");
        exitButton.setLayoutX(325);
        exitButton.setLayoutY(390);
        exitButton.setPrefSize(150, 42);
        exitButton.setFont(Font.font("Georgia", 16));
        exitButton.setStyle(
            "-fx-background-color: #2b1d16;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #b8860b;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        exitButton.setOnAction(event -> {
            primaryStage.close();
        });
        menuRoot.getChildren().add(exitButton);

        // How to Play background image
        Image instructionBgImage = new Image(
            getClass().getResource("/assets/Castle2.png").toExternalForm()
        );
        ImageView instructionBg = new ImageView(instructionBgImage);
        instructionBg.setFitWidth(800);
        instructionBg.setFitHeight(600);
        instructionBg.setPreserveRatio(false);
        instructionRoot.getChildren().add(instructionBg);

        // Dark transparent layer
        Rectangle instructionOverlay = new Rectangle(800, 600);
        instructionOverlay.setFill(Color.rgb(0, 0, 0, 0.45));
        instructionRoot.getChildren().add(instructionOverlay);

        // Instruction panel
        Rectangle instructionPanel = new Rectangle(110, 75, 580, 430);
        instructionPanel.setArcWidth(25);
        instructionPanel.setArcHeight(25);
        instructionPanel.setFill(Color.rgb(43, 29, 22, 0.82));
        instructionPanel.setStroke(Color.GOLD);
        instructionPanel.setStrokeWidth(3);
        instructionPanel.setEffect(new DropShadow(18, Color.BLACK));
        instructionRoot.getChildren().add(instructionPanel);

        // How to Play title
        Text instructTitleText = new Text("How to Play");
        instructTitleText.setFont(Font.font("Georgia", 42));
        instructTitleText.setFill(Color.GOLD);
        instructTitleText.setX(285);
        instructTitleText.setY(135);
        instructTitleText.setEffect(new DropShadow(10, Color.BLACK));
        instructionRoot.getChildren().add(instructTitleText);

        // Instruction body
        Text instructBodyText = new Text(
            "OBJECTIVE\n" +
                "Defend your castle and survive all 30 waves.\n\n" +
                "CONTROLS\n" +
                "• Type the word above each enemy to attack.\n" +
                "• Press BACKSPACE to fix typing mistakes.\n" +
                "• Press \\ to use your stored power-up.\n" +
                "• Press ESC or click Pause to freeze the game.\n\n" +
                "BONUS\n" +
                "Keep a 10 combo streak to earn power-ups."
        );
        instructBodyText.setFont(Font.font("Georgia", 18));
        instructBodyText.setFill(Color.WHEAT);
        instructBodyText.setX(175);
        instructBodyText.setY(180);
        instructBodyText.setLineSpacing(8);
        instructBodyText.setWrappingWidth(470);
        instructBodyText.setEffect(new DropShadow(6, Color.BLACK));
        instructionRoot.getChildren().add(instructBodyText);

        // Back button
        Button instructBackButton = new Button("Back to Main Menu");
        instructBackButton.setLayoutX(315);
        instructBackButton.setLayoutY(520);
        instructBackButton.setPrefSize(170, 42);
        instructBackButton.setFont(Font.font("Georgia", 15));
        instructBackButton.setStyle(
            "-fx-background-color: #3b2a20;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        instructBackButton.setOnAction(event -> {
            // Restart menu music when returning from How to Play to the main menu
            musicManager.playMenuMusic();

            gameState[0] = "MENU";
            primaryStage.setScene(startScene);
        });
        instructionRoot.getChildren().add(instructBackButton);

        // Difficulty background image
        Image difficultyBgImage = new Image(
            getClass().getResource("/assets/Castle2.png").toExternalForm()
        );
        ImageView difficultyBg = new ImageView(difficultyBgImage);
        difficultyBg.setFitWidth(800);
        difficultyBg.setFitHeight(600);
        difficultyBg.setPreserveRatio(false);
        difficultyRoot.getChildren().add(difficultyBg);

        // Dark transparent layer
        Rectangle difficultyOverlay = new Rectangle(800, 600);
        difficultyOverlay.setFill(Color.rgb(0, 0, 0, 0.45));
        difficultyRoot.getChildren().add(difficultyOverlay);

        // Difficulty panel
        Rectangle difficultyPanel = new Rectangle(230, 90, 340, 390);
        difficultyPanel.setArcWidth(25);
        difficultyPanel.setArcHeight(25);
        difficultyPanel.setFill(Color.rgb(43, 29, 22, 0.82));
        difficultyPanel.setStroke(Color.GOLD);
        difficultyPanel.setStrokeWidth(3);
        difficultyPanel.setEffect(new DropShadow(18, Color.BLACK));
        difficultyRoot.getChildren().add(difficultyPanel);

        // Difficulty title
        Text difficultyText = new Text("Choose Difficulty");
        difficultyText.setFont(Font.font("Georgia", 36));
        difficultyText.setFill(Color.GOLD);
        difficultyText.setX(265);
        difficultyText.setY(150);
        difficultyText.setEffect(new DropShadow(10, Color.BLACK));
        difficultyRoot.getChildren().add(difficultyText);

        // Easy button
        Button startEasyGameButton = new Button("Easy");
        startEasyGameButton.setLayoutX(325);
        startEasyGameButton.setLayoutY(210);
        startEasyGameButton.setPrefSize(150, 42);
        startEasyGameButton.setFont(Font.font("Georgia", 16));
        startEasyGameButton.setStyle(
            "-fx-background-color: #355e3b;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        startEasyGameButton.setOnAction(event -> {
            // Stop menu music when the actual game starts
            musicManager.stopMenuMusic();

            primaryStage.setScene(gameScene);
            castle.reset();
            waveManager.reset();
            waveManager.setDifficulty("EASY");
            waveManager.spawnNextWave();
            player.reset();
            powerUpManager.reset();
            damageTexts.clear();
            lightningEffects.clear();
            resumeCountdown[0] = false;
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        difficultyRoot.getChildren().add(startEasyGameButton);

        // Medium button
        Button startMediumGameButton = new Button("Medium");
        startMediumGameButton.setLayoutX(325);
        startMediumGameButton.setLayoutY(265);
        startMediumGameButton.setPrefSize(150, 42);
        startMediumGameButton.setFont(Font.font("Georgia", 16));
        startMediumGameButton.setStyle(
            "-fx-background-color: #7a4a20;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        startMediumGameButton.setOnAction(event -> {
            // Stop menu music when the actual game starts
            musicManager.stopMenuMusic();

            primaryStage.setScene(gameScene);
            castle.reset();
            waveManager.reset();
            waveManager.setDifficulty("MEDIUM");
            waveManager.spawnNextWave();
            player.reset();
            powerUpManager.reset();
            damageTexts.clear();
            lightningEffects.clear();
            resumeCountdown[0] = false;
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        difficultyRoot.getChildren().add(startMediumGameButton);

        // Hard button
        Button startHardGameButton = new Button("Hard");
        startHardGameButton.setLayoutX(325);
        startHardGameButton.setLayoutY(320);
        startHardGameButton.setPrefSize(150, 42);
        startHardGameButton.setFont(Font.font("Georgia", 16));
        startHardGameButton.setStyle(
            "-fx-background-color: #6b1e1e;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        startHardGameButton.setOnAction(event -> {
            // Stop menu music when the actual game starts
            musicManager.stopMenuMusic();

            primaryStage.setScene(gameScene);
            castle.reset();
            waveManager.reset();
            waveManager.setDifficulty("HARD");
            waveManager.spawnNextWave();
            player.reset();
            powerUpManager.reset();
            damageTexts.clear();
            lightningEffects.clear();
            resumeCountdown[0] = false;
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        difficultyRoot.getChildren().add(startHardGameButton);

        // Back to menu button
        Button difficultyBackToMenuButton = new Button("Back to Main Menu");
        // Keep or restart menu music when returning from difficulty selection
        musicManager.playMenuMusic();

        difficultyBackToMenuButton.setLayoutX(305);
        difficultyBackToMenuButton.setLayoutY(390);
        difficultyBackToMenuButton.setPrefSize(190, 42);
        difficultyBackToMenuButton.setFont(Font.font("Georgia", 15));
        difficultyBackToMenuButton.setStyle(
            "-fx-background-color: #3b2a20;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        difficultyBackToMenuButton.setOnAction(event -> {
            gameState[0] = "MENU";
            primaryStage.setScene(startScene);
        });
        difficultyRoot.getChildren().add(difficultyBackToMenuButton);

        // Pause button in game screen
        Button pauseButton = new Button("Pause");
        pauseButton.setLayoutX(380);
        pauseButton.setLayoutY(20);
        pauseButton.setPrefSize(78, 26);
        pauseButton.setStyle(
            "-fx-background-color: rgba(128, 0, 0, 0.78);" +
                "-fx-text-fill: #f5f5f5;" +
                "-fx-border-color: rgba(255, 255, 255, 0.22);" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-font-size: 11px;"
        );
        pauseButton.setOnAction(event -> {
            if (gameState[0].equals("PLAYING")) {
                gameState[0] = "PAUSED";
            }
        });
        gameRoot.getChildren().add(pauseButton);

        // Resume button in pause screen
        Button resumeButton = new Button("Resume Battle");
        resumeButton.setLayoutX(315);
        resumeButton.setLayoutY(325);
        resumeButton.setPrefSize(170, 42);
        resumeButton.setFont(Font.font("Georgia", 16));
        resumeButton.setStyle(
            "-fx-background-color: #6b3e1e;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        resumeButton.setOnAction(event -> {
            resumeCountdown[0] = true;
            gameState[0] = "COUNTDOWN";
            countdownStart[0] = 0;
        });
        resumeButton.setVisible(false);
        gameRoot.getChildren().add(resumeButton);

        // Exit to menu button in pause screen
        Button pauseExitToMenuButton = new Button("Exit to Main Menu");
        // Restart menu music when leaving gameplay and returning to menu
        musicManager.playMenuMusic();

        pauseExitToMenuButton.setLayoutX(305);
        pauseExitToMenuButton.setLayoutY(380);
        pauseExitToMenuButton.setPrefSize(190, 42);
        pauseExitToMenuButton.setFont(Font.font("Georgia", 15));
        pauseExitToMenuButton.setStyle(
            "-fx-background-color: #3b2a20;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        pauseExitToMenuButton.setOnAction(event -> {
            player.setInput("");
            gameState[0] = "MENU";
            primaryStage.setScene(startScene);
        });
        pauseExitToMenuButton.setVisible(false);
        gameRoot.getChildren().add(pauseExitToMenuButton);

        // Win screen background image
        Image winBgImage = new Image(
            getClass().getResource("/assets/Castlewin.png").toExternalForm()
        );
        ImageView winBg = new ImageView(winBgImage);
        winBg.setFitWidth(800);
        winBg.setFitHeight(600);
        winBg.setPreserveRatio(false);
        winRoot.getChildren().add(winBg);

        // Win screen overlay for readable text
        Rectangle winOverlay = new Rectangle(800, 600);
        winOverlay.setFill(Color.rgb(0, 0, 0, 0.28));
        winRoot.getChildren().add(winOverlay);

        // Win screen panel
        Rectangle winPanel = new Rectangle(220, 125, 360, 310);
        winPanel.setArcWidth(25);
        winPanel.setArcHeight(25);
        winPanel.setFill(Color.rgb(43, 29, 22, 0.78));
        winPanel.setStroke(Color.GOLD);
        winPanel.setStrokeWidth(3);
        winPanel.setEffect(new DropShadow(18, Color.BLACK));
        winRoot.getChildren().add(winPanel);

        // Win screen title
        Text winText = new Text("Victory!");
        winText.setFont(Font.font("Georgia", 50));
        winText.setFill(Color.GOLD);
        winText.setX(305);
        winText.setY(198);
        winText.setEffect(new DropShadow(10, Color.BLACK));
        winRoot.getChildren().add(winText);

        // Win screen subtitle
        Text winSubText = new Text("The castle stands strong.");
        winSubText.setFont(Font.font("Georgia", 18));
        winSubText.setFill(Color.WHEAT);
        winSubText.setX(300);
        winSubText.setY(235);
        winSubText.setEffect(new DropShadow(8, Color.BLACK));
        winRoot.getChildren().add(winSubText);

        // Total score text in win screen
        Text winScoreText = new Text("");
        winScoreText.setFont(Font.font("Georgia", 28));
        winScoreText.setFill(Color.WHEAT);
        winScoreText.setX(330);
        winScoreText.setY(275);
        winScoreText.setEffect(new DropShadow(8, Color.BLACK));
        winRoot.getChildren().add(winScoreText);

        // Play again button in win screen
        Button winPlayAgainButton = new Button("Play Again");
        // Restart menu music when leaving gameplay and returning to menu
        musicManager.playMenuMusic();

        winPlayAgainButton.setLayoutX(325);
        winPlayAgainButton.setLayoutY(310);
        winPlayAgainButton.setPrefSize(150, 42);
        winPlayAgainButton.setFont(Font.font("Georgia", 16));
        winPlayAgainButton.setStyle(
            "-fx-background-color: #6b3e1e;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        winPlayAgainButton.setOnAction(event -> {
            primaryStage.setScene(difficultyScene);
            gameState[0] = "DIFFICULTY SELECTION";
        });
        winRoot.getChildren().add(winPlayAgainButton);

        // Back to menu button in win screen
        Button winBackToMenuButton = new Button("Back to Main Menu");
        winBackToMenuButton.setLayoutX(305);
        winBackToMenuButton.setLayoutY(365);
        winBackToMenuButton.setPrefSize(190, 42);
        winBackToMenuButton.setFont(Font.font("Georgia", 15));
        winBackToMenuButton.setStyle(
            "-fx-background-color: #3b2a20;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        winBackToMenuButton.setOnAction(event -> {
            // Restart menu music when returning from victory screen to main menu
            musicManager.playMenuMusic();

            gameState[0] = "MENU";
            player.setInput("");
            primaryStage.setScene(startScene);
        });
        winRoot.getChildren().add(winBackToMenuButton);

        // Lose screen background image
        Image loseBgImage = new Image(
            getClass().getResource("/assets/Castlelose.png").toExternalForm()
        );
        ImageView loseBg = new ImageView(loseBgImage);
        loseBg.setFitWidth(800);
        loseBg.setFitHeight(600);
        loseBg.setPreserveRatio(false);
        loseRoot.getChildren().add(loseBg);

        // Lose screen overlay for readable text
        Rectangle loseOverlay = new Rectangle(800, 600);
        loseOverlay.setFill(Color.rgb(0, 0, 0, 0.48));
        loseRoot.getChildren().add(loseOverlay);

        // Lose screen panel
        Rectangle losePanel = new Rectangle(220, 125, 360, 310);
        losePanel.setArcWidth(25);
        losePanel.setArcHeight(25);
        losePanel.setFill(Color.rgb(20, 10, 8, 0.80));
        losePanel.setStroke(Color.DARKRED);
        losePanel.setStrokeWidth(3);
        losePanel.setEffect(new DropShadow(18, Color.BLACK));
        loseRoot.getChildren().add(losePanel);

        // Lose screen title
        Text loseText = new Text("Defeat...");
        loseText.setFont(Font.font("Georgia", 50));
        loseText.setFill(Color.ORANGERED);
        loseText.setX(305);
        loseText.setY(198);
        loseText.setEffect(new DropShadow(10, Color.BLACK));
        loseRoot.getChildren().add(loseText);

        // Lose screen subtitle
        Text loseSubText = new Text("The castle has fallen.");
        loseSubText.setFont(Font.font("Georgia", 18));
        loseSubText.setFill(Color.WHEAT);
        loseSubText.setX(305);
        loseSubText.setY(235);
        loseSubText.setEffect(new DropShadow(8, Color.BLACK));
        loseRoot.getChildren().add(loseSubText);

        // Total score text in lose screen
        Text loseScoreText = new Text("");
        loseScoreText.setFont(Font.font("Georgia", 28));
        loseScoreText.setFill(Color.WHEAT);
        loseScoreText.setX(330);
        loseScoreText.setY(275);
        loseScoreText.setEffect(new DropShadow(8, Color.BLACK));
        loseRoot.getChildren().add(loseScoreText);

        // Play again button in lose screen
        Button losePlayAgainButton = new Button("Try Again");
        // Restart menu music when leaving gameplay and returning to menu
        musicManager.playMenuMusic();

        losePlayAgainButton.setLayoutX(325);
        losePlayAgainButton.setLayoutY(310);
        losePlayAgainButton.setPrefSize(150, 42);
        losePlayAgainButton.setFont(Font.font("Georgia", 16));
        losePlayAgainButton.setStyle(
            "-fx-background-color: #6b1e1e;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #d4af37;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        losePlayAgainButton.setOnAction(event -> {
            primaryStage.setScene(difficultyScene);
            gameState[0] = "DIFFICULTY SELECTION";
        });
        loseRoot.getChildren().add(losePlayAgainButton);

        // Back to menu button in lose screen
        Button loseBackToMenuButton = new Button("Back to Main Menu");
        loseBackToMenuButton.setLayoutX(305);
        loseBackToMenuButton.setLayoutY(365);
        loseBackToMenuButton.setPrefSize(190, 42);
        loseBackToMenuButton.setFont(Font.font("Georgia", 15));
        loseBackToMenuButton.setStyle(
            "-fx-background-color: #2b1d16;" +
                "-fx-text-fill: #fff2c2;" +
                "-fx-border-color: #8b0000;" +
                "-fx-border-width: 2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;"
        );
        loseBackToMenuButton.setOnAction(event -> {
            // Restart menu music when returning from defeat screen to main menu
            musicManager.playMenuMusic();

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

                // Show loading / countdown screen
                if (gameState[0].equals("COUNTDOWN")) {
                    pauseButton.setVisible(false);
                    resumeButton.setVisible(false);
                    pauseExitToMenuButton.setVisible(false);

                    if (countdownStart[0] == 0) {
                        countdownStart[0] = now;
                    }

                    long elapsed = now - countdownStart[0];
                    double progress = Math.min(elapsed / 3_000_000_000.0, 1.0);
                    int secondsLeft = 3 - (int) (elapsed / 1_000_000_000);
                    String loadingTitle;
                    String loadingSubtitle;
                    String loadingText;
                    String loadingTip;

                    if (resumeCountdown[0]) {
                        loadingTitle = "Restoring Battle Flow";
                        loadingSubtitle = "Magic returns to the battlefield.";
                        loadingText = "Recharging the defense spell...";
                        loadingTip =
                            "Tip: Use power-ups when the wave becomes dangerous.";
                    } else {
                        loadingTitle = "Battle Begins Soon";
                        loadingSubtitle =
                            "Prepare your typing skills, defender.";
                        loadingText = "Loading battle field...";
                        loadingTip =
                            "Tip: Type accurately to keep your combo alive.";
                    }
                    // Dark fantasy background
                    gc.setFill(Color.web("#101522"));
                    gc.fillRect(0, 0, 800, 600);

                    // Distant ground
                    gc.setFill(Color.web("#1b2433"));
                    gc.fillRect(0, 430, 800, 170);

                    // Simple castle silhouettes in background
                    gc.setFill(Color.web("#222b3d"));
                    gc.fillRect(80, 310, 80, 120);
                    gc.fillRect(170, 260, 90, 170);
                    gc.fillRect(280, 330, 80, 100);
                    gc.fillRect(520, 300, 90, 130);
                    gc.fillRect(630, 250, 90, 180);

                    // Castle tower roofs
                    gc.setFill(Color.web("#151a28"));
                    gc.fillPolygon(
                        new double[] { 70, 120, 170 },
                        new double[] { 310, 250, 310 },
                        3
                    );
                    gc.fillPolygon(
                        new double[] { 160, 215, 270 },
                        new double[] { 260, 190, 260 },
                        3
                    );
                    gc.fillPolygon(
                        new double[] { 620, 675, 730 },
                        new double[] { 250, 180, 250 },
                        3
                    );

                    // Small stars
                    gc.setFill(Color.web("#f8e7a2"));
                    gc.fillOval(90, 70, 3, 3);
                    gc.fillOval(160, 110, 4, 4);
                    gc.fillOval(250, 55, 3, 3);
                    gc.fillOval(470, 85, 4, 4);
                    gc.fillOval(600, 60, 3, 3);
                    gc.fillOval(710, 125, 4, 4);

                    // Main parchment shadow
                    gc.setFill(Color.rgb(0, 0, 0, 0.45));
                    gc.fillRoundRect(200, 125, 430, 350, 28, 28);

                    // Main parchment panel
                    gc.setFill(Color.web("#e8c982"));
                    gc.fillRoundRect(185, 110, 430, 350, 28, 28);

                    // Inner parchment panel
                    gc.setFill(Color.web("#f5dfaa"));
                    gc.fillRoundRect(205, 130, 390, 310, 20, 20);

                    // Outer parchment border
                    gc.setStroke(Color.web("#7a4a20"));
                    gc.setLineWidth(4);
                    gc.strokeRoundRect(185, 110, 430, 350, 28, 28);

                    // Inner parchment border
                    gc.setStroke(Color.web("#b8860b"));
                    gc.setLineWidth(2);
                    gc.strokeRoundRect(205, 130, 390, 310, 20, 20);

                    // Title text, centered
                    gc.setFont(Font.font("Georgia", 34));
                    gc.setFill(Color.web("#5a2d0c"));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText(loadingTitle, 400, 185);

                    // Subtitle text, centered
                    gc.setFont(Font.font("Georgia", 17));
                    gc.setFill(Color.web("#6b3e1e"));
                    gc.fillText(loadingSubtitle, 400, 220);

                    // Shield shape for countdown
                    gc.setFill(Color.web("#7a1f1f"));
                    gc.fillPolygon(
                        new double[] { 400, 345, 345, 400, 455, 455 },
                        new double[] { 245, 270, 330, 385, 330, 270 },
                        6
                    );

                    // Shield border
                    gc.setStroke(Color.GOLD);
                    gc.setLineWidth(4);
                    gc.strokePolygon(
                        new double[] { 400, 345, 345, 400, 455, 455 },
                        new double[] { 245, 270, 330, 385, 330, 270 },
                        6
                    );

                    // Countdown number, centered visually in the shield
                    gc.setFont(Font.font("Georgia", 64));
                    gc.setFill(Color.WHITE);
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setTextBaseline(VPos.CENTER);
                    gc.fillText(
                        String.valueOf(Math.max(secondsLeft, 1)),
                        400,
                        302
                    );

                    // Reset baseline after drawing centered countdown number
                    gc.setTextBaseline(VPos.BASELINE);

                    // Loading text, centered
                    gc.setFont(Font.font("Georgia", 18));
                    gc.setFill(Color.web("#5a2d0c"));
                    gc.fillText(loadingText, 400, 410);

                    // Loading bar background
                    gc.setFill(Color.web("#6b3e1e"));
                    gc.fillRoundRect(255, 425, 290, 22, 12, 12);

                    // Loading bar fill
                    gc.setFill(Color.web("#d4af37"));
                    gc.fillRoundRect(255, 425, 290 * progress, 22, 12, 12);

                    // Loading bar border
                    gc.setStroke(Color.web("#4a260d"));
                    gc.setLineWidth(2);
                    gc.strokeRoundRect(255, 425, 290, 22, 12, 12);

                    // Tip text, centered
                    gc.setFont(Font.font("Georgia", 14));
                    gc.setFill(Color.web("#f5dfaa"));
                    gc.fillText(loadingTip, 400, 515);

                    // Reset text alignment so the rest of the game UI does not become centered
                    gc.setTextAlign(TextAlignment.LEFT);

                    if (progress >= 1.0) {
                        gameState[0] = "PLAYING";
                    }

                    return;
                }

                // Show pause screen
                if (gameState[0].equals("PAUSED")) {
                    pauseButton.setVisible(false);
                    resumeButton.setVisible(true);
                    pauseExitToMenuButton.setVisible(true);

                    // Dark fantasy background
                    gc.setFill(Color.web("#101522"));
                    gc.fillRect(0, 0, 800, 600);

                    // Ground shadow
                    gc.setFill(Color.web("#1b2433"));
                    gc.fillRect(0, 430, 800, 170);

                    // Castle silhouettes in background
                    gc.setFill(Color.web("#222b3d"));
                    gc.fillRect(80, 310, 80, 120);
                    gc.fillRect(170, 260, 90, 170);
                    gc.fillRect(280, 330, 80, 100);
                    gc.fillRect(520, 300, 90, 130);
                    gc.fillRect(630, 250, 90, 180);

                    // Castle tower roofs
                    gc.setFill(Color.web("#151a28"));
                    gc.fillPolygon(
                        new double[] { 70, 120, 170 },
                        new double[] { 310, 250, 310 },
                        3
                    );
                    gc.fillPolygon(
                        new double[] { 160, 215, 270 },
                        new double[] { 260, 190, 260 },
                        3
                    );
                    gc.fillPolygon(
                        new double[] { 620, 675, 730 },
                        new double[] { 250, 180, 250 },
                        3
                    );

                    // Small stars
                    gc.setFill(Color.web("#f8e7a2"));
                    gc.fillOval(90, 70, 3, 3);
                    gc.fillOval(160, 110, 4, 4);
                    gc.fillOval(250, 55, 3, 3);
                    gc.fillOval(470, 85, 4, 4);
                    gc.fillOval(600, 60, 3, 3);
                    gc.fillOval(710, 125, 4, 4);

                    // Pause panel shadow
                    gc.setFill(Color.rgb(0, 0, 0, 0.45));
                    gc.fillRoundRect(200, 125, 430, 350, 28, 28);

                    // Main parchment panel
                    gc.setFill(Color.web("#e8c982"));
                    gc.fillRoundRect(185, 110, 430, 350, 28, 28);

                    // Inner parchment panel
                    gc.setFill(Color.web("#f5dfaa"));
                    gc.fillRoundRect(205, 130, 390, 310, 20, 20);

                    // Outer border
                    gc.setStroke(Color.web("#7a4a20"));
                    gc.setLineWidth(4);
                    gc.strokeRoundRect(185, 110, 430, 350, 28, 28);

                    // Inner border
                    gc.setStroke(Color.web("#b8860b"));
                    gc.setLineWidth(2);
                    gc.strokeRoundRect(205, 130, 390, 310, 20, 20);

                    // Pause title
                    gc.setFont(Font.font("Georgia", 46));
                    gc.setFill(Color.web("#5a2d0c"));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText("Game Paused", 400, 190);

                    // Pause subtitle
                    gc.setFont(Font.font("Georgia", 18));
                    gc.setFill(Color.web("#6b3e1e"));
                    gc.fillText(
                        "The battlefield waits for your return.",
                        400,
                        230
                    );

                    // Decorative divider
                    gc.setStroke(Color.web("#b8860b"));
                    gc.setLineWidth(2);
                    gc.strokeLine(270, 260, 530, 260);

                    // Small instruction text
                    gc.setFont(Font.font("Georgia", 16));
                    gc.setFill(Color.web("#5a2d0c"));
                    gc.fillText(
                        "Resume when you are ready to defend the castle.",
                        400,
                        300
                    );

                    // Reset text alignment
                    gc.setTextAlign(TextAlignment.LEFT);
                }
                // Show game screen
                if (gameState[0].equals("PLAYING")) {
                    gc.drawImage(background, 0, 0, 800, 600);
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

                        if (enemy.isDeathFinished()) {
                            iterator.remove();
                            continue;
                        }

                        if (enemy.isDying()) {
                            enemy.render(gc);
                            continue;
                        }

                        if (enemy.getX() >= 550) {
                            castle.takeDamage(enemy.getDamage());
                            player.resetCombo(0);
                            powerUpManager.resetLastComboMilestone();
                            iterator.remove();
                            continue;
                        }

                        enemy.move();
                        enemy.render(gc);
                    }
                    // Update and render floating damage texts
                    Iterator<DamageText> dtIterator = damageTexts.iterator();
                    while (dtIterator.hasNext()) {
                        DamageText dt = dtIterator.next();
                        dt.update();
                        dt.render(gc);
                        if (dt.isExpired()) dtIterator.remove();
                    }
                    // Render lightning effects when word is typed
                    Iterator<LightningEffect> ltIterator =
                        lightningEffects.iterator();
                    while (ltIterator.hasNext()) {
                        LightningEffect fx = ltIterator.next();
                        fx.update();
                        fx.render(gc);
                        gc.setImageSmoothing(false);
                        if (fx.isExpired()) ltIterator.remove();
                    }
                    // Top HUD panel
                    gc.setFill(Color.rgb(10, 15, 25, 0.75));
                    gc.fillRoundRect(12, 10, 776, 76, 18, 18);
                    gc.setStroke(Color.rgb(255, 255, 255, 0.18));
                    gc.strokeRoundRect(12, 10, 776, 76, 18, 18);

                    // Wave / score / combo
                    gc.setFont(Font.font("Verdana", 16));
                    gc.setFill(Color.WHITESMOKE);
                    gc.fillText(
                        "Wave " + (waveManager.getWaveNumber() - 1),
                        28,
                        37
                    );
                    gc.fillText("Score: " + player.getScore(), 28, 63);
                    gc.fillText("Combo: " + player.getCombo(), 170, 37);

                    // Powerup display
                    String currentPowerUp = "NONE";

                    if (powerUpManager.isSlowActive()) {
                        currentPowerUp = String.format(
                            "SLOW ENEMIES (%.1fs)",
                            powerUpManager.getSlowRemaining(now)
                        );
                    } else if (powerUpManager.isDoublePointsActive()) {
                        currentPowerUp = String.format(
                            "DOUBLE POINTS (%.1fs)",
                            powerUpManager.getDoublePointsRemaining(now)
                        );
                    } else if (powerUpManager.hasPowerUp()) {
                        currentPowerUp = powerUpManager
                            .getStoredPowerUps()
                            .get(0)
                            .toString();
                    }

                    gc.setFont(Font.font("Verdana", 16));
                    gc.setFill(Color.GOLD);
                    gc.fillText("Power-Up: " + currentPowerUp, 170, 63);

                    // HP bar
                    gc.setFill(Color.WHITESMOKE);
                    gc.fillText("HP", 490, 37);
                    gc.setFill(Color.rgb(255, 255, 255, 0.14));
                    gc.fillRoundRect(520, 24, 230, 16, 8, 8);
                    gc.setFill(Color.DARKRED);
                    gc.fillRoundRect(
                        520,
                        24,
                        230 * castle.getHealthPercent(),
                        16,
                        8,
                        8
                    );
                    gc.setStroke(Color.rgb(255, 255, 255, 0.20));
                    gc.strokeRoundRect(520, 24, 230, 16, 8, 8);

                    // Input box
                    gc.setFill(Color.rgb(0, 0, 0, 0.55));
                    gc.fillRoundRect(220, 538, 360, 42, 14, 14);
                    gc.setStroke(Color.rgb(255, 255, 255, 0.35));
                    gc.strokeRoundRect(220, 538, 360, 42, 14, 14);

                    gc.setFont(Font.font("Verdana", 18));
                    gc.setFill(Color.WHITESMOKE);
                    gc.fillText("Word: ", 235, 566);
                    gc.setFill(Color.AZURE);
                    gc.fillText(player.getInput(), 300, 566);

                    // Castle block in game screen
                    gc.drawImage(castle.getCurrentSprite(), 600, 50, 400, 300);
                    gc.drawImage(castle.getCurrentSprite(), 600, 200, 400, 300);

                    // Lose and win situation
                    if (castle.isDestroyed()) {
                        gameState[0] = "ENDED";
                        loseScoreText.setText("Score: " + player.getScore());
                        primaryStage.setScene(loseScene);
                    } else if (waveManager.isWaveCleared()) {
                        if (waveManager.isFinalWaveCleared()) {
                            gameState[0] = "ENDED";
                            winScoreText.setText("Score: " + player.getScore());
                            primaryStage.setScene(winScene);
                        } else {
                            waveManager.spawnNextWave();
                        }
                    }
                }
            }
        };
        //        winScoreText.setText("Score : 999");
        //        gameState[0] = "ENDED";
        //        primaryStage.setScene(winScene);
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
