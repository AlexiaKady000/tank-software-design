package ru.mipt.bit.platformer.model;

/**
 * Type - interface, role - internal capability interface
 */
public interface Damageable extends GameObject, Health {
    void damage(int amount);
    boolean isAlive();
}
