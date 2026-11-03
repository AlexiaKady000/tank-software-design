package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface GameObject {
    boolean occupies(GridPoint2 coordinates);

    default void live(float tick, WorldContext world) {
    }

    default float timeToNextEvent() {
        return Float.POSITIVE_INFINITY;
    }

    default boolean canCollideWith(GameObject other) {
        return this != other;
    }
}
