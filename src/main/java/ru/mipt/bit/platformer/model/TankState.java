package ru.mipt.bit.platformer.model;

/**
 * Type - interface, role - internal state interface
 */
public interface TankState {
    float getMovementSpeedMultiplier();

    default float getFireRateMultiplier() {
        return 1f;
    }

    default boolean canShoot() {
        return true;
    }

    default TankState onHealthChanged(Health health) {
        return this;
    }
}
