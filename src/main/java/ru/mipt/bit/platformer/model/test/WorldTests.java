package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.LevelObserver;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;
import ru.mipt.bit.platformer.model.WorldContext;
import ru.mipt.bit.platformer.model.collision.CollisionSystem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class WorldTest {
    private static class Counter implements GameObject {
        int calls;
        Runnable action = () -> {};

        @Override
        public boolean occupies(GridPoint2 coordinates) {
            return false;
        }

        @Override
        public void live(float tick, WorldContext world) {
            calls++;
            action.run();
        }
    }

    private static class Events implements LevelObserver {
        final List<GameObject> added = new ArrayList<>();
        final List<GameObject> removed = new ArrayList<>();

        @Override
        public void objectAdded(GameObject object) {
            added.add(object);
        }

        @Override
        public void objectRemoved(GameObject object) {
            removed.add(object);
        }
    }

    @Test
    void shouldNotifyInitialAndRuntimeMembershipChangesExactlyOnce() {
        Field world = new Field(5, 5);
        Tree tree = new Tree(new GridPoint2());
        Tank tank = new Tank(new GridPoint2(1, 1));
        world.addObject(tree);
        Events events = new Events();
        world.addObserver(events);
        world.addObserver(events);
        world.addObject(tank);
        world.addObject(tank);
        world.removeObject(tank);
        world.removeObject(tank);
        assertEquals(2, events.added.size());
        assertSame(tree, events.added.get(0));
        assertEquals(1, events.removed.size());
        world.removeObserver(events);
        world.removeObject(tree);
        assertEquals(1, events.removed.size());
    }

    @Test
    void shouldPublishTransientBulletAddedAndRemovedEventsOnAdjacentHit() {
        Field world = new Field(5, 5);
        Tank shooter = new Tank(new GridPoint2(1, 1));
        Tank target = new Tank(new GridPoint2(2, 1));
        world.addObject(shooter);
        world.addObject(target);
        Events events = new Events();
        world.addObserver(events);
        shooter.shoot(world);
        assertEquals(3, events.added.size());
        assertEquals(1, events.removed.size());
        assertSame(events.added.get(2), events.removed.get(0));
    }

    @Test
    void shouldAllowRemovingObjectsDuringTheirLife() {
        Field world = new Field(5, 5);
        Counter first = new Counter();
        Counter second = new Counter();
        first.action = () -> world.removeObject(second);
        world.addObject(first);
        world.addObject(second);
        world.live(1f);
        assertEquals(1, first.calls);
        assertEquals(0, second.calls);
    }

    @Test
    void shouldStartNewObjectsOnNextTick() {
        Field world = new Field(5, 5);
        Counter parent = new Counter();
        Counter child = new Counter();
        parent.action = () -> world.addObject(child);
        world.addObject(parent);
        world.live(1f);
        assertTrue(world.contains(child));
        assertEquals(0, child.calls);
        world.live(1f);
        assertEquals(1, child.calls);
    }

    @Test
    void shouldExposeReadOnlySnapshot() {
        Field world = new Field(5, 5);
        List<GameObject> snapshot = world.getObjects();
        world.addObject(new Tree(new GridPoint2()));
        assertTrue(snapshot.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> world.getObjects().clear());
    }

    @Test
    void shouldValidateTickAndDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new Field(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Field(1, 0));
        Field world = new Field(5, 5);
        for (float tick : new float[] {-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> world.live(tick));
        }
        Counter counter = new Counter();
        world.addObject(counter);
        world.live(0f);
        assertEquals(0, counter.calls);
    }

    @Test
    void shouldSupportCollisionRulesWithoutChangingWorld() {
        CollisionSystem collisions = new CollisionSystem();
        collisions.register(Counter.class, Tree.class, (first, second) -> true,
                (first, second, world) -> world.removeObject(first));
        Field world = new Field(5, 5, collisions);
        Tree tree = new Tree(new GridPoint2());
        world.addObject(tree);
        Counter object = new Counter();
        world.addObject(object);
        assertFalse(world.contains(object));
        assertTrue(world.contains(tree));
    }

    @Test
    void shouldAllowObserverToUnsubscribeDuringNotification() {
        Field world = new Field(5, 5);
        world.addObserver(new LevelObserver() {

            @Override
            public void objectAdded(GameObject object) {
                world.removeObserver(this);
            }

            @Override
            public void objectRemoved(GameObject object) {
                fail("Observer was not removed");
            }
        });
        Tree tree = new Tree(new GridPoint2());
        world.addObject(tree);
        world.removeObject(tree);
    }
}
