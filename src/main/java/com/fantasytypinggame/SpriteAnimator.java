package com.fantasytypinggame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteAnimator {

    private final Image sheet;
    private final int frameWidth;
    private final int frameHeight;
    private final int columns;
    private final int frameCount;
    private final int ticksPerFrame;
    private final boolean looping;

    private int frameIndex = 0;
    private int tickCounter = 0;
    private boolean finished = false;

    public SpriteAnimator(
        Image sheet,
        int frameWidth,
        int frameHeight,
        int columns,
        int frameCount,
        int ticksPerFrame
    ) {
        this(
            sheet,
            frameWidth,
            frameHeight,
            columns,
            frameCount,
            ticksPerFrame,
            true
        );
    }

    public SpriteAnimator(
        Image sheet,
        int frameWidth,
        int frameHeight,
        int columns,
        int frameCount,
        int ticksPerFrame,
        boolean looping
    ) {
        this.sheet = sheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.columns = columns;
        this.frameCount = frameCount;
        this.ticksPerFrame = ticksPerFrame;
        this.looping = looping;
    }

    // reset the animation sheet once frame is finished
    public void reset() {
        frameIndex = 0;
        tickCounter = 0;
        finished = false;
    }

    // updates the frame of the animation sheet
    public void update() {
        if (finished) {
            return;
        }

        tickCounter++;

        if (tickCounter >= ticksPerFrame) {
            tickCounter = 0;
            frameIndex++;

            if (frameIndex >= frameCount) {
                if (looping) {
                    frameIndex = 0;
                } else {
                    frameIndex = frameCount - 1;
                    finished = true;
                }
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void draw(
        GraphicsContext gc,
        double x,
        double y,
        double drawWidth,
        double drawHeight
    ) {
        int col = frameIndex % columns;
        int row = frameIndex / columns;

        gc.drawImage(
            sheet,
            col * frameWidth,
            row * frameHeight,
            frameWidth,
            frameHeight,
            x,
            y,
            drawWidth,
            drawHeight
        );
    }
}
