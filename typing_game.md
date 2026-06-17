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



&#x20;   private Stage primaryStage;



&#x20;   // Check input word match with enemy word or not

&#x20;   private void checkPlayerInput(

&#x20;       ArrayList<Enemy> enemyList,

&#x20;       Player player,

&#x20;       PowerUpManager powerUpManager,

&#x20;       ArrayList<DamageText> damageTexts

&#x20;   ) {

&#x20;       Iterator<Enemy> iterator = enemyList.iterator();

&#x20;       while (iterator.hasNext()) {

&#x20;           Enemy enemy = iterator.next();

&#x20;           String currentWord = enemy.getCurrentWord();

&#x20;           if (currentWord != null \&\& currentWord.equals(player.getInput())) {

&#x20;               enemy.removeCurrentWord();

&#x20;               enemy.takeDamage(1);

&#x20;               player.setInput("");

&#x20;               damageTexts.add(

&#x20;                   new DamageText(enemy.getX() + 20, enemy.getY() - 15, "-1")

&#x20;               );

&#x20;               if (enemy.isDefeated()) {

&#x20;                   int point = enemy.onDeath();

&#x20;                   if (powerUpManager.isDoublePointsActive()) point \*= 2;

&#x20;                   player.addScore(point);

&#x20;                   player.increaseCombo();

&#x20;                   powerUpManager.checkCombo(player.getCombo());

&#x20;                   iterator.remove();

&#x20;               } else {

&#x20;                   // Enemy hit but not yet defeated — show damage text

&#x20;                   damageTexts.add(

&#x20;                       new DamageText(

&#x20;                           enemy.getX() + 20,

&#x20;                           enemy.getY() - 15,

&#x20;                           "-1"

&#x20;                       )

&#x20;                   );

&#x20;               }

&#x20;           }

&#x20;       }

&#x20;   }



&#x20;   @Override

&#x20;   public void start(Stage stage) {

&#x20;       this.primaryStage = stage;

&#x20;       Group menuRoot = new Group();

&#x20;       Group difficultyRoot = new Group();

&#x20;       Group gameRoot = new Group();

&#x20;       Group loseRoot = new Group();

&#x20;       Group winRoot = new Group();

&#x20;       

&#x20;       **// Create Group Container**

&#x20;       **Group instructionRoot = new Group();**



&#x20;       Scene startScene = new Scene(menuRoot, 800, 600, Color.BEIGE);

&#x20;       Scene difficultyScene = new Scene(

&#x20;           difficultyRoot,

&#x20;           800,

&#x20;           600,

&#x20;           Color.BEIGE

&#x20;       );

&#x20;       Scene gameScene = new Scene(gameRoot, 800, 600, Color.BEIGE);

&#x20;       Scene loseScene = new Scene(loseRoot, 800, 600, Color.LIGHTGRAY);

&#x20;       Scene winScene = new Scene(winRoot, 800, 600, Color.LIGHTGRAY);

&#x20;       

&#x20;       **// Create Scene Container**

&#x20;       **Scene instructionScene = new Scene(instructionRoot, 800, 600, Color.BEIGE);**



&#x20;       // Initialize game window and setup

&#x20;       stage.setTitle("Fantasy Typing Game");

&#x20;       stage.setScene(startScene);

&#x20;       Canvas canvas = new Canvas(800, 600);

&#x20;       gameRoot.getChildren().add(canvas);

&#x20;       GraphicsContext gc = canvas.getGraphicsContext2D();

&#x20;       stage.show();



&#x20;       String\[] gameState = { "MENU" };

&#x20;       long\[] countdownStart = { 0 };

&#x20;       Castle castle = new Castle(100); // Castle total health is 100

&#x20;       WaveManager waveManager = new WaveManager(30); // Number of waves per game is 20

&#x20;       Player player = new Player(); // Initialize player object

&#x20;       PowerUpManager powerUpManager = new PowerUpManager();

&#x20;       ArrayList<DamageText> damageTexts = new ArrayList<>();



&#x20;       // Player input handler

&#x20;       gameScene.setOnKeyPressed(event -> {

&#x20;           if (event.getCode() == KeyCode.BACK\_SPACE) {

&#x20;               if (player.getInput().length() > 0) {

&#x20;                   player.removeLastInput();

&#x20;               }

&#x20;           } else if (event.getCode() == KeyCode.ESCAPE) {

&#x20;               if (gameState\[0].equals("PLAYING")) {

&#x20;                   gameState\[0] = "PAUSED";

&#x20;               } else if (gameState\[0].equals("PAUSED")) {

&#x20;                   gameState\[0] = "COUNTDOWN";

&#x20;                   countdownStart\[0] = 0;

&#x20;               }

&#x20;           } else {

&#x20;               String key = event.getText();

&#x20;               if (key.equals("\\\\")) {

&#x20;                   PowerUpManager.PowerUpType used =

&#x20;                       powerUpManager.activateNext(

&#x20;                           waveManager.getActiveEnemies(),

&#x20;                           System.nanoTime()

&#x20;                       );

&#x20;                   if (used == PowerUpManager.PowerUpType.DAMAGE\_ALL) {

&#x20;                       Iterator<Enemy> iterator = waveManager

&#x20;                           .getActiveEnemies()

&#x20;                           .iterator();

&#x20;                       while (iterator.hasNext()) {

&#x20;                           Enemy enemy = iterator.next();

&#x20;                           damageTexts.add(

&#x20;                               new DamageText(

&#x20;                                   enemy.getX() + 20,

&#x20;                                   enemy.getY() - 15,

&#x20;                                   "-1"

&#x20;                               )

&#x20;                           );

&#x20;                           if (enemy.isDefeated()) {

&#x20;                               int point = enemy.onDeath();

&#x20;                               if (powerUpManager.isDoublePointsActive()) {

&#x20;                                   point \*= 2;

&#x20;                               }

&#x20;                               player.addScore(point);

&#x20;                               player.increaseCombo();

&#x20;                               powerUpManager.checkCombo(player.getCombo());

&#x20;                               iterator.remove();

&#x20;                           }

&#x20;                       }

&#x20;                   }

&#x20;               }

&#x20;               if (!key.isBlank() \&\& Character.isLetter(key.charAt(0))) {

&#x20;                   player.updateInput(key);

&#x20;                   checkPlayerInput(

&#x20;                       waveManager.getActiveEnemies(),

&#x20;                       player,

&#x20;                       powerUpManager,

&#x20;                       damageTexts

&#x20;                   );

&#x20;               }

&#x20;           }

&#x20;       });



&#x20;       // Game title on menu screen

&#x20;       Text titleText = new Text("Typing Game");

&#x20;       titleText.setFont(Font.font("Verdana", 40));

&#x20;       titleText.setFill(Color.BROWN);

&#x20;       titleText.setX(270);

&#x20;       titleText.setY(200);

&#x20;       menuRoot.getChildren().add(titleText);



&#x20;       // Start game button

&#x20;       Button startGameButton = new Button("Start Game");

&#x20;       startGameButton.setLayoutX(357);

&#x20;       startGameButton.setLayoutY(250);

&#x20;       startGameButton.setOnAction(event -> {

&#x20;           primaryStage.setScene(difficultyScene);

&#x20;           gameState\[0] = "DIFFICULTY SELECTION";

&#x20;       });

&#x20;       menuRoot.getChildren().add(startGameButton);



&#x20;       **// Button in Main Menu**

&#x20;       **Button menuInstructionsButton = new Button("How to Play");**

&#x20;       **menuInstructionsButton.setLayoutX(356);**

&#x20;       **menuInstructionsButton.setLayoutY(300); // Placed between Start and Exit**

&#x20;       **menuInstructionsButton.setOnAction(event -> {**

&#x20;           **gameState\[0] = "INSTRUCTIONS";**

&#x20;           **primaryStage.setScene(instructionScene);**

&#x20;       **});**

&#x20;       **menuRoot.getChildren().add(menuInstructionsButton);**



&#x20;       // Exit game button (Shifted down layout Y to 350 to make room)

&#x20;       Button exitButton = new Button("Exit Game");

&#x20;       exitButton.setLayoutX(360);

&#x20;       exitButton.setLayoutY(350);

&#x20;       exitButton.setOnAction(event -> {

&#x20;           primaryStage.close();

&#x20;       });

&#x20;       menuRoot.getChildren().add(exitButton);



&#x20;       **// Build Instruction View Content**

&#x20;       **Text instructTitleText = new Text("How to Play");**

&#x20;       **instructTitleText.setFont(Font.font("Verdana", 36));**

&#x20;       **instructTitleText.setFill(Color.BROWN);**

&#x20;       **instructTitleText.setX(300);**

&#x20;       **instructTitleText.setY(100);**

&#x20;       **instructionRoot.getChildren().add(instructTitleText);**



&#x20;       **Text instructBodyText = new Text(**

&#x20;           **"OBJECTIVE:\\n" +**

&#x20;           **"   Defend your castle on the right. Survive all 30 waves to win!\\n\\n" +**

&#x20;           **"CONTROLS:\\n" +**

&#x20;           **"   • Type words appearing above enemies to attack them.\\n" +**

&#x20;           **"   • Press 'BACKSPACE' to fix typing mistakes.\\n" +**

&#x20;           **"   • Press backslash '\\\\' to use earned Power-ups (Damage All, Slow, etc.).\\n" +**

&#x20;           **"   • Press 'ESC' or click the Pause button to freeze the game.\\n\\n" +**

&#x20;           **"BONUS:\\n" +**

&#x20;           **"   Maintain a continuous typing Combo to automatically gain Power-ups!"**

&#x20;       **);**

&#x20;       **instructBodyText.setFont(Font.font("Verdana", 15));**

&#x20;       **instructBodyText.setX(100);**

&#x20;       **instructBodyText.setY(180);**

&#x20;       **instructBodyText.setLineSpacing(8);**

&#x20;       **instructionRoot.getChildren().add(instructBodyText);**

&#x20;     

&#x20;       **// Backward Navigation Button Configuration**

&#x20;       **Button instructBackButton = new Button("Back to Main Menu");**

&#x20;       **instructBackButton.setLayoutX(335);**

&#x20;       **instructBackButton.setLayoutY(500);**

&#x20;       **instructBackButton.setOnAction(event -> {**

&#x20;           **gameState\[0] = "MENU";**

&#x20;           **primaryStage.setScene(startScene); // Swaps stage back to the default menu scene**

&#x20;       **});**

&#x20;       **instructionRoot.getChildren().add(instructBackButton);**



&#x20;       // Difficulty screen title text

&#x20;       Text difficultyText = new Text("Difficulty:");

&#x20;       difficultyText.setFont(Font.font("Verdana", 40));

&#x20;       difficultyText.setFill(Color.BROWN);

&#x20;       difficultyText.setX(320);

&#x20;       difficultyText.setY(100);

&#x20;       difficultyRoot.getChildren().add(difficultyText);



&#x20;       // Start easy game button

&#x20;       Button startEasyGameButton = new Button("Easy");

&#x20;       startEasyGameButton.setLayoutX(385);

&#x20;       startEasyGameButton.setLayoutY(150);

&#x20;       startEasyGameButton.setOnAction(event -> {

&#x20;           primaryStage.setScene(gameScene);

&#x20;           castle.reset();

&#x20;           waveManager.reset();

&#x20;           waveManager.setDifficulty("EASY");

&#x20;           waveManager.spawnNextWave();

&#x20;           player.reset();

&#x20;           powerUpManager.reset();

&#x20;           damageTexts.clear();

&#x20;           gameState\[0] = "COUNTDOWN";

&#x20;           countdownStart\[0] = 0;

&#x20;       });

&#x20;       difficultyRoot.getChildren().add(startEasyGameButton);



&#x20;       // Start medium game button

&#x20;       Button startMediumGameButton = new Button("Medium");

&#x20;       startMediumGameButton.setLayoutX(375);

&#x20;       startMediumGameButton.setLayoutY(200);

&#x20;       startMediumGameButton.setOnAction(event -> {

&#x20;           primaryStage.setScene(gameScene);

&#x20;           castle.reset();

&#x20;           waveManager.reset();

&#x20;           waveManager.setDifficulty("MEDIUM");

&#x20;           waveManager.spawnNextWave();

&#x20;           player.reset();

&#x20;           powerUpManager.reset();

&#x20;           damageTexts.clear();

&#x20;           gameState\[0] = "COUNTDOWN";

&#x20;           countdownStart\[0] = 0;

&#x20;       });

&#x20;       difficultyRoot.getChildren().add(startMediumGameButton);



&#x20;       // Start hard game button

&#x20;       Button startHardGameButton = new Button("Hard");

&#x20;       startHardGameButton.setLayoutX(385);

&#x20;       startHardGameButton.setLayoutY(250);

&#x20;       startHardGameButton.setOnAction(event -> {

&#x20;           primaryStage.setScene(gameScene);

&#x20;           castle.reset();

&#x20;           waveManager.reset();

&#x20;           waveManager.setDifficulty("HARD");

&#x20;           waveManager.spawnNextWave();

&#x20;           player.reset();

&#x20;           powerUpManager.reset();

&#x20;           damageTexts.clear();

&#x20;           gameState\[0] = "COUNTDOWN";

&#x20;           countdownStart\[0] = 0;

&#x20;       });

&#x20;       difficultyRoot.getChildren().add(startHardGameButton);



&#x20;       // Back to menu button in difficulty screen

&#x20;       Button difficultyBackToMenuButton = new Button("Back to Main Menu");

&#x20;       difficultyBackToMenuButton.setLayoutX(345);

&#x20;       difficultyBackToMenuButton.setLayoutY(300);

&#x20;       difficultyBackToMenuButton.setOnAction(event -> {

&#x20;           gameState\[0] = "MENU";

&#x20;           primaryStage.setScene(startScene);

&#x20;       });

&#x20;       difficultyRoot.getChildren().add(difficultyBackToMenuButton);



&#x20;       // Pause button in game screen

&#x20;       Button pauseButton = new Button("Pause");

&#x20;       pauseButton.setLayoutX(460);

&#x20;       pauseButton.setLayoutY(5);

&#x20;       pauseButton.setOnAction(event -> {

&#x20;           if (gameState\[0].equals("PLAYING")) {

&#x20;               gameState\[0] = "PAUSED";

&#x20;           }

&#x20;       });

&#x20;       gameRoot.getChildren().add(pauseButton);



&#x20;       // Resume button in pause screen

&#x20;       Button resumeButton = new Button("Resume");

&#x20;       resumeButton.setLayoutX(370);

&#x20;       resumeButton.setLayoutY(330);

&#x20;       resumeButton.setOnAction(event -> {

&#x20;           gameState\[0] = "COUNTDOWN";

&#x20;           countdownStart\[0] = 0;

&#x20;       });

&#x20;       resumeButton.setVisible(false);

&#x20;       gameRoot.getChildren().add(resumeButton);



&#x20;       // Exit to menu button in pause screen

&#x20;       Button pauseExitToMenuButton = new Button("Exit to Main Menu");

&#x20;       pauseExitToMenuButton.setLayoutX(345);

&#x20;       pauseExitToMenuButton.setLayoutY(380);

&#x20;       pauseExitToMenuButton.setOnAction(event -> {

&#x20;           player.setInput("");

&#x20;           gameState\[0] = "MENU";

&#x20;           primaryStage.setScene(startScene);

&#x20;       });

&#x20;       gameRoot.getChildren().add(pauseExitToMenuButton);



&#x20;       // Play again button in win screen

&#x20;       Button winPlayAgainButton = new Button("Play Again");

&#x20;       winPlayAgainButton.setLayoutX(360);

&#x20;       winPlayAgainButton.setLayoutY(300);

&#x20;       winPlayAgainButton.setOnAction(event -> {

&#x20;           primaryStage.setScene(difficultyScene);

&#x20;           gameState\[0] = "DIFFICULTY SELECTION";

&#x20;       });

&#x20;       winRoot.getChildren().add(winPlayAgainButton);



&#x20;       // Play again button in lose screen

&#x20;       Button losePlayAgainButton = new Button("Play Again");

&#x20;       losePlayAgainButton.setLayoutX(360);

&#x20;       losePlayAgainButton.setLayoutY(300);

&#x20;       losePlayAgainButton.setOnAction(event -> {

&#x20;           primaryStage.setScene(difficultyScene);

&#x20;           gameState\[0] = "DIFFICULTY SELECTION";

&#x20;       });

&#x20;       loseRoot.getChildren().add(losePlayAgainButton);



&#x20;       // Win screen display

&#x20;       Text winText = new Text("You Win!");

&#x20;       winText.setFont(Font.font("Verdana", 40));

&#x20;       winText.setFill(Color.GREEN);

&#x20;       winText.setX(315);

&#x20;       winText.setY(200);

&#x20;       winRoot.getChildren().add(winText);



&#x20;       // Lose screen display

&#x20;       Text loseText = new Text("You Lose!");

&#x20;       loseText.setFont(Font.font("Verdana", 40));

&#x20;       loseText.setFill(Color.RED);

&#x20;       loseText.setX(315);

&#x20;       loseText.setY(200);

&#x20;       loseRoot.getChildren().add(loseText);



&#x20;       // Total score text in win screen

&#x20;       Text winScoreText = new Text("");

&#x20;       winScoreText.setFont(Font.font("Verdana", 30));

&#x20;       winScoreText.setFill(Color.BLACK);

&#x20;       winScoreText.setX(315);

&#x20;       winScoreText.setY(275);

&#x20;       winRoot.getChildren().add(winScoreText);



&#x20;       // Total score text in lose screen

&#x20;       Text loseScoreText = new Text("");

&#x20;       loseScoreText.setFont(Font.font("Verdana", 30));

&#x20;       loseScoreText.setFill(Color.BLACK);

&#x20;       loseScoreText.setX(315);

&#x20;       loseScoreText.setY(275);

&#x20;       loseRoot.getChildren().add(loseScoreText);



&#x20;       // Back to menu button in win screen

&#x20;       Button winBackToMenuButton = new Button("Back to Main Menu");

&#x20;       winBackToMenuButton.setLayoutX(336);

&#x20;       winBackToMenuButton.setLayoutY(340);

&#x20;       winBackToMenuButton.setOnAction(event -> {

&#x20;           gameState\[0] = "MENU";

&#x20;           player.setInput("");

&#x20;           primaryStage.setScene(startScene);

&#x20;       });

&#x20;       winRoot.getChildren().add(winBackToMenuButton);



&#x20;       // Back to menu button in lose screen

&#x20;       Button loseBackToMenuButton = new Button("Back to Main Menu");

&#x20;       loseBackToMenuButton.setLayoutX(336);

&#x20;       loseBackToMenuButton.setLayoutY(340);

&#x20;       loseBackToMenuButton.setOnAction(event -> {

&#x20;           gameState\[0] = "MENU";

&#x20;           player.setInput("");

&#x20;           primaryStage.setScene(startScene);

&#x20;       });

&#x20;       loseRoot.getChildren().add(loseBackToMenuButton);



&#x20;       AnimationTimer gameLoop = new AnimationTimer() {

&#x20;           @Override

&#x20;           public void handle(long now) {

&#x20;               // Clear screen

&#x20;               gc.clearRect(0, 0, 800, 600);



&#x20;               // Show countdown screen

&#x20;               if (gameState\[0].equals("COUNTDOWN")) {

&#x20;                   pauseButton.setVisible(false);

&#x20;                   resumeButton.setVisible(false);

&#x20;                   pauseExitToMenuButton.setVisible(false);

&#x20;                   if (countdownStart\[0] == 0) countdownStart\[0] = now;

&#x20;                   long elapsed = now - countdownStart\[0];

&#x20;                   int secondsLeft = 3 - (int) (elapsed / 1\_000\_000\_000);



&#x20;                   if (secondsLeft > 0) {

&#x20;                       gc.setFont(Font.font("Verdana", 80));

&#x20;                       gc.setFill(Color.BLACK);

&#x20;                       gc.fillText(String.valueOf(secondsLeft), 380, 320);

&#x20;                       return;

&#x20;                   } else {

&#x20;                       gameState\[0] = "PLAYING";

&#x20;                   }

&#x20;               }



&#x20;               // Show pause screen

&#x20;               if (gameState\[0].equals("PAUSED")) {

&#x20;                   gc.setFont(Font.font("Verdana", 60));

&#x20;                   gc.setFill(Color.BLACK);

&#x20;                   gc.fillText("PAUSED", 290, 280);

&#x20;                   pauseButton.setVisible(false);

&#x20;                   resumeButton.setVisible(true);

&#x20;                   pauseExitToMenuButton.setVisible(true);

&#x20;               }

&#x20;               // Show game screen

&#x20;               if (gameState\[0].equals("PLAYING")) {

&#x20;                   // Hide unnecessary button

&#x20;                   pauseButton.setVisible(true);

&#x20;                   resumeButton.setVisible(false);

&#x20;                   pauseExitToMenuButton.setVisible(false);



&#x20;                   // Update screen content

&#x20;                   Iterator<Enemy> iterator = waveManager

&#x20;                       .getActiveEnemies()

&#x20;                       .iterator();

&#x20;                   powerUpManager.update(waveManager.getActiveEnemies(), now);

&#x20;                   while (iterator.hasNext()) {

&#x20;                       Enemy enemy = iterator.next();

&#x20;                       if (enemy.getX() >= 600) {

&#x20;                           castle.takeDamage(enemy.getDamage());

&#x20;                           player.resetCombo(0);

&#x20;                           iterator.remove();

&#x20;                       } else {

&#x20;                           enemy.move();

&#x20;                           enemy.render(gc);

&#x20;                       }

&#x20;                   }



&#x20;                   // Update and render floating damage texts

&#x20;                   Iterator<DamageText> dtIterator = damageTexts.iterator();

&#x20;                   while (dtIterator.hasNext()) {

&#x20;                       DamageText dt = dtIterator.next();

&#x20;                       dt.update();

&#x20;                       dt.render(gc);

&#x20;                       if (dt.isExpired()) dtIterator.remove();

&#x20;                   }



&#x20;                   // Score text update

&#x20;                   gc.setFont(Font.font("Verdana", 20));

&#x20;                   gc.setFill(Color.GREEN);

&#x20;                   gc.fillText("Score: " + player.getScore() + "", 10, 20);



&#x20;                   // Combo text update

&#x20;                   gc.setFont(Font.font("Verdana", 20));

&#x20;                   gc.setFill(Color.GREEN);

&#x20;                   gc.fillText("Combo: " + player.getCombo() + "", 150, 20);



&#x20;                   // Power-up inventory display

&#x20;                   gc.setFont(Font.font("Verdana", 16));

&#x20;                   gc.setFill(Color.PURPLE);

&#x20;                   String puDisplay =

&#x20;                       "Power-ups: " +

&#x20;                       powerUpManager.getStoredPowerUps().size();

&#x20;                   gc.fillText(puDisplay, 300, 20);



&#x20;                   // Active effect timers

&#x20;                   if (powerUpManager.isSlowActive()) {

&#x20;                       gc.setFill(Color.CYAN);

&#x20;                       gc.fillText(

&#x20;                           String.format(

&#x20;                               "SLOW: %.1fs",

&#x20;                               powerUpManager.getSlowRemaining(now)

&#x20;                           ),

&#x20;                           300,

&#x20;                           45

&#x20;                       );

&#x20;                   }

&#x20;                   if (powerUpManager.isDoublePointsActive()) {

&#x20;                       gc.setFill(Color.GOLD);

&#x20;                       gc.fillText(

&#x20;                           String.format(

&#x20;                               "2x PTS: %.1fs",

&#x20;                               powerUpManager.getDoublePointsRemaining(now)

&#x20;                           ),

&#x20;                           300,

&#x20;                           65

&#x20;                       );

&#x20;                   }



&#x20;                   // Wave number update

&#x20;                   gc.setFont(Font.font("Verdana", 20));

&#x20;                   gc.setFill(Color.BLACK);

&#x20;                   gc.fillText(

&#x20;                       "Wave Number: " +

&#x20;                           (waveManager.getWaveNumber() - 1) +

&#x20;                           "",

&#x20;                       10,

&#x20;                       50

&#x20;                   );



&#x20;                   // Health bar update

&#x20;                   gc.setFill(Color.GRAY);

&#x20;                   gc.fillRect(600, 20, 150, 10);

&#x20;                   gc.setFill(Color.RED);

&#x20;                   gc.fillRect(600, 20, 150 \* castle.getHealthPercent(), 10);

&#x20;                   gc.setFill(Color.BLUE);

&#x20;                   gc.fillText(player.getInput(), 310, 570);



&#x20;                   // Castle block in game screen

&#x20;                   gc.setFill(Color.BROWN);

&#x20;                   gc.fillRect(600, 50, 150, 500);



&#x20;                   // Lose and win situation

&#x20;                   if (castle.isDestroyed()) {

&#x20;                       gameState\[0] = "ENDED";

&#x20;                       primaryStage.setScene(loseScene);

&#x20;                       loseScoreText.setText("Score: " + player.getScore());

&#x20;                   } else if (waveManager.isWaveCleared()) {

&#x20;                       if (waveManager.isFinalWaveCleared()) {

&#x20;                           gameState\[0] = "ENDED";

&#x20;                           primaryStage.setScene(winScene);

&#x20;                           winScoreText.setText("Score: " + player.getScore());

&#x20;                       } else {

&#x20;                           waveManager.spawnNextWave();

&#x20;                       }

&#x20;                   }

&#x20;               }

&#x20;           }

&#x20;       };

&#x20;       gameLoop.start();

&#x20;   }



&#x20;   public static void main(String\[] args) {

&#x20;       launch(args);

&#x20;   }

}

