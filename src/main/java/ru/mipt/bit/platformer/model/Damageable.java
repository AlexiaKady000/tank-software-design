package ru.mipt.bit.platformer.model;

public interface Damageable extends GameObject, Health {
    void damage(int amount);
    boolean isAlive();
}
