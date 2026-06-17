package com.fantasytypinggame;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FastMob extends Enemy {

    FastMob(
        double x,
        double y,
        double speed,
        int health,
        int damage,
        ArrayList<String> wordList
    ) {
        super(x, y, speed, health, damage, wordList);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(getX(), getY(), 40, 40);
        gc.setFont(Font.font("Arial", 14));
        gc.setFill(Color.RED);
        gc.fillText(getWordList().get(0), getX(), getY() - 10);
    }

    @Override
    public int onDeath() {
        return 20;
    }
}
