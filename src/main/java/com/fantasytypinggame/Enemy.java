package com.fantasytypinggame;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;

public abstract class Enemy {

    private double x;
    private double y;
    private double pace;
    private int health;
    private int damage;
    private ArrayList<String> wordList = new ArrayList<String>();

    Enemy(
        double x,
        double y,
        double pace,
        int health,
        int damage,
        ArrayList<String> wordList
    ) {
        this.x = x;
        this.y = y;
        this.pace = pace;
        this.health = health;
        this.damage = damage;
        this.wordList = wordList;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getPace() {
        return this.pace;
    }

    public int getHealth() {
        return this.health;
    }

    public int getDamage() {
        return damage;
    }

    public ArrayList<String> getWordList() {
        return this.wordList;
    }

    public String getCurrentWord() {
        if (wordList.isEmpty()) return null;
        return this.wordList.get(0);
    }

    public void removeCurrentWord() {
        if (!this.wordList.isEmpty()) this.wordList.remove(0);
    }

    public void applySpeedMultiplier(double multiplier) {
        this.pace = this.pace * multiplier;
    }

    public void move() {
        this.x = this.x + pace;
    }

    public void takeDamage(int amount) {
        this.health = this.health - amount;
        if (this.health < 0) this.health = 0;
    }

    public boolean isDefeated() {
        if (this.health == 0) {
            return true;
        } else {
            return false;
        }
    }

    public abstract void render(GraphicsContext gc);

    public abstract int onDeath();
}
