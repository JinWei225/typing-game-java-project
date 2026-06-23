package com.fantasytypinggame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class LightningEffect {

    private final SpriteAnimator animator;
    private final double x;
    private final double y;
    private final double size;

    LightningEffect(double x, double y) {
        Image sheet = new Image(
            getClass().getResourceAsStream("/assets/lightining2-Sheet.png")
        );

        this.animator = new SpriteAnimator(sheet, 64, 64, 6, 6, 2, false);
        this.x = x - 24;
        this.y = y - 24;
        this.size = 96;
    }

    public void update() {
        animator.update();
    }

    public void render(GraphicsContext gc) {
        gc.save();

        // soft glow behind it
        gc.setGlobalAlpha(0.35);
        animator.draw(gc, x - 8, y - 8, size + 16, size + 16);

        // main bolt
        gc.setGlobalAlpha(1.0);
        animator.draw(gc, x, y, size, size);

        gc.restore();
    }

    public boolean isExpired() {
        return animator.isFinished();
    }
}
