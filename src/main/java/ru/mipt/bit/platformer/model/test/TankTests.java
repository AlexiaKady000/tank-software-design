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

    @Test
    void shouldStopAtAllFourBoundaries() {
        GridPoint2[] positions = {new GridPoint2(1, 2), new GridPoint2(0, 1),
                new GridPoint2(1, 0), new GridPoint2(2, 1)};
        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            Tank tank = new Tank(positions[i]);
            Field field = new Field(3, 3);
            field.addObstacle(tank);

            tank.move(directions[i], field);
            tank.update(1f);

            assertFalse(tank.isMoving());
            assertEquals(positions[i], tank.getCoordinates());
            assertEquals(positions[i], tank.getDestinationCoordinates());
            assertEquals(directions[i], tank.getDirection());
        }
    }

    @Test
    void shouldOccupyBothCellsUntilMovementCompletes() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        Field field = new Field(4, 4);
        field.addObstacle(tank);
        assertTrue(tank.blocks(new GridPoint2(1, 1)));
        assertFalse(tank.blocks(new GridPoint2(2, 1)));

        tank.move(Direction.RIGHT, field);
        tank.update(0.2f);
        assertFalse(field.canMoveTo(new GridPoint2(1, 1)));
        assertFalse(field.canMoveTo(new GridPoint2(2, 1)));
        assertTrue(field.canMoveTo(new GridPoint2(3, 1)));

        tank.update(0.2f);
        assertTrue(field.canMoveTo(new GridPoint2(1, 1)));
        assertFalse(field.canMoveTo(new GridPoint2(2, 1)));
    }

    @Test
    void shouldNotEnterStationaryTankOrSwapCells() {
        Field field = new Field(4, 4);
        Tank first = new Tank(new GridPoint2(1, 1));
        Tank second = new Tank(new GridPoint2(2, 1));
        field.addObstacle(first);
        field.addObstacle(second);

        first.move(Direction.RIGHT, field);
        second.move(Direction.LEFT, field);

        assertFalse(first.isMoving());
        assertFalse(second.isMoving());
    }

    @Test
    void shouldReserveDestinationForFirstTankOnly() {
        Field field = new Field(4, 4);
        Tank first = new Tank(new GridPoint2(0, 1));
        Tank second = new Tank(new GridPoint2(2, 1));
        field.addObstacle(first);
        field.addObstacle(second);

        first.move(Direction.RIGHT, field);
        second.move(Direction.LEFT, field);

        assertTrue(first.isMoving());
        assertFalse(second.isMoving());
        assertEquals(new GridPoint2(2, 1), second.getDestinationCoordinates());
    }

    @Test
    void shouldBlockEntryToSourceUntilDepartureCompletes() {
        Field field = new Field(4, 4);
        Tank first = new Tank(new GridPoint2(1, 1));
        Tank follower = new Tank(new GridPoint2(0, 1));
        field.addObstacle(first);
        field.addObstacle(follower);
        first.move(Direction.RIGHT, field);
        first.update(0.2f);

        follower.move(Direction.RIGHT, field);
        assertFalse(follower.isMoving());

        first.update(0.2f);
        follower.move(Direction.RIGHT, field);
        assertTrue(follower.isMoving());
        assertEquals(new GridPoint2(1, 1), follower.getDestinationCoordinates());
    }

    @Test
    void shouldNotChangeDirectionOrDestinationDuringMovement() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        tank.move(Direction.RIGHT, coordinates -> true);
        tank.update(0.1f);

        tank.move(Direction.UP, coordinates -> true);

        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(new GridPoint2(2, 1), tank.getDestinationCoordinates());
        assertEquals(0.25f, tank.getMovementProgress());
    }

    @Test
    void shouldClampLargeDeltaTimeAndIgnoreUpdatesAtRest() {
        Tank tank = new Tank(new GridPoint2(1, 1));
        tank.update(1f);
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        tank.move(Direction.UP, coordinates -> true);
        tank.update(10f);
        assertFalse(tank.isMoving());
        assertEquals(1f, tank.getMovementProgress());
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
        tank.update(1f);
        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
    }

    @Test
    void shouldCopyInitialAndReturnedCoordinates() {
        GridPoint2 initial = new GridPoint2(1, 1);
        Tank tank = new Tank(initial);
        initial.set(5, 5);
        tank.getCoordinates().set(8, 8);
        tank.getDestinationCoordinates().set(9, 9);

        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
        assertEquals(new GridPoint2(1, 1), tank.getDestinationCoordinates());
    }

}
