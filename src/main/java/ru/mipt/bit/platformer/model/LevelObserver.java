package ru.mipt.bit.platformer.model;

public interface LevelObserver {
    void objectAdded(GameObject object);
    void objectRemoved(GameObject object);
}
