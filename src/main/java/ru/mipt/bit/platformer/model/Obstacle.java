package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Type - interface, role - internal capability interface
 */
public interface Obstacle extends GameObject {
    boolean blocks(GridPoint2 coordinates);

    @Override
    default boolean occupies(GridPoint2 coordinates) {
        return blocks(coordinates);
    }
}
