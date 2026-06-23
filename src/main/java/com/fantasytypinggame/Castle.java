package com.fantasytypinggame;

import javafx.scene.image.Image;

public class Castle {

    private int maxHealth;
    private int health;
    private Image castleFull;
    private Image castleDamaged;
    private Image castleCritical;

    Castle(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        castleFull = new Image(
            getClass().getResourceAsStream("/assets/castle_full.png")
        );
        castleDamaged = new Image(
            getClass().getResourceAsStream("/assets/castle_damaged.png")
        );
        castleCritical = new Image(
            getClass().getResourceAsStream("/assets/castle_critical.png")
        );
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getHealthPercent() {
        return (double) health / maxHealth;
    }

    public void takeDamage(int amount) {
        this.health = this.health - amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public boolean isDestroyed() {
        if (this.health == 0) {
            return true;
        }
        return false;
    }

    public void reset() {
        this.health = maxHealth;
    }

    public Image getCurrentSprite() {
        double hpPercent = getHealthPercent();

        if (hpPercent > 0.7) {
            return castleFull;
        }

        if (hpPercent > 0.3) {
            return castleDamaged;
        }

        return castleCritical;
    }
}
