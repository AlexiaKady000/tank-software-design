package ru.mipt.bit.platformer.input.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.input.KeyboardControlStrategy;
import ru.mipt.bit.platformer.input.RandomControlStrategy;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlStrategyTest {
    @Test
    void shouldUseCustomKeysForEveryDirection() {
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(new GridPoint2(1, 1));
            Map<Integer, Direction> bindings = new LinkedHashMap<>();
            bindings.put(42, direction);
            KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> key == 42, bindings);

            Command command = strategy.getCommand(tank, coordinates -> true);
            assertFalse(tank.isMoving());
            command.execute();

            assertEquals(direction.move(new GridPoint2(1, 1)), tank.getDestinationCoordinates());
        }
    }

    @Test
    void shouldPreserveKeyPriorityAndCopyBindings() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(1, Direction.UP);
        bindings.put(2, Direction.LEFT);
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> true, bindings);
        bindings.clear();

        strategy.getCommand(tank, coordinates -> true).execute();

        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
    }

    @Test
    void shouldSkipUnpressedKeyAndUseAlternativeKey() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(1, Direction.LEFT);
        bindings.put(2, Direction.UP);
        bindings.put(3, Direction.UP);
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> key == 3, bindings);

        strategy.getCommand(tank, coordinates -> true).execute();

        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    void shouldStayStillWithoutPressedKeys() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(42, Direction.DOWN);
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> false, bindings);

        assertSame(Command.NONE, strategy.getCommand(tank, coordinates -> true));
        strategy.getCommand(tank, coordinates -> true).execute();
        assertFalse(tank.isMoving());
    }

    @Test
    void shouldStayStillWithEmptyBindings() {
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> true, new LinkedHashMap<>());
        assertSame(Command.NONE, strategy.getCommand(new Tank(new GridPoint2()), coordinates -> true));
    }

    @Test
    void shouldChooseRandomDirectionAndReturnDeferredCommand() {
        for (int seed = 0; seed < 100; seed++) {
            Tank tank = new Tank(new GridPoint2(1, 1));
            Direction expected = Direction.values()[new Random(seed).nextInt(Direction.values().length)];
            RandomControlStrategy strategy = new RandomControlStrategy(new Random(seed));

            Command command = strategy.getCommand(tank, coordinates -> true);
            assertFalse(tank.isMoving());
            command.execute();

            assertEquals(expected, tank.getDirection());
            assertEquals(expected.move(new GridPoint2(1, 1)), tank.getDestinationCoordinates());
        }
    }

    @Test
    void shouldKeepPlayerStillWhileAiMoves() {
        Tank player = new Tank(new GridPoint2());
        Tank ai = new Tank(new GridPoint2(5, 5));
        KeyboardControlStrategy keyboard = new KeyboardControlStrategy(key -> false, new LinkedHashMap<>());

        keyboard.getCommand(player, coordinates -> true).execute();
        new RandomControlStrategy(new Random(1)).getCommand(ai, coordinates -> true).execute();

        assertFalse(player.isMoving());
        assertTrue(ai.isMoving());
    }
}
