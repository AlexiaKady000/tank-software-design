package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import java.util.List;

public interface WorldContext extends MovementField {
    void addObject(GameObject object);
    void removeObject(GameObject object);
    boolean contains(GameObject object);
    boolean isInsideField(GridPoint2 coordinates);
    List<GameObject> getObjects();
    void resolveCollisions();
}
