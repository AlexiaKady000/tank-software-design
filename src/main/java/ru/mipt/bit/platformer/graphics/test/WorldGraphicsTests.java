package ru.mipt.bit.platformer.graphics.test;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.graphics.GraphicsFactory;
import ru.mipt.bit.platformer.graphics.ObjectGraphics;
import ru.mipt.bit.platformer.graphics.WorldGraphics;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorldGraphicsTest {
    private static class FakeGraphics implements ObjectGraphics {
        int updates;
        int renders;
        int disposals;

        @Override
        public void update() {
            updates++;
        }

        @Override
        public void render(Batch batch) {
            renders++;
        }

        @Override
        public void dispose() {
            disposals++;
        }
    }

    @Test
    void shouldObserveInitialAndRuntimeObjectsWithoutOpenGl() {
        Field world = new Field(5, 5);
        Tank tank = new Tank(new GridPoint2(1, 1));
        world.addObject(tank);
        List<FakeGraphics> created = new ArrayList<>();
        GraphicsFactory factory = new GraphicsFactory();
        factory.register(GameObject.class, object -> {
            FakeGraphics graphics = new FakeGraphics();
            created.add(graphics);
            return graphics;
        });
        WorldGraphics graphics = new WorldGraphics(world, factory);
        Tree tree = new Tree(new GridPoint2());
        world.addObject(tree);
        graphics.objectAdded(tree);
        assertEquals(2, created.size());
        graphics.render(null);
        assertEquals(1, created.get(0).updates);
        assertEquals(1, created.get(0).renders);
        assertEquals(1, created.get(1).renders);
        world.removeObject(tank);
        world.removeObject(tank);
        assertEquals(1, created.get(0).disposals);
        graphics.render(null);
        assertEquals(1, created.get(0).renders);
        assertEquals(2, created.get(1).renders);
        graphics.dispose();
        assertEquals(1, created.get(0).disposals);
        assertEquals(1, created.get(1).disposals);
        world.addObject(new Tank(new GridPoint2(4, 4)));
        assertEquals(2, created.size());
        graphics.dispose();
        assertEquals(1, created.get(1).disposals);
    }

    @Test
    void shouldRequireFactoryForEveryObjectType() {
        assertThrows(IllegalArgumentException.class,
                () -> new GraphicsFactory().create(new Tree(new GridPoint2())));
    }
}
