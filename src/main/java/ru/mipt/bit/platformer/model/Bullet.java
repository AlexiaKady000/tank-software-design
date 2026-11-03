package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public class Bullet implements GameObject {
    private static final float TIME_EPSILON = 0.000001f;

    private final GridPoint2 coordinates;
    private final GridPoint2 previousCoordinates;
    private final Direction direction;
    private final GameObject owner;
    private final float movementDuration;
    private final int damage;
    private float elapsed;

    public Bullet(GridPoint2 coordinates, Direction direction, GameObject owner,
                  float movementDuration, int damage) {
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f || damage <= 0) {
            throw new IllegalArgumentException("Bullet duration and damage must be positive");
        }
        this.coordinates = new GridPoint2(coordinates);
        previousCoordinates = new GridPoint2(coordinates);
        this.direction = direction;
        this.owner = owner;
        this.movementDuration = movementDuration;
        this.damage = damage;
    }

    @Override
    public void live(float tick, WorldContext world) {
        previousCoordinates.set(coordinates);
        elapsed += tick;
        if (elapsed + TIME_EPSILON >= movementDuration) {
            elapsed = Math.max(0f, elapsed - movementDuration);
            coordinates.set(direction.move(coordinates));
            if (!world.isInsideField(coordinates)) {
                world.removeObject(this);
            }
        }
    }

    @Override
    public float timeToNextEvent() {
        return Math.max(0f, movementDuration - elapsed);
    }

    @Override
    public boolean occupies(GridPoint2 coordinates) {
        return this.coordinates.equals(coordinates);
    }

    @Override
    public boolean canCollideWith(GameObject other) {
        return other != this && other != owner;
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }

    public GridPoint2 getPreviousCoordinates() {
        return new GridPoint2(previousCoordinates);
    }

    public int getDamage() {
        return damage;
    }
}
