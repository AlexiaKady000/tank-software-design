package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.collision.CollisionSystem;
import ru.mipt.bit.platformer.model.collision.GameCollisions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field implements WorldContext, ObservableLevel {
    private static final float MIN_TIME_STEP = 0.000001f;

    private final int width;
    private final int height;
    private final CollisionSystem collisions;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<LevelObserver> observers = new ArrayList<>();

    public Field(int width, int height) {
        this(width, height, GameCollisions.create());
    }

    public Field(int width, int height, CollisionSystem collisions) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.collisions = collisions;
    }

    public void addObstacle(Obstacle obstacle) {
        addObject(obstacle);
    }

    @Override
    public void addObject(GameObject object) {
        if (objects.contains(object)) {
            return;
        }
        objects.add(object);
        for (LevelObserver observer : new ArrayList<>(observers)) {
            observer.objectAdded(object);
        }
        resolveCollisions();
    }

    @Override
    public void removeObject(GameObject object) {
        if (objects.remove(object)) {
            for (LevelObserver observer : new ArrayList<>(observers)) {
                observer.objectRemoved(object);
            }
        }
    }

    @Override
    public boolean contains(GameObject object) {
        return objects.contains(object);
    }

    @Override
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(new ArrayList<>(objects));
    }

    @Override
    public void addObserver(LevelObserver observer) {
        if (observers.contains(observer)) {
            return;
        }
        observers.add(observer);
        for (GameObject object : new ArrayList<>(objects)) {
            if (contains(object)) {
                observer.objectAdded(object);
            }
        }
    }

    @Override
    public void removeObserver(LevelObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean canMoveTo(GridPoint2 coordinates) {
        if (!isInsideField(coordinates)) {
            return false;
        }
        for (GameObject object : objects) {
            if (object instanceof Obstacle && ((Obstacle) object).blocks(coordinates)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInsideField(GridPoint2 coordinates) {
        return coordinates.x >= 0 && coordinates.x < width
                && coordinates.y >= 0 && coordinates.y < height;
    }

    @Override
    public void resolveCollisions() {
        collisions.resolve(this);
    }

    public void live(float tick) {
        if (!Float.isFinite(tick) || tick < 0f) {
            throw new IllegalArgumentException("Tick must be finite and non-negative");
        }
        List<GameObject> snapshot = new ArrayList<>(objects);
        resolveCollisions();
        double remaining = tick;
        while (remaining > 0) {
            float step = (float) remaining;
            for (GameObject object : snapshot) {
                if (contains(object)) {
                    step = Math.min(step, object.timeToNextEvent());
                }
            }
            step = Math.max(step, MIN_TIME_STEP);
            step = (float) Math.min(remaining, step);
            for (GameObject object : snapshot) {
                if (contains(object)) {
                    object.live(step, this);
                }
            }
            resolveCollisions();
            remaining -= step;
        }
    }
}
