package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Type - interface, role - internal domain service interface
 */
public interface MovementField {
    boolean canMoveTo(GridPoint2 coordinates);
}
