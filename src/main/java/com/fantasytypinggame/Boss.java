package com.fantasytypinggame;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Boss extends Enemy {

    private SpriteAnimator walkAnimator;
    private SpriteAnimator deathAnimator;
    private boolean dying = false;

    Boss(
        double x,
        double y,
        double pace,
        int health,
        int damage,
        ArrayList<String> wordList
    ) {
        super(x, y, pace, health, damage, wordList);

        Image walkSheet = new Image(
            getClass().getResourceAsStream("/assets/boss.png")
        );

        Image deathSheet = new Image(
            getClass().getResourceAsStream("/assets/boss_die.png")
        );

        walkAnimator = new SpriteAnimator(walkSheet, 150, 150, 4, 10, 8);
        deathAnimator = new SpriteAnimator(
            deathSheet,
            250,
            250,
            4,
            11,
            2,
            false
        );
    }

    @Override
    public void beginDeath() {
        if (!dying) {
            dying = true;
            deathAnimator.reset();
        }
    }

    @Override
    public boolean isDying() {
        return dying;
    }

    @Override
    public boolean isDeathFinished() {
        return dying && deathAnimator.isFinished();
    }

    @Override
    public void render(GraphicsContext gc) {
        double drawWidth = 135;
        double drawHeight = 135;

        if (dying) {
            deathAnimator.draw(gc, getX(), getY(), drawWidth, drawHeight);
            deathAnimator.update();
        } else {
            walkAnimator.draw(gc, getX(), getY(), drawWidth, drawHeight);
            walkAnimator.update();
        }

        String word = getCurrentWord();
        if (word != null) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.setFill(
                isSlowed() ? Color.rgb(100, 210, 255) : Color.rgb(255, 165, 50)
            );

            Text temp = new Text(word);
            temp.setFont(gc.getFont());
            double textX =
                getX() + (drawWidth - temp.getLayoutBounds().getWidth()) / 2;

            gc.fillText(word, textX, getY() - 10);
        }
    }

    @Override
    public int onDeath() {
        return 40;
    }
}
