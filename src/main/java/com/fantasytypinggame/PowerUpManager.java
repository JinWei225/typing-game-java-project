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

    private ArrayList<PowerUpType> storedPowerUps = new ArrayList<
        PowerUpType
    >();
    private int lastComboMilestone = 0;

    // Timed effect state
    private boolean slowActive = false;
    private boolean doublePointsActive = false;
    private long slowEndTime = 0;
    private long doublePointsEndTime = 0;
    private boolean enemiesSlowed = false;

    // Check combo and award a random power-up at each milestone
    public void checkCombo(int combo) {
        int milestone = (combo / COMBO_INTERVAL) * COMBO_INTERVAL;
        if (milestone > 0 && milestone > lastComboMilestone) {
            lastComboMilestone = milestone;
            awardRandomPowerUp();
        }
    }

    private void awardRandomPowerUp() {
        PowerUpType[] types = PowerUpType.values();
        PowerUpType awarded = types[(int) (Math.random() * types.length)];
        storedPowerUps.add(awarded);
    }

    public boolean hasPowerUp() {
        return !storedPowerUps.isEmpty();
    }

    public List<PowerUpType> getStoredPowerUps() {
        return storedPowerUps;
    }

    // Called when player presses '\' to use power-ups
    public PowerUpType activateNext(ArrayList<Enemy> enemies, long now) {
        if (storedPowerUps.isEmpty()) return null;

        PowerUpType type = storedPowerUps.remove(0);
        switch (type) {
            case DAMAGE_ALL:
                for (Enemy e : enemies) e.takeDamage(1);
                break;
            case SLOW_ENEMIES:
                slowActive = true;
                slowEndTime = now + EFFECT_DURATION_NS;
                enemiesSlowed = false; // Mark for application next update
                break;
            case DOUBLE_POINTS:
                doublePointsActive = true;
                doublePointsEndTime = now + EFFECT_DURATION_NS;
                break;
        }
        return type;
    }

    // applying/reverting timed effects handler
    public void update(ArrayList<Enemy> enemies, long now) {
        // Apply slow to enemies if not yet applied this activation
        if (slowActive && !enemiesSlowed) {
            for (Enemy e : enemies) e.applySpeedMultiplier(0.5);
            enemiesSlowed = true;
        }

        // Revert slow when duration expires
        if (slowActive && now >= slowEndTime) {
            for (Enemy e : enemies) e.applySpeedMultiplier(2.0); // restore
            slowActive = false;
            enemiesSlowed = false;
        }

        // Expire double points
        if (doublePointsActive && now >= doublePointsEndTime) {
            doublePointsActive = false;
        }
    }

    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }

    public boolean isSlowActive() {
        return slowActive;
    }

    // Returns remaining seconds of a timed effect, for HUD display
    public double getSlowRemaining(long now) {
        return slowActive
            ? Math.max(0, (slowEndTime - now) / 1_000_000_000.0)
            : 0;
    }

    public double getDoublePointsRemaining(long now) {
        return doublePointsActive
            ? Math.max(0, (doublePointsEndTime - now) / 1_000_000_000.0)
            : 0;
    }

    public void reset() {
        storedPowerUps.clear();
        lastComboMilestone = 0;
        slowActive = false;
        doublePointsActive = false;
        enemiesSlowed = false;
    }
}
