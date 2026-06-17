package com.fantasytypinggame;

public class Castle {

    private int maxHealth;
    private int health;

    Castle(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
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
}
