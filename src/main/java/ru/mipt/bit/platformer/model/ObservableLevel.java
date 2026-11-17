package ru.mipt.bit.platformer.model;

/**
 * Type - interface, role - subscription interface
 */
public interface ObservableLevel {
    void addObserver(LevelObserver observer);
    void removeObserver(LevelObserver observer);
}
