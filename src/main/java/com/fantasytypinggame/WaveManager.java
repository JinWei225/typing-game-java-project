package com.fantasytypinggame;

import java.util.ArrayList;
import java.util.Arrays;

public class WaveManager {

    private String difficulty = "EASY";
    private final double fastMobSpeed = 0.55;
    private final double smallMobSpeed = 0.3;
    private final double bossSpeed = 0.15;
    private int waveNumber = 1;
    private int totalWaveNumber;
    private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

    // Get list of words from WordBank and convert it from array into ArrayList
    private ArrayList<String> fastMobWordPool = new ArrayList<String>(
        Arrays.asList(WordBank.getEasyWords())
    );
    private ArrayList<String> smallMobWordPool = new ArrayList<String>(
        Arrays.asList(WordBank.getMediumWords())
    );
    private ArrayList<String> bossWordPool = new ArrayList<String>(
        Arrays.asList(WordBank.getBossWords())
    );

    WaveManager(int totalWaveNumber) {
        this.totalWaveNumber = totalWaveNumber;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public ArrayList<Enemy> getActiveEnemies() {
        return enemies;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public boolean isWaveCleared() {
        return enemies.isEmpty();
    }

    public boolean isFinalWaveCleared() {
        return (this.waveNumber > this.totalWaveNumber && enemies.isEmpty());
    }

    private ArrayList<String> getFastMobWord() {
        int count = 0;
        switch (this.difficulty) {
            case "EASY":
            case "MEDIUM":
                count = 1;
                break;
            case "HARD":
                count = 2;
                break;
        }
        if (count > fastMobWordPool.size()) {
            fastMobWordPool = new ArrayList<String>(
                Arrays.asList(WordBank.getEasyWords())
            );
        }
        ArrayList<String> wordList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            int index = (int) (Math.random() * fastMobWordPool.size());
            String word = fastMobWordPool.remove(index);
            wordList.add(word);
        }
        return wordList;
    }

    private ArrayList<String> getSmallMobWord() {
        int count = 0;
        switch (this.difficulty) {
            case "EASY":
                count = 1;
                break;
            case "MEDIUM":
            case "HARD":
                count = 2;
                break;
        }
        if (count > smallMobWordPool.size()) {
            smallMobWordPool = new ArrayList<String>(
                Arrays.asList(WordBank.getMediumWords())
            );
        }
        ArrayList<String> wordList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            int index = (int) (Math.random() * smallMobWordPool.size());
            String word = smallMobWordPool.remove(index);
            wordList.add(word);
        }
        return wordList;
    }

    private ArrayList<String> getBossWord() {
        int count = 0;
        switch (this.difficulty) {
            case "EASY":
                count = 2;
                break;
            case "MEDIUM":
                count = 3;
                break;
            case "HARD":
                count = 4;
                break;
        }
        if (count > bossWordPool.size()) {
            bossWordPool = new ArrayList<String>(
                Arrays.asList(WordBank.getBossWords())
            );
        }
        ArrayList<String> wordList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            int index = (int) (Math.random() * bossWordPool.size());
            String word = bossWordPool.remove(index);
            wordList.add(word);
        }
        return wordList;
    }

    public void spawnNextWave() {
        int num = this.waveNumber;
        int remainder = num % 5;
        int numBoss = num / 5;
        int numFast = 0;
        int numSmall = 0;
        double availableHeight = 400;

        if (remainder > 0) {
            numSmall = remainder / 2;
            numFast = remainder % 2 == 1 ? numSmall + 1 : numSmall;
        }

        double startX = 0;
        int bossHealth = 0;
        int bossDamage = 0;
        int fastMobHealth = 0;
        int fastMobDamage = 0;
        int smallMobHealth = 0;
        int smallMobDamage = 0;
        switch (this.difficulty) {
            case "EASY": {
                bossHealth = 2;
                bossDamage = 20;
                fastMobHealth = 1;
                fastMobDamage = 10;
                smallMobHealth = 1;
                smallMobDamage = 10;
                break;
            }
            case "MEDIUM": {
                bossHealth = 3;
                bossDamage = 30;
                fastMobHealth = 1;
                fastMobDamage = 15;
                smallMobHealth = 2;
                smallMobDamage = 15;
                break;
            }
            case "HARD": {
                bossHealth = 4;
                bossDamage = 40;
                fastMobHealth = 2;
                fastMobDamage = 20;
                smallMobHealth = 2;
                smallMobDamage = 20;
                break;
            }
        }

        int totalEnemies = numBoss + numFast + numSmall;
        double spacing = availableHeight / (totalEnemies + 1);
        int enemyIndex = 0;

        for (int i = 0; i < numBoss; i++) {
            double y = 50 + spacing * (enemyIndex + 1);
            enemies.add(
                new Boss(
                    startX,
                    y,
                    bossSpeed,
                    bossHealth,
                    bossDamage,
                    getBossWord()
                )
            );
            enemyIndex++;
        }
        for (int i = 0; i < numFast; i++) {
            double y = 50 + spacing * (enemyIndex + 1);
            enemies.add(
                new FastMob(
                    startX,
                    y,
                    fastMobSpeed,
                    fastMobHealth,
                    fastMobDamage,
                    getFastMobWord()
                )
            );
            enemyIndex++;
        }
        for (int i = 0; i < numSmall; i++) {
            double y = 50 + spacing * (enemyIndex + 1);
            enemies.add(
                new SmallMobs(
                    startX,
                    y,
                    smallMobSpeed,
                    smallMobHealth,
                    smallMobDamage,
                    getSmallMobWord()
                )
            );
            enemyIndex++;
        }
        waveNumber++;
    }

    // Reset to initial state to prepare for new game
    public void reset() {
        this.fastMobWordPool = new ArrayList<String>(
            Arrays.asList(WordBank.getEasyWords())
        );
        this.smallMobWordPool = new ArrayList<String>(
            Arrays.asList(WordBank.getMediumWords())
        );
        this.bossWordPool = new ArrayList<String>(
            Arrays.asList(WordBank.getBossWords())
        );
        this.enemies = new ArrayList<Enemy>();
        this.waveNumber = 1;
    }
}
