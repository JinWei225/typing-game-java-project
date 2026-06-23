package com.fantasytypinggame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DamageText {

    private double x;
    private double y;
    private String text;
    private double opacity;
    private static final double RISE_SPEED = 0.8;
    private static final double FADE_SPEED = 0.02;

    DamageText(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.opacity = 1.0;
    }

    // Move upward and fade out each tick
    public void update() {
        this.y -= RISE_SPEED;
        this.opacity -= FADE_SPEED;
    }

    public boolean isExpired() {
        return this.opacity <= 0;
    }

    public void render(GraphicsContext gc) {
        gc.save(); // preserve current gc state
        gc.setGlobalAlpha(this.opacity);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setFill(Color.RED);
        gc.fillText(this.text, this.x, this.y);
        gc.restore(); // restore gc state so other renders are unaffected
    }
}
