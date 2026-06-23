package com.fantasytypinggame;

import java.util.ArrayList;
import java.util.List;

public class PowerUpManager {

    public enum PowerUpType {
        DAMAGE_ALL, // Deduct 1 HP from all enemies
        SLOW_ENEMIES, // Halve all enemy speeds for 2 seconds
        DOUBLE_POINTS, // Double points for kills in 2 seconds
    }

    private static final int COMBO_INTERVAL = 10; // Award power-up every 10 combos
    private static final long EFFECT_DURATION_NS = 10_000_000_000L; // 10 seconds in nanoseconds

    private ArrayList<PowerUpType> storedPowerUps =
        new ArrayList<PowerUpType>();
    private int lastComboMilestone = 0;

    // Timed effect state
    private boolean slowActive = false;
    private boolean doublePointsActive = false;
    private boolean enemiesSlowed;
    private long slowEndTime = 0;
    private long doublePointsEndTime = 0;

    // Check combo and award a random power-up at each milestone
    public void checkCombo(int combo) {
        int milestone = (combo / COMBO_INTERVAL) * COMBO_INTERVAL;
        if (milestone > 0 && milestone > this.lastComboMilestone) {
            this.lastComboMilestone = milestone;
            awardRandomPowerUp();
        }
    }

    private void awardRandomPowerUp() {
        PowerUpType[] types = PowerUpType.values();
        PowerUpType awarded = types[(int) (Math.random() * types.length)];
        this.storedPowerUps.add(awarded);
    }

    public boolean hasPowerUp() {
        return !this.storedPowerUps.isEmpty();
    }

    public List<PowerUpType> getStoredPowerUps() {
        return this.storedPowerUps;
    }

    // Called when player presses '\' to use power-ups
    public PowerUpType activateNext(ArrayList<Enemy> enemies, long now) {
        if (this.storedPowerUps.isEmpty()) return null;

        PowerUpType type = this.storedPowerUps.remove(0);
        switch (type) {
            case DAMAGE_ALL:
                for (Enemy e : enemies) e.takeDamage(1);
                break;
            case SLOW_ENEMIES:
                this.slowActive = true;
                this.slowEndTime = now + EFFECT_DURATION_NS;
                break;
            case DOUBLE_POINTS:
                this.doublePointsActive = true;
                this.doublePointsEndTime = now + EFFECT_DURATION_NS;
                break;
        }
        return type;
    }

    // applying/reverting timed effects handler
    public void update(ArrayList<Enemy> enemies, long now) {
        // Slow any enemy not yet affected (catches new spawns mid-effect)
        if (this.slowActive) {
            for (Enemy e : enemies) {
                if (!e.isSlowed()) {
                    e.applySpeedMultiplier(0.5);
                    e.setSlowed(true);
                }
            }
            this.enemiesSlowed = true;
        }

        // Revert slow when duration expires
        if (this.slowActive && now >= this.slowEndTime) {
            for (Enemy e : enemies) {
                if (e.isSlowed()) {
                    e.applySpeedMultiplier(2.0); // restore only enemies that were actually slowed
                    e.setSlowed(false);
                }
            }
            this.slowActive = false;
            this.enemiesSlowed = false;
        }

        // Expire double points
        if (this.doublePointsActive && now >= this.doublePointsEndTime) {
            this.doublePointsActive = false;
        }
    }

    public boolean isDoublePointsActive() {
        return this.doublePointsActive;
    }

    public boolean isSlowActive() {
        return this.slowActive;
    }

    // Returns remaining seconds of a timed effect, for HUD display
    public double getSlowRemaining(long now) {
        return this.slowActive
            ? Math.max(0, (this.slowEndTime - now) / 1_000_000_000.0)
            : 0;
    }

    public double getDoublePointsRemaining(long now) {
        return this.doublePointsActive
            ? Math.max(0, (this.doublePointsEndTime - now) / 1_000_000_000.0)
            : 0;
    }

    public void resetLastComboMilestone() {
        this.lastComboMilestone = 0;
    }

    public void reset() {
        this.storedPowerUps.clear();
        this.lastComboMilestone = 0;
        this.slowActive = false;
        this.doublePointsActive = false;
    }
}
