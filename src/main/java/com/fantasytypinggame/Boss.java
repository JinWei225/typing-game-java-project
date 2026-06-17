package com.fantasytypinggame;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Boss extends Enemy {

    Boss(
        double x,
        double y,
        double pace,
        int health,
        int damage,
        ArrayList<String> wordList
    ) {
        super(x, y, pace, health, damage, wordList);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(getX(), getY(), 60, 90);
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.RED);
        gc.fillText(getWordList().get(0), getX(), getY() - 10);
    }

    @Override
    public int onDeath() {
        return 40;
    }
}
