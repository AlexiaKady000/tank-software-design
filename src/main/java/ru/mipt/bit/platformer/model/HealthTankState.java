package ru.mipt.bit.platformer.model;

/**
 * Type - enum, role - state policy
 */
public enum HealthTankState implements TankState {
    LIGHT(1f, true),
    MEDIUM(0.5f, true),
    HEAVY(1f / 3f, false);

    private final float movementSpeedMultiplier;
    private final boolean shootingAllowed;

    HealthTankState(float movementSpeedMultiplier, boolean shootingAllowed) {
        this.movementSpeedMultiplier = movementSpeedMultiplier;
        this.shootingAllowed = shootingAllowed;
    }

    @Override
    public float getMovementSpeedMultiplier() {
        return movementSpeedMultiplier;
    }

    @Override
    public boolean canShoot() {
        return shootingAllowed;
    }

    @Override
    public TankState onHealthChanged(Health health) {
        long scaledHealth = health.getHealth() * 100L;
        if (scaledHealth > health.getMaxHealth() * 70L) {
            return LIGHT;
        }
        if (scaledHealth >= health.getMaxHealth() * 15L) {
            return MEDIUM;
        }
        return HEAVY;
    }
}
