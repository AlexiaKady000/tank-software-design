package ru.mipt.bit.platformer.model.collision;

import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.WorldContext;

/**
 * Type - interface, role - internal rule interface
 */
public interface CollisionHandler<A extends GameObject, B extends GameObject> {
    void collide(A first, B second, WorldContext world);
}
