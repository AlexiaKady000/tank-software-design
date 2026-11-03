package ru.mipt.bit.platformer.input.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.input.KeyboardControlStrategy;
import ru.mipt.bit.platformer.input.RandomControlStrategy;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlStrategyTest {
    private Field world(Tank tank) {
        Field field = new Field(20, 20);
        field.addObject(tank);
        return field;
    }

    @Test
    void shouldUseCustomKeysForEveryDirection() {
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(new GridPoint2(1, 1));
            Field field = world(tank);
            Map<Integer, Direction> bindings = new LinkedHashMap<>();
            bindings.put(42, direction);
            Command command = new KeyboardControlStrategy(key -> key == 42, bindings).getCommand(tank, field);
            assertFalse(tank.isMoving());
            command.execute();
            assertEquals(direction.move(new GridPoint2(1, 1)), tank.getDestinationCoordinates());
        }
    }

    @Test
    void shouldPreserveKeyPriorityAndCopyBindings() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = world(tank);
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(1, Direction.UP);
        bindings.put(2, Direction.LEFT);
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> true, bindings);
        bindings.clear();
        strategy.getCommand(tank, field).execute();
        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    void shouldSkipUnpressedKeyAndUseAlternativeKey() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(1, Direction.LEFT);
        bindings.put(2, Direction.UP);
        bindings.put(3, Direction.UP);
        new KeyboardControlStrategy(key -> key == 3, bindings).getCommand(tank, world(tank)).execute();
        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    void shouldStayStillWithoutPressedKeysOrBindings() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = world(tank);
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(42, Direction.DOWN);
        assertSame(Command.NONE, new KeyboardControlStrategy(key -> false, bindings).getCommand(tank, field));
        assertSame(Command.NONE, new KeyboardControlStrategy(key -> true, new LinkedHashMap<>()).getCommand(tank, field));
    }

    @Test
    void shouldShootOnSpaceIncludingDuringMovement() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = world(tank);
        tank.move(Direction.RIGHT, field);
        KeyboardControlStrategy keyboard = new KeyboardControlStrategy(key -> true, new LinkedHashMap<>(), () -> true);
        assertTrue(keyboard.getCommand(tank, field) instanceof ShootCommand);
        assertSame(Command.NONE, new KeyboardControlStrategy(key -> true, new LinkedHashMap<>()).getCommand(tank, field));
    }

    @Test
    void shouldChooseRandomMovementOrShooting() {
        for (int seed = 0; seed < 100; seed++) {
            Tank tank = new Tank(new GridPoint2(5, 5));
            Field field = world(tank);
            Random expectedRandom = new Random(seed);
            expectedRandom.nextFloat();
            Direction expected = Direction.values()[expectedRandom.nextInt(Direction.values().length)];
            Command move = new RandomControlStrategy(new Random(seed), 0f).getCommand(tank, field);
            assertFalse(tank.isMoving());
            move.execute();
            assertEquals(expected, tank.getDirection());
            assertTrue(new RandomControlStrategy(new Random(seed), 1f).getCommand(tank, field) instanceof ShootCommand);
            assertSame(Command.NONE, new RandomControlStrategy(new Random(seed), 0f).getCommand(tank, field));
        }
    }

    @Test
    void shouldValidateShootingProbability() {
        for (float probability : new float[] {-1f, 2f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new RandomControlStrategy(new Random(), probability));
        }
    }
}
