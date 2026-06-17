package com.fantasytypinggame;

public class Player {

    private String currentInput = "";
    private int score = 0;
    private int combo = 0;

    public void setInput(String input) {
        this.currentInput = input;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void resetCombo(int combo) {
        this.combo = 0;
    }

    public String getInput() {
        return this.currentInput;
    }

    public int getScore() {
        return this.score;
    }

    public int getCombo() {
        return this.combo;
    }

    public void updateInput(String key) {
        this.currentInput = this.currentInput + key;
    }

    public void removeLastInput() {
        this.currentInput = this.currentInput.substring(
            0,
            this.currentInput.length() - 1
        );
    }

    public void addScore(int points) {
        this.score = this.score + points;
    }

    public void increaseCombo() {
        this.combo++;
    }

    public void reset() {
        this.currentInput = "";
        this.score = 0;
    }
}
