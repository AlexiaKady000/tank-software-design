package ru.mipt.bit.platformer.model.collision;

import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.WorldContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class CollisionSystem {
    private interface Rule {
        boolean apply(GameObject first, GameObject second, WorldContext world);
    }

    private final List<Rule> rules = new ArrayList<>();

    public <A extends GameObject, B extends GameObject> void register(
            Class<A> firstType, Class<B> secondType, BiPredicate<A, B> touches,
            CollisionHandler<A, B> handler) {
        rules.add((first, second, world) -> {
            if (firstType.isInstance(first) && secondType.isInstance(second)) {
                A a = firstType.cast(first);
                B b = secondType.cast(second);
                if (touches.test(a, b)) {
                    handler.collide(a, b, world);
                    return true;
                }
            }
            if (firstType.isInstance(second) && secondType.isInstance(first)) {
                A a = firstType.cast(second);
                B b = secondType.cast(first);
                if (touches.test(a, b)) {
                    handler.collide(a, b, world);
                    return true;
                }
            }
            return false;
        });
    }

    public void resolve(WorldContext world) {
        List<GameObject> objects = world.getObjects();
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                GameObject first = objects.get(i);
                GameObject second = objects.get(j);
                if (!world.contains(first) || !world.contains(second)
                        || !first.canCollideWith(second) || !second.canCollideWith(first)) {
                    continue;
                }
                for (Rule rule : rules) {
                    if (rule.apply(first, second, world)) {
                        break;
                    }
                }
            }
        }
    }
}
