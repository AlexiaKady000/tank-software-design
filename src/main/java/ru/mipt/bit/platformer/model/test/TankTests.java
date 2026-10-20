package ru.mipt.bit.platformer.model.test;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;
import ru.mipt.bit.platformer.model.Direction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankTest {
    @Test
    void shouldStartAtInitialCoordinates() {
        Tank tank = new Tank(new GridPoint2(1, 1));

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    void shouldStartMovingToEmptyCell() {
        Field field = new Field(10, 8);
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, field);

        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
    }

    @Test
    void shouldNotMoveIntoTree() {
        Field field = new Field(10, 8);
        field.addObstacle(new Tree(new GridPoint2(1, 2)));

        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, field);

        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }

    @Test
    void shouldReachDestination() {
        Field field = new Field(10, 8);
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.RIGHT, field);
        tank.update(0.4f);

        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    void shouldUseMovementFieldAbstraction() {
        Tank tank = new Tank(new GridPoint2(1, 1));

        tank.move(Direction.UP, coordinates -> false);
        assertFalse(tank.isMoving());

        tank.move(Direction.UP, coordinates -> true);
        assertTrue(tank.isMoving());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
    }

    @Test
    void shouldUseConfiguredMovementDuration() {
        Tank tank = new Tank(new GridPoint2(1, 1), 0.8f);
        tank.move(Direction.RIGHT, coordinates -> true);

        tank.update(0.4f);
        assertEquals(0.5f, tank.getMovementProgress());
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());

        tank.update(0.4f);
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
    }

    @Test
    void shouldRejectInvalidMovementDuration() {
        for (float duration : new float[] {0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                    () -> new Tank(new GridPoint2(1, 1), duration));
        }
    }
}
