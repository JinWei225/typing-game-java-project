package com.fantasytypinggame;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SmallMobs extends Enemy {

    private SpriteAnimator walkAnimator;
    private SpriteAnimator deathAnimator;
    private boolean dying = false;

    SmallMobs(
        double x,
        double y,
        double pace,
        int health,
        int damage,
        ArrayList<String> wordList
    ) {
        super(x, y, pace, health, damage, wordList);

        Image walkSheet = new Image(
            getClass().getResourceAsStream("/assets/smallmob.png")
        );

        Image deathSheet = new Image(
            getClass().getResourceAsStream("/assets/smallmob_die.png")
        );

        walkAnimator = new SpriteAnimator(walkSheet, 200, 200, 4, 11, 8);
        deathAnimator = new SpriteAnimator(
            deathSheet,
            200,
            200,
            6,
            21,
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
        double drawWidth = 113;
        double drawHeight = 113;

        if (dying) {
            deathAnimator.draw(gc, getX(), getY(), drawWidth, drawHeight);
            deathAnimator.update();
        } else {
            walkAnimator.draw(gc, getX(), getY(), drawWidth, drawHeight);
            walkAnimator.update();
        }

        String word = getCurrentWord();
        if (word != null) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(isSlowed() ? Color.rgb(100, 210, 255) : Color.YELLOW);

            Text temp = new Text(word);
            temp.setFont(gc.getFont());
            double textX =
                getX() + (drawWidth - temp.getLayoutBounds().getWidth()) / 2;

            gc.fillText(word, textX, getY() - 8);
        }
    }

    @Override
    public int onDeath() {
        return 10;
    }
}
