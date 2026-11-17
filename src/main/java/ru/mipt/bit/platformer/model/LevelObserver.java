package ru.mipt.bit.platformer.model;

/**
 * Type - interface, role - outbound port
 */
public interface LevelObserver {
    void objectAdded(GameObject object);
    void objectRemoved(GameObject object);
}
