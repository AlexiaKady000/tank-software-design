package ru.mipt.bit.platformer.model.collision;

import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Damageable;
import ru.mipt.bit.platformer.model.GameObject;

public final class GameCollisions {
    private GameCollisions() {
    }

    public static CollisionSystem create() {
        CollisionSystem collisions = new CollisionSystem();
        collisions.register(Bullet.class, Bullet.class,
                (first, second) -> first.occupies(second.getCoordinates())
                        || (first.getPreviousCoordinates().equals(second.getCoordinates())
                        && second.getPreviousCoordinates().equals(first.getCoordinates())),
                (first, second, world) -> {
                    world.removeObject(first);
                    world.removeObject(second);
                });
        collisions.register(Bullet.class, Damageable.class,
                (bullet, target) -> target.occupies(bullet.getCoordinates()),
                (bullet, target, world) -> {
                    world.removeObject(bullet);
                    target.damage(bullet.getDamage());
                    if (!target.isAlive()) {
                        world.removeObject(target);
                    }
                });
        collisions.register(Bullet.class, GameObject.class,
                (bullet, target) -> target.occupies(bullet.getCoordinates()),
                (bullet, target, world) -> world.removeObject(bullet));
        return collisions;
    }
}
