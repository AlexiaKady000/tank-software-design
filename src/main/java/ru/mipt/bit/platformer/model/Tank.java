package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import static com.badlogic.gdx.math.MathUtils.clamp;
import static com.badlogic.gdx.math.MathUtils.isEqual;

public class Tank implements Updatable, Obstacle {
    private final float movementDuration;

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    public Tank(GridPoint2 initialCoordinates) {
        this(initialCoordinates, 0.4f);
    }

    public Tank(GridPoint2 initialCoordinates, float movementDuration) {
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        this.movementDuration = movementDuration;
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
    }

    public void move(Direction direction, MovementField field) {
        if (isMoving()) {
            return;
        }
        this.direction = direction;
        GridPoint2 newDestination = direction.move(coordinates);
        if (field.canMoveTo(newDestination)) {
            destinationCoordinates.set(newDestination);
            movementProgress = 0f;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!isMoving()) {
            return;
        }
        movementProgress = clamp(movementProgress + deltaTime / movementDuration, 0f, 1f);
        if (isEqual(movementProgress, 1f)) {
            coordinates.set(destinationCoordinates);
        }
    }

    @Override
    public boolean blocks(GridPoint2 coordinates) {
        return this.coordinates.equals(coordinates)
                || (isMoving() && destinationCoordinates.equals(coordinates));
    }

    public boolean isMoving() {
        return !isEqual(movementProgress, 1f);
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }

    public GridPoint2 getDestinationCoordinates() {
        return new GridPoint2(destinationCoordinates);
    }

    public float getMovementProgress() {
        return movementProgress;
    }

    public Direction getDirection() {
        return direction;
    }
}