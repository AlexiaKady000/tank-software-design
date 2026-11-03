package ru.mipt.bit.platformer.model;

public interface Damageable extends GameObject {
    void damage(int amount);
    boolean isAlive();
}
