package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import static com.badlogic.gdx.math.MathUtils.clamp;
import static com.badlogic.gdx.math.MathUtils.isEqual;

public class Tank implements Updatable, Obstacle, Damageable {
    private final float movementDuration;
    private final float shotInterval;
    private final float bulletMovementDuration;
    private int health = 3;
    private float shotCooldown;

    private final GridPoint2 coordinates;
    private final GridPoint2 destinationCoordinates;
    private float movementProgress = 1f;
    private Direction direction = Direction.RIGHT;

    public Tank(GridPoint2 initialCoordinates) {
        this(initialCoordinates, 0.4f);
    }

    public Tank(GridPoint2 initialCoordinates, float movementDuration) {
        this(initialCoordinates, movementDuration, 0.4f, 0.08f);
    }

    public Tank(GridPoint2 initialCoordinates, float movementDuration,
                float shotInterval, float bulletMovementDuration) {
        if (!Float.isFinite(shotInterval) || shotInterval <= 0f
                || !Float.isFinite(bulletMovementDuration) || bulletMovementDuration <= 0f) {
            throw new IllegalArgumentException("Shot interval and bullet duration must be positive");
        }
        this.shotInterval = shotInterval;
        this.bulletMovementDuration = bulletMovementDuration;
        if (!Float.isFinite(movementDuration) || movementDuration <= 0f) {
            throw new IllegalArgumentException("Movement duration must be positive and finite");
        }
        this.movementDuration = movementDuration;
        coordinates = new GridPoint2(initialCoordinates);
        destinationCoordinates = new GridPoint2(initialCoordinates);
    }

    public void move(Direction direction, MovementField field) {
        if (!isAlive() || isMoving()) {
            return;
        }
        this.direction = direction;
        GridPoint2 newDestination = direction.move(coordinates);
        if (field.canMoveTo(newDestination)) {
            destinationCoordinates.set(newDestination);
            movementProgress = 0f;
        }
    }

    public void shoot(WorldContext world) {
        if (!isAlive() || !world.contains(this) || shotCooldown > 0f) {
            return;
        }
        shotCooldown = shotInterval;
        Bullet bullet = new Bullet(direction.move(coordinates), direction, this, bulletMovementDuration, 1);
        world.addObject(bullet);
        if (!world.isInsideField(bullet.getCoordinates())) {
            world.removeObject(bullet);
        }
    }

    @Override
    public void damage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage must not be negative");
        }
        health = Math.max(0, health - amount);
    }

    @Override
    public boolean isAlive() {
        return health > 0;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public void live(float tick, WorldContext world) {
        update(tick);
    }

    @Override
    public float timeToNextEvent() {
        return isMoving() ? (1f - movementProgress) * movementDuration : Float.POSITIVE_INFINITY;
    }

    @Override
    public void update(float deltaTime) {
        shotCooldown = Math.max(0f, shotCooldown - deltaTime);
        if (isEqual(shotCooldown, 0f)) {
            shotCooldown = 0f;
        }
        if (!isAlive() || !isMoving()) {
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