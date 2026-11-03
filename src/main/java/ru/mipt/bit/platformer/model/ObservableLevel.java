package ru.mipt.bit.platformer.model;

public interface ObservableLevel {
    void addObserver(LevelObserver observer);
    void removeObserver(LevelObserver observer);
}
